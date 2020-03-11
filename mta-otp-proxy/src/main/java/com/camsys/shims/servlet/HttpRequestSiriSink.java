package com.camsys.shims.servlet;

import com.camsys.shims.util.source.MergingSiriSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;
import uk.org.siri.siri.Siri;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Serve merged SIRI via collector to support legacy GMS integrations.
 */
public class HttpRequestSiriSink implements HttpRequestHandler {

    private static Logger _log = LoggerFactory.getLogger(HttpRequestSiriSink.class);

    private MergingSiriSource _siriSource;
    private JAXBContext _context = null;

    public void setSource(MergingSiriSource siriSource) {
        _siriSource = siriSource;
    }

    @Override
    public void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        if (_siriSource == null)
            httpServletResponse.getWriter().print("");
        try {
            httpServletResponse.getWriter().print(getXml(_siriSource.getFeed()));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public String getXml(Siri siri) throws Exception {
        Marshaller marshaller = this.getContext().createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", false);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setEventHandler(new ValidationEventHandler() {
            public boolean handleEvent(ValidationEvent event) {
                throw new RuntimeException(event.getMessage(), event.getLinkedException());
            }
        });
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Writer output = new StringWriter();
        marshaller.marshal(siri, output);
        String outputAsString = output.toString();
        // here is our chance to do any nasty hacks to the XML
//        outputAsString = outputAsString.replaceAll("<Siri>", "<Siri xmlns:ns2=\"http://www.ifopt.org.uk/acsb\" xmlns=\"http://www.siri.org.uk/siri\" xmlns:ns4=\"http://datex2.eu/schema/1_0/1_0\" xmlns:ns3=\"http://www.ifopt.org.uk/ifopt\">");
//        outputAsString = outputAsString.replaceAll("<Summary>", "<Summary xml:lang=\"EN\">");
//        outputAsString = outputAsString.replaceAll("<Description>", "<Description xml:lang=\"EN\">");
//        .replace("<Description", "<LegacyDescription")
//                .replace("</Description>", "</LegacyDescription>")
        outputAsString = outputAsString
                .replace("<Description xml:lang=\"EN\">", "<LongDescription>")
                .replace("</Description>", "</LongDescription>")
                .replace("<Priority>", "<MessagePriority>")
                .replace("</Priority>", "</MessagePriority>");
        return outputAsString;
    }

    private JAXBContext getContext() {
        if (_context == null) {
            try {
                _context = JAXBContext.newInstance(new Class[]{Siri.class});
            } catch (Exception e) {
                _log.error("failure creating jaxb context:", e);
            }
        }
        return _context;
    }

}
