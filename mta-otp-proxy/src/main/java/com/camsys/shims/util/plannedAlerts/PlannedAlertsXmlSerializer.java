package com.camsys.shims.util.plannedAlerts;

import org.onebusaway.nyc.siri.support.SiriDistanceExtension;
import org.onebusaway.nyc.siri.support.SiriExtensionWrapper;
import uk.org.siri.siri.Siri;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class PlannedAlertsXmlSerializer {

    private JAXBContext context = null;

    public PlannedAlertsXmlSerializer() {
        try {
            this.context = JAXBContext.newInstance(Siri.class, SiriExtensionWrapper.class, SiriDistanceExtension.class);
        } catch (Exception var2) {
            ;
        }

    }

    public String getXml(PlannedServiceAlert plannedAlert) throws Exception {
        throw new Exception("Planned Alert to XML has not yet been implemented");
    }

    public PlannedServiceAlert fromXml(String xml) throws JAXBException {
        Unmarshaller u = this.context.createUnmarshaller();
        PlannedServiceAlert plannedServiceAlert = (PlannedServiceAlert)u.unmarshal(new StringReader(xml));
        return plannedServiceAlert;
    }
}
