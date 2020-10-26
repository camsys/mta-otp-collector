package com.camsys.shims.servlet;

import com.camsys.shims.util.HtmlCleanupUtil;
import com.camsys.shims.util.source.MergingSiriSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;
import uk.org.siri.siri.DefaultedTextStructure;
import uk.org.siri.siri.PtConsequenceStructure;
import uk.org.siri.siri.PtSituationElementStructure;
import uk.org.siri.siri.ServiceConditionEnumeration;
import uk.org.siri.siri.ServiceDelivery;
import uk.org.siri.siri.Siri;
import uk.org.siri.siri.SituationExchangeDeliveryStructure;

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
    private static final String GMS_TYPE = "gms";
    private static final String CIS_TYPE = "cis";

    private MergingSiriSource _siriSource;
    private JAXBContext _context = null;
    private String _type = GMS_TYPE;
    private HtmlCleanupUtil _htmlCleanupUtil;

    public void setHtmlCleanupUtil(HtmlCleanupUtil htmlCleanupUtil) {
        _htmlCleanupUtil = htmlCleanupUtil;
    }

    public void setSource(MergingSiriSource siriSource) {
        _siriSource = siriSource;
    }

    public void setType(String type) {
        _type = type;
    }

    @Override
    public void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

        if (_siriSource == null || _siriSource.getFeed() == null) {
            httpServletResponse.getWriter().print("<Siri xmlns:ns2=\"http://www.ifopt.org.uk/acsb\" xmlns=\"http://www.siri.org.uk/siri\" xmlns:ns4=\"http://datex2.eu/schema/1_0/1_0\" xmlns:ns3=\"http://www.ifopt.org.uk/ifopt\"></Siri>");
            return;
        }

        try {
            httpServletResponse.getWriter().print(getXml(filterSiri(_siriSource.getFeed()), CIS_TYPE.equals(_type)));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public Siri filterSiri(Siri siri) {
        if (siri.getServiceDelivery() != null) {
            ServiceDelivery sd = siri.getServiceDelivery();
            if (sd.getSituationExchangeDelivery() != null) {
                for (SituationExchangeDeliveryStructure seds : sd.getSituationExchangeDelivery()) {
                    if (seds.getSituations() != null) {
                        SituationExchangeDeliveryStructure.Situations s = seds.getSituations();
                        if (s.getPtSituationElement() != null) {
                            for (PtSituationElementStructure pt : s.getPtSituationElement()) {
                                if (pt.getSummary() != null) {
                                    DefaultedTextStructure txt = pt.getSummary();
                                    String html = txt.getValue();
                                    String cleanedHtml = _htmlCleanupUtil.filterAndBlacklist(html);
                                    txt.setValue(cleanedHtml);
                                }
                                if (pt.getDescription() != null) {
                                    DefaultedTextStructure txt = pt.getDescription();
                                    String html = txt.getValue();
                                    String cleanedHtml = _htmlCleanupUtil.filterAndBlacklist(html);
                                    txt.setValue(cleanedHtml);
                                }
                                if (pt.getConsequences() != null) {
                                    if (pt.getConsequences().getConsequence() != null) {
                                        for (PtConsequenceStructure consequence : pt.getConsequences().getConsequence()) {
                                            if (consequence.getCondition() == null) {
                                                /*
                                                 * in an effort to make output as similar as possible we output a sentinel
                                                 * that will later be replaced with <Condition/>
                                                 */
                                                consequence.setCondition(ServiceConditionEnumeration.PTI_13_255);
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return siri;
    }

    public String getXml(Siri siri, boolean isBuscis) throws Exception {
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

        if (isBuscis)
            return formatForBusCIS(outputAsString);
        return formatForGMS(outputAsString);
    }
    private String formatForGMS(String outputAsString) {
        // the GMS feed deviates from SIRI in some cases.  Make those changes below
        outputAsString = outputAsString.replaceAll("(<Summary xml:lang=\"EN\">(.*)</Summary>)",
                "$1\012                    <TmpDescription>$2</TmpDescription>");  // make it look like it belongs
        outputAsString = outputAsString
                .replace("<Description>", "<LongDescription>")
                .replace("<Description xml:lang=\"EN\">", "<LongDescription>")
                .replace("</Description>", "</LongDescription>")
                .replace("<TmpDescription>", "<Description xml:lang=\"EN\">")
                .replace("</TmpDescription>", "</Description>")
                .replace("<Priority>", "<MessagePriority>")
                .replace("</Priority>", "</MessagePriority>")
                /* make it look exactly like GMS */
                .replace("<Condition>pti13_255</Condition>", "<Condition/>");
        return outputAsString;

    }

    private String formatForBusCIS(String outputAsString) {
        outputAsString = outputAsString.replace("<Condition>pti13_255</Condition>", "<Condition/>");
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
