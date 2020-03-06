package com.camsys.shims.gtfsrt.alerts.siri.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PtSituationElement", propOrder = {
        "creationTime",
        "situationNumber",
        "publicationWindow",
        "summary",
        "description",
        "longDescription",
        "planned",
        "reasonName",
        "messagePriority",
        "source",
        "affects",
        "consequences"
})
public class PtSituationElement {

    @XmlElement(name = "CreationTime")
    private Date creationTime;
    @XmlElement(name = "SituationNumber")
    private String situationNumber;
    @XmlElement(name = "PublicationWindow")
    private PublicationWindow publicationWindow;
    @XmlElement(name = "Summary")
    private String summary;
    @XmlElement(name = "Description")
    private String description;
    @XmlElement(name = "LongDescription")
    private String longDescription;
    @XmlElement(name = "Planned")
    private Boolean planned;
    @XmlElement(name = "ReasonName")
    private String reasonName;
    @XmlElement(name = "MessagePriority")
    private int messagePriority;
    @XmlElement(name = "Source")
    private Source source;
    @XmlElement(name = "Affects")
    private List<Affects> affects = new ArrayList<Affects>();
    @XmlElement(name = "Consequences")
    private List<Consequence> consequences = new ArrayList<Consequence>();

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getSituationNumber() {
        return situationNumber;
    }

    public void setSituationNumber(String situationNumber) {
        this.situationNumber = situationNumber;
    }

    public PublicationWindow getPublicationWindow() {
        return publicationWindow;
    }

    public void setPublicationWindow(PublicationWindow publicationWindow) {
        this.publicationWindow = publicationWindow;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String _longDescription) {
        this.longDescription = _longDescription;
    }

    public Boolean getPlanned() {
        return planned;
    }

    public void setPlanned(Boolean planned) {
        this.planned = planned;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }

    public int getMessagePriority() {
        return messagePriority;
    }

    public void setMessagePriority(int messagePriority) {
        this.messagePriority = messagePriority;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public List<Affects> getAffects() {
        return affects;
    }

    public void setAffects(List<Affects> affects) {
        this.affects = affects;
    }

    public List<Consequence> getConsequences() {
        return consequences;
    }

    public void setConsequences(List<Consequence> consequences) {
        this.consequences = consequences;
    }


}
