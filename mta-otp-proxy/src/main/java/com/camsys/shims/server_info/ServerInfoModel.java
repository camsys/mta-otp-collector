package com.camsys.shims.server_info;
import java.util.Properties;
import java.io.InputStream;

public class ServerInfoModel {

    public String gitCommitId;
    public String gitBuildTime;
    public String gitCommitTime;
    public String projectVersion;
    public String gitCommitIdDescribe;

    public ServerInfoModel() {
        final String FILE = "maven-version.properties";
        try {
            Properties props = new java.util.Properties();
            InputStream in = ServerInfoModel.class.getClassLoader().getResourceAsStream(FILE);
            props.load(in);
            this.gitCommitId = props.getProperty("git.commit.id");
            this.gitBuildTime = props.getProperty("git.build.time");
            this.gitCommitTime = props.getProperty("git.commit.time");
            this.projectVersion = props.getProperty("project.version");
            this.gitCommitIdDescribe = props.getProperty("git.commit.id.describe");

        } catch (Exception e) {
            this.gitCommitId = "UNKNOWN";
            this.gitBuildTime = "UNKNOWN";
            this.gitCommitTime = "UNKNOWN";
            this.projectVersion = "UNKNOWN";
            this.gitCommitIdDescribe = "UNKNOWN";
        }
    }
}

