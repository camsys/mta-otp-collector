package com.camsys.shims.util.deserializer;

import com.amazonaws.util.IOUtils;
import com.camsys.shims.util.deserializer.Deserializer;
import com.camsys.shims.util.plannedAlerts.PlannedServiceAlert;
import com.camsys.shims.util.plannedAlerts.PlannedAlertsXmlSerializer;
import org.onebusaway.nyc.siri.support.SiriXmlSerializer;
import uk.org.siri.siri.Siri;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;

public class PlannedServiceAlertDeserializer implements Deserializer<PlannedServiceAlert> {

    private PlannedAlertsXmlSerializer _plannedAlertsXmlSerializer = new PlannedAlertsXmlSerializer();

    @Override
    public PlannedServiceAlert deserialize(InputStream inputStream) throws IOException {
        String xml = IOUtils.toString(inputStream);

        PlannedServiceAlert test = deserialize(xml);

        System.out.println(test.getGetstatus4ResponseDeliveryStructure().getRoutesStructure().size());
        System.out.println(test.getGetstatus4ResponseDeliveryStructure().getStatusesStructure().size());
        System.out.println(test.getGetstatus4ResponseDeliveryStructure().getStopsStructure().size());

        return deserialize(xml);
    }

    protected PlannedServiceAlert deserialize(String xml) throws IOException {
        try {
            return _plannedAlertsXmlSerializer.fromXml(xml);
        } catch(JAXBException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    @Override
    public String getMimeType() {
        return "text/xml";
    }

}
