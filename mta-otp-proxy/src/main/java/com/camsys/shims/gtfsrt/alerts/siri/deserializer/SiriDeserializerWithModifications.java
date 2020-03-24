/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.camsys.shims.gtfsrt.alerts.siri.deserializer;

import com.amazonaws.util.IOUtils;
import com.camsys.shims.util.HtmlCleanupUtil;
import jdk.nashorn.internal.ir.WhileNode;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import uk.org.siri.siri.*;
import uk.org.siri.siri.SituationExchangeDeliveryStructure.Situations;


import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// deserialize SIRI and modify LongDescription
public class SiriDeserializerWithModifications extends SiriDeserializer {

    private HtmlCleanupUtil _htmlCleanupUtil;
    private String _filterLmm;

    public void setHtmlCleanupUtil(HtmlCleanupUtil htmlCleanupUtil) {
        _htmlCleanupUtil = htmlCleanupUtil;
    }
    public void setFilterLmm(String filterLmm) {
        _filterLmm = filterLmm;
    }

    @Override
    public Siri deserialize(InputStream inputStream) throws IOException {
        String xml = IOUtils.toString(inputStream);
        xml = xml.replace("<Description", "<LegacyDescription")
            .replace("</Description>", "</LegacyDescription>")
            .replace("<LongDescription>", "<Description>")
            .replace("</LongDescription>", "</Description>")
            .replace("<MessagePriority>", "<Priority>")
            .replace("</MessagePriority>", "</Priority>");
        Siri siri = deserialize(xml);
        removeHtml(siri);
        filterLmm(siri);
        return siri;
    }

    private void removeHtml(Siri siri) {
        if (siri.getServiceDelivery() != null) {
            ServiceDelivery sd = siri.getServiceDelivery();
            if (sd.getSituationExchangeDelivery() != null) {
                for (SituationExchangeDeliveryStructure seds : sd.getSituationExchangeDelivery()) {
                    if (seds.getSituations() != null) {
                        Situations s = seds.getSituations();
                        if (s.getPtSituationElement() != null) {
                            for (PtSituationElementStructure pt : s.getPtSituationElement()) {
                                if (pt.getDescription() != null) {
                                    DefaultedTextStructure txt = pt.getDescription();
                                    String html = txt.getValue();
                                    String cleanedHtml = _htmlCleanupUtil.filterHtml(html);
                                    txt.setValue(cleanedHtml);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void filterLmm(Siri siri) {
        //ServiceDelivery serviceDelivery = new ServiceDelivery();
        //siri1.setServiceDelivery(serviceDelivery);
        //siri1.setServiceDelivery(siri.getServiceDelivery());
        Situations s1 = new Situations();
        if (siri.getServiceDelivery() != null) {
            ServiceDelivery sd = siri.getServiceDelivery();
            if (sd.getSituationExchangeDelivery() != null) {
                for (SituationExchangeDeliveryStructure seds : sd.getSituationExchangeDelivery()) {
                    if (seds.getSituations() != null) {
                        Situations s = seds.getSituations();
                        if (s.getPtSituationElement() != null) {
                            for (PtSituationElementStructure pt: s.getPtSituationElement()) {
                                if (!pt.getSituationNumber().getValue().contains("lmm")) s1.getPtSituationElement().add(pt);
                            }
                            seds.setSituations(s1);
                        }
                    }
                }
            }
        }
    }
}
