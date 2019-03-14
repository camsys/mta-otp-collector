package com.camsys.shims.util.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.util.EC2MetadataUtils;
import com.kurtraschke.nyctrtproxy.services.CloudwatchProxyDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by lcaraballo on 5/3/18.
 */
public class CloudWatchService extends CloudwatchProxyDataListener {

    private static final Logger _log = LoggerFactory.getLogger(CloudWatchService.class);

    private ScheduledExecutorService _scheduledExecutorService = null;

    private boolean _primary = false;

    private String _autoScalingGroupName;

    public void setAutoScalingGroupName(String autoScalingGroupName) {
        _autoScalingGroupName = autoScalingGroupName;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !_primary;
    }

    @PostConstruct
    public void init() {
        try {
            super.init();
            if(_autoScalingGroupName != null && !_autoScalingGroupName.equalsIgnoreCase("none") && !_env.equalsIgnoreCase("local")) {
                scheduleLeadershipElection();
            }
            else{
                _primary = true;
            }
        } catch(Exception e){
            _log.warn("Unable to connect to CloudWatch", e);
            _disabled = true;
        }
    }

    private void scheduleLeadershipElection(){
        try {
           if(EC2MetadataUtils.getInstanceInfo() != null) {
                if(_scheduledExecutorService != null) {
                    _scheduledExecutorService.shutdownNow();
                    Thread.sleep(1 * 1000);
                }
                _scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                _scheduledExecutorService.scheduleAtFixedRate(new LeadershipElectionTask(), 1, 1, TimeUnit.DAYS.MINUTES);
            } else{
                throw new Exception("Unable to get Metadata for AWS Instance");
            }
        } catch (Exception e){
            _log.warn("Unable to connect to AWS Instance.", e);
            _primary = false;
        }
    }

    @PreDestroy
    public void destroy(){
        _scheduledExecutorService.shutdownNow();
    }

    private class LeadershipElectionTask implements Runnable {
        private AmazonAutoScaling _autoScale;
        private AmazonEC2 _ec2;
        private List<Filter> describeFilters = new ArrayList<>();


        public LeadershipElectionTask(){
            try {
                BasicAWSCredentials cred = new BasicAWSCredentials(_accessKey, _secretKey);
                _ec2 = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(cred)).build();
                _autoScale = AmazonAutoScalingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(cred)).build();
                describeFilters.add(new Filter("aws:autoscaling:groupName"));
            } catch(Exception e){
                _log.warn("Unable to create AWS Clients", e);
                _disabled = true;
                _scheduledExecutorService.shutdownNow();
            }
        }

        public void run() {
            AutoScalingGroup autoScalingGroup = getAutoScalingGroups();

            if(autoScalingGroup != null) {

                String oldestInstance = null;
                Date oldestInstanceLaunchTime = new Date();

                List<String> instanceIds = autoScalingGroup.getInstances().stream()
                        .map(com.amazonaws.services.autoscaling.model.Instance::getInstanceId)
                        .collect(Collectors.toList());

                List<Instance> instances = getInstances(instanceIds);

                for (Instance instance : instances) {
                    if (instance.getLaunchTime().before(oldestInstanceLaunchTime)) {
                        oldestInstanceLaunchTime = instance.getLaunchTime();
                        oldestInstance = instance.getInstanceId();
                    }
                }

                if (oldestInstance != null && oldestInstance.equals(EC2MetadataUtils.getInstanceId())) {
                    _log.warn("This is the primary instance.");
                    _primary = true;
                } else {
                    _log.warn("This is not the primary instance. Oldest Instance Id is {}, this Instance Id is {}", oldestInstance, EC2MetadataUtils.getInstanceId());
                    _primary = false;
                }
            } else {
                _log.warn("Not the primary instance, no autoScaling group found.");
                _primary = false;
            }
        }

        private AutoScalingGroup getAutoScalingGroups(){

            DescribeTagsRequest describeTagsRequest = new DescribeTagsRequest().withFilters(describeFilters);
            DescribeTagsResult describeTagsResult = _ec2.describeTags(describeTagsRequest);
            if(describeTagsResult.getTags().size() > 0) {
                DescribeAutoScalingGroupsResult result = _autoScale.describeAutoScalingGroups(
                        new DescribeAutoScalingGroupsRequest());
                return result.getAutoScalingGroups().stream()
                        .filter(group -> group.getAutoScalingGroupName().startsWith(_autoScalingGroupName))
                        .findFirst().orElse(null);
            }
            return null;
        }

        private List<Instance> getInstances(List<String> instanceIds){
            try {
                DescribeInstancesRequest request = new DescribeInstancesRequest();
                request.setInstanceIds(instanceIds);
                DescribeInstancesResult result = _ec2.describeInstances(request);
                List<Instance> instances = new ArrayList();
                for (Reservation reservation : result.getReservations()) {
                    instances.addAll(reservation.getInstances());
                }
                return instances;
            } catch (Exception e){
                _log.error("Unable to retreive instances", e);
                return Collections.EMPTY_LIST;
            }
        }
    }


}
