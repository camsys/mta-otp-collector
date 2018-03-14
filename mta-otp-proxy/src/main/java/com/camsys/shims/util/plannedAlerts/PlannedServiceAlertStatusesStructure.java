package com.camsys.shims.util.plannedAlerts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Status",
        propOrder = {"title", "statustype", "id", "begins", "expires", "lastupdate", "affects", "document", "texts", "categories", "summary", "canceltext"}
)
public class PlannedServiceAlertStatusesStructure {
    @XmlElement(
            name = "Title",
            defaultValue = "true"
    )
    protected String title;

    @XmlElement(
            name = "Statustype",
            defaultValue = "true"
    )
    protected String statusType;

    @XmlElement(
            name = "Id",
            defaultValue = "true"
    )
    protected int statusId;

    @XmlElement(
            name = "Begins",
            defaultValue = "true"
    )
    protected String begins;

    @XmlElement(
            name = "Expires",
            defaultValue = "true"
    )
    protected String expires;

    @XmlElement(
            name = "Lastupdate",
            defaultValue = "true"
    )
    protected String lastUpdate;

    @XmlElement(
            name = "Afects",
            defaultValue = "true"
    )
    protected String affects;

    @XmlElement(
            name = "Document",
            defaultValue = "true"
    )
    protected String document;

    @XmlElement(
            name = "Texts",
            defaultValue = "true"
    )
    protected List<String> texts;

    @XmlElement(
            name = "Categories",
            defaultValue = "true"
    )
    protected String categories;


    @XmlElement(
            name = "Summary",
            defaultValue = "true"
    )
    protected String summary;

    @XmlElement(
            name = "Canceltext",
            defaultValue = "true"
    )
    protected String cancelText;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getBegins() {
        return begins;
    }

    public void setBegins(String begins) {
        this.begins = begins;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getAffects() {
        return affects;
    }

    public void setAffects(String affects) {
        this.affects = affects;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCancelText() {
        return cancelText;
    }

    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

}
