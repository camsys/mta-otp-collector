package com.camsys.shims.service_status.deserializer;

import com.amazonaws.util.IOUtils;
import com.camsys.mta.gms_service_status.Service;
import com.camsys.shims.util.deserializer.Deserializer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import uk.org.siri.siri.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Created by lcaraballo on 4/5/18.
 */
public class GmsServiceStatusDeserializer implements Deserializer<Service> {

    @Override
    public Service deserialize(InputStream inputStream) throws IOException {
        String xml = IOUtils.toString(inputStream);
        return deserialize(xml);
    }

    protected Service deserialize(String xml) throws IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(Service.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            Service service = (Service) unmarshaller.unmarshal(reader);
            return service;
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
