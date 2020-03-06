package com.camsys.shims.gtfsrt.alerts.siri.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Source {

    @XmlElement(name = "SourceType")
    private String sourceType;

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    public String getSourceType() {
        return sourceType;
    }
}
