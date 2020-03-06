package com.camsys.shims.gtfsrt.alerts.siri.model;

import javax.xml.bind.annotation.XmlElement;

public class DefaultedText {

    @XmlElement(name = "Value")
    private String _value;
    public String getValue() {
        return _value;
    }
    public void setValue(String value) {
        _value = value;
    }
}
