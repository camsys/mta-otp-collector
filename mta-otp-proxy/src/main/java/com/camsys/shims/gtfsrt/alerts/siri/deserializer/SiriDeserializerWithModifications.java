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
import uk.org.siri.siri.DefaultedTextStructure;
import uk.org.siri.siri.PtSituationElementStructure;
import uk.org.siri.siri.ServiceDelivery;
import uk.org.siri.siri.Siri;
import uk.org.siri.siri.SituationExchangeDeliveryStructure;
import uk.org.siri.siri.SituationExchangeDeliveryStructure.Situations;


import java.io.IOException;
import java.io.InputStream;

/**
 * deserialize SIRI and make some modifications to
 * coerce into SIRI compliant data model.
 * This includes modifications to LongDescription and MessagePriority.
 */
public class SiriDeserializerWithModifications extends SiriDeserializer {

    private static final String LMM_PREFIX = "lmm:";

    private HtmlCleanupUtil _htmlCleanupUtil;
    private boolean _filterLmm;

    public void setHtmlCleanupUtil(HtmlCleanupUtil htmlCleanupUtil) {
        _htmlCleanupUtil = htmlCleanupUtil;
    }
    public void setFilterLmm(boolean filterLmm) {
        _filterLmm = filterLmm;
    }

    @Override
    public Siri deserialize(InputStream inputStream) throws IOException {
        String xml = IOUtils.toString(inputStream);
        // here we undo some of the "liberties" that GMS has taken with the SIRI feed
        // Description -> LegacyDescription (which means its lost)
        // LongDescription -> Description
        // MessagePriority -> Priority
        xml = xml.replace("<Description", "<LegacyDescription")
            .replace("</Description>", "</LegacyDescription>")
            .replace("<LongDescription>", "<Description>")
            .replace("</LongDescription>", "</Description>")
            .replace("<MessagePriority>", "<Priority>")
            .replace("</MessagePriority>", "</Priority>");
        Siri siri = deserialize(xml);
        removeHtml(siri);
        if (_filterLmm) {filterLmm(siri);}
        return siri;
    }

    /**
     * filter out HTML tags assuming downstream clients don't support them
     * @param siri compliant data feed
     */
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

    // LMM data should come from GTFS-RT, not SIRI
    // if its in the SIRI feed, another system (GMS) is echoing it to us
    // and it should be disregarded
    private void filterLmm(Siri siri) {
        if (siri.getServiceDelivery() != null) {
            ServiceDelivery sd = siri.getServiceDelivery();
            if (sd.getSituationExchangeDelivery() != null) {
                for (SituationExchangeDeliveryStructure seds : sd.getSituationExchangeDelivery()) {
                    if (seds.getSituations() != null) {
                        Situations s = seds.getSituations();
                        Situations filteredSituations = new Situations();
                        if (s.getPtSituationElement() != null) {
                            for (PtSituationElementStructure pt: s.getPtSituationElement()) {
                                if (!pt.getSituationNumber().getValue().startsWith(LMM_PREFIX)) filteredSituations.getPtSituationElement().add(pt);
                            }
                            seds.setSituations(filteredSituations);
                        }
                    }
                }
            }
        }
    }
}
