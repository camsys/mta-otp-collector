package com.camsys.shims.servlet;

import com.camsys.shims.util.HtmlCleanupUtil;
import com.camsys.shims.util.source.MergingSiriSource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import uk.org.siri.siri.DefaultedTextStructure;
import uk.org.siri.siri.PtSituationElementStructure;
import uk.org.siri.siri.ServiceDelivery;
import uk.org.siri.siri.Siri;
import uk.org.siri.siri.SituationExchangeDeliveryStructure;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

/**
 * OBANYC-4185: Bus Time ingests this feed, and alerts pasted from Word (e.g. U+2019 ’) were
 * served as '?' because the response had no charset and defaulted to ISO-8859-1.
 */
public class HttpRequestSiriSinkTest {

    private static final String ALERT_TEXT =
            "We’re running “as much” service — as we can…";

    private HttpRequestSiriSink sink;

    @Before
    public void setUp() {
        sink = new HttpRequestSiriSink();
        sink.setType("cis");
        // identity cleanup so the test only exercises encoding
        sink.setHtmlCleanupUtil(new HtmlCleanupUtil() {
            @Override
            public String filterAndBlacklist(String html) {
                return html;
            }
        });
    }

    @Test
    public void servesNonLatin1AlertTextAsUtf8() throws Exception {
        sink.setSource(sourceWith(ALERT_TEXT));
        MockHttpServletResponse response = new MockHttpServletResponse();

        sink.handleRequest(new MockHttpServletRequest(), response);

        String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        assertTrue("alert text should survive intact but was: " + body, body.contains(ALERT_TEXT));
        String contentType = response.getHeader("Content-Type");
        assertTrue("Content-Type should declare UTF-8 but was " + contentType,
                contentType != null && contentType.contains("charset=UTF-8"));
    }

    @Test
    public void emptyFeedStillDeclaresUtf8() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        sink.handleRequest(new MockHttpServletRequest(), response);

        String contentType = response.getHeader("Content-Type");
        assertTrue("Content-Type should declare UTF-8 but was " + contentType,
                contentType != null && contentType.contains("charset=UTF-8"));
    }

    private static MergingSiriSource sourceWith(String summary) {
        DefaultedTextStructure text = new DefaultedTextStructure();
        text.setValue(summary);
        PtSituationElementStructure situation = new PtSituationElementStructure();
        situation.setSummary(text);
        SituationExchangeDeliveryStructure.Situations situations = new SituationExchangeDeliveryStructure.Situations();
        situations.getPtSituationElement().add(situation);
        SituationExchangeDeliveryStructure delivery = new SituationExchangeDeliveryStructure();
        delivery.setSituations(situations);
        ServiceDelivery serviceDelivery = new ServiceDelivery();
        serviceDelivery.getSituationExchangeDelivery().add(delivery);
        Siri siri = new Siri();
        siri.setServiceDelivery(serviceDelivery);

        return new MergingSiriSource(Collections.emptyList()) {
            @Override
            public Siri getFeed() {
                return siri;
            }
        };
    }
}
