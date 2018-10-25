package com.camsys.shims.service_status.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.Date;

/**
 * <p>StatusDetail class.</p>
 *
 */
public class StatusDetail {

    private String statusSummary;

    private String statusDescription;

    private BigInteger priority;

    private String direction;

    private Date creationDate;

    private Date startDate;

    private Date endDate;

    /**
     * <p>Getter for the field <code>statusSummary</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonProperty("statusSummary")
    public String getStatusSummary() {
        return statusSummary;
    }

    /**
     * <p>Setter for the field <code>statusSummary</code>.</p>
     *
     * @param statusSummary a {@link java.lang.String} object.
     */
    public void setStatusSummary(String statusSummary) {
        this.statusSummary = statusSummary;
    }

    /**
     * <p>Getter for the field <code>statusDescription</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonProperty("statusDescription")
    public String getStatusDescription() {
        return statusDescription;
    }

    /**
     * <p>Setter for the field <code>statusDescription</code>.</p>
     *
     * @param statusDescription a {@link java.lang.String} object.
     */
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    /**
     * <p>Getter for the field <code>priority</code>.</p>
     *
     * @return a {@link java.math.BigInteger} object.
     */
    @JsonProperty("priority")
    public BigInteger getPriority() {
        return priority;
    }

    /**
     * <p>Setter for the field <code>priority</code>.</p>
     *
     * @param priority a {@link java.math.BigInteger} object.
     */
    public void setPriority(BigInteger priority) {
        this.priority = priority;
    }

    /**
     * <p>Getter for the field <code>direction</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonProperty("direction")
    public String getDirection() {
        return direction;
    }

    /**
     * <p>Setter for the field <code>direction</code>.</p>
     *
     * @param direction a {@link java.lang.String} object.
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * <p>Getter for the field <code>creationDate</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    @JsonProperty("creationDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "America/New_York")
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * <p>Setter for the field <code>creationDate</code>.</p>
     *
     * @param creationDate a {@link java.util.Date} object.
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * <p>Getter for the field <code>startDate</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    @JsonProperty("startDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "America/New_York")
    public Date getStartDate() {
        return startDate;
    }

    /**
     * <p>Setter for the field <code>startDate</code>.</p>
     *
     * @param startDate a {@link java.util.Date} object.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * <p>Getter for the field <code>endDate</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    @JsonProperty("endDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "America/New_York")
    public Date getEndDate() {
        return endDate;
    }

    /**
     * <p>Setter for the field <code>endDate</code>.</p>
     *
     * @param endDate a {@link java.util.Date} object.
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatusDetail that = (StatusDetail) o;

        if (statusSummary != null ? !statusSummary.equals(that.statusSummary) : that.statusSummary != null)
            return false;
        if (statusDescription != null ? !statusDescription.equals(that.statusDescription) : that.statusDescription != null)
            return false;
        if (priority != null ? !priority.equals(that.priority) : that.priority != null) return false;
        if (direction != null ? !direction.equals(that.direction) : that.direction != null) return false;
        if (creationDate != null ? !creationDate.equals(that.creationDate) : that.creationDate != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        return endDate != null ? endDate.equals(that.endDate) : that.endDate == null;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = statusSummary != null ? statusSummary.hashCode() : 0;
        result = 31 * result + (statusDescription != null ? statusDescription.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }
}
