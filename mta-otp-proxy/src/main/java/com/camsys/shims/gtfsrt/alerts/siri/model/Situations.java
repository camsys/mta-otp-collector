package com.camsys.shims.gtfsrt.alerts.siri.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Situations {

    @XmlElement(name = "PtSituationElement")
    private List<PtSituationElement> ptSituationElement = new ArrayList<PtSituationElement>();

    public List<PtSituationElement> getPtSituationElement() {
        return ptSituationElement;
    }

    public void setPtSituationElement(List<PtSituationElement> ptSituationElement) {
        this.ptSituationElement = ptSituationElement;
    }
}
