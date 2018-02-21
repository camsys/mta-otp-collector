package com.camsys.shims.service_status.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class StatusDetail {

    private String statusSummary;

    private String statusDescription;

    private BigInteger priority;

    private String direction;

    private String creationDate;

    private String startDate;

    private String endDate;

    @JsonProperty("statusSummary")
    public String getStatusSummary() {
        return statusSummary;
    }

    public void setStatusSummary(String statusSummary) {
        this.statusSummary = statusSummary;
    }

    @JsonProperty("statusDescription")
    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @JsonProperty("priority")
    public BigInteger getPriority() {
        return priority;
    }

    public void setPriority(BigInteger priority) {
        this.priority = priority;
    }

    @JsonProperty("direction")
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @JsonProperty("creationDate")
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    @JsonProperty("startDate")
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @JsonProperty("endDate")
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
