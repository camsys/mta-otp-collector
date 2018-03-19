package com.camsys.shims.util.deserializer;

import com.amazonaws.util.IOUtils;
import com.camsys.mta.plannedwork.Getstatus4ResponseType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

public class PlannedServiceAlertDeserializer implements Deserializer<Getstatus4ResponseType> {

    private Unmarshaller _um;

    public PlannedServiceAlertDeserializer() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Getstatus4ResponseType.class);
        _um = jc.createUnmarshaller();
    }

    @Override
    public Getstatus4ResponseType deserialize(InputStream inputStream) throws IOException {
        try {
            JAXBElement<Getstatus4ResponseType> nycResponse;

            SOAPMessage message = MessageFactory.newInstance().createMessage(null, inputStream);
            nycResponse = _um.unmarshal(message.getSOAPBody().extractContentAsDocument(), Getstatus4ResponseType.class);



            Getstatus4ResponseType o = nycResponse.getValue();
            if (o.getResponsecode().equals("0")) {
                return o;
            } else {
                return null;
            }
        } catch(JAXBException je) {
            throw new IOException(je);
        }catch (Exception e)
        {
            throw new IOException(e);
        }
    }

    @Override
    public String getMimeType() {
        return "text/xml";
    }

}