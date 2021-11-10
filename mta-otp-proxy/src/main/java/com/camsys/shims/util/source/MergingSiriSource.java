package com.camsys.shims.util.source;

import uk.org.siri.siri.AffectedVehicleJourneyStructure;
import uk.org.siri.siri.AffectsScopeStructure;
import uk.org.siri.siri.HalfOpenTimestampRangeStructure;
import uk.org.siri.siri.PtConsequenceStructure;
import uk.org.siri.siri.PtConsequencesStructure;
import uk.org.siri.siri.PtSituationElementStructure;
import uk.org.siri.siri.ServiceDelivery;
import uk.org.siri.siri.Siri;
import uk.org.siri.siri.SituationExchangeDeliveryStructure;
import uk.org.siri.siri.SituationSourceStructure;

import java.util.List;

/**
 * Merge a list of SIRI feeds into one feed.
 */
public class MergingSiriSource {

    private Siri _siri;
    private List<SiriSource> _sources;

    public MergingSiriSource(List<SiriSource> sources) { this._sources = sources; }

    public Siri getFeed() {
        return _siri;
    }


    public void update() {
        Siri all = new Siri();
        for (SiriSource source : _sources) {
            if (source != null) {
                source.update();
                if (source.getSiri() != null) {
                    ServiceDelivery sd = source.getSiri().getServiceDelivery();
                    fillServiceDelivery(all, sd);
                }
            }
        }
        _siri = all;

    }

    private void fillServiceDelivery(Siri output, ServiceDelivery inputSd) {
        for (SituationExchangeDeliveryStructure seds : inputSd.getSituationExchangeDelivery()) {
            if (output.getServiceDelivery() == null) {
                ServiceDelivery sd = new ServiceDelivery();
                sd.setResponseTimestamp(inputSd.getResponseTimestamp());
                output.setServiceDelivery(sd);

            } else {
                if (output.getServiceDelivery().getResponseTimestamp() != null
                && output.getServiceDelivery().getResponseTimestamp().getTime() <
                   inputSd.getResponseTimestamp().getTime()) {
                    output.getServiceDelivery().setResponseTimestamp(inputSd.getResponseTimestamp());
                }
            }
            fillSituationExchangeDelivery(output.getServiceDelivery(), seds);
        }

    }

    private void fillSituationExchangeDelivery(ServiceDelivery output, SituationExchangeDeliveryStructure inputSeds) {
        SituationExchangeDeliveryStructure outputSed = new SituationExchangeDeliveryStructure();
        outputSed.setResponseTimestamp(inputSeds.getResponseTimestamp());
        outputSed.setStatus(inputSeds.isStatus());
        output.getSituationExchangeDelivery().add(outputSed);
        fillSituations(outputSed, inputSeds.getSituations());

    }

    private void fillSituations(SituationExchangeDeliveryStructure output, SituationExchangeDeliveryStructure.Situations inputSituations) {
        SituationExchangeDeliveryStructure.Situations outputSituation = new SituationExchangeDeliveryStructure.Situations();
        output.setSituations(outputSituation);
        for (PtSituationElementStructure pt : inputSituations.getPtSituationElement()) {
            fillPtSituationElement(outputSituation, pt);
        }

    }

    private void fillPtSituationElement(SituationExchangeDeliveryStructure.Situations output, PtSituationElementStructure pt) {
        PtSituationElementStructure outputPt = new PtSituationElementStructure();
        output.getPtSituationElement().add(outputPt);
        outputPt.setCreationTime(pt.getCreationTime());
        outputPt.setSituationNumber(pt.getSituationNumber());
        fillPublicationWindow(outputPt, pt.getPublicationWindow());
        outputPt.setSummary(pt.getSummary());
        outputPt.setDescription(pt.getDescription());
        outputPt.setAdvice(pt.getAdvice()); // place holder for long description
        outputPt.setPlanned(pt.isPlanned());
        outputPt.setReasonName(pt.getReasonName());
        outputPt.setPriority(pt.getPriority());
        fillSource(outputPt, pt.getSource());
        fillAffects(outputPt, pt.getAffects());
        fillConsequences(outputPt, pt.getConsequences());
    }

    private void fillConsequences(PtSituationElementStructure outputPt, PtConsequencesStructure consequences) {
        PtConsequencesStructure outputConsequence = new PtConsequencesStructure();
        outputPt.setConsequences(outputConsequence);
        for (PtConsequenceStructure consequence : consequences.getConsequence()) {
            fillConsequence(outputConsequence, consequence);
        }
    }

    private void fillConsequence(PtConsequencesStructure outputConsequences, PtConsequenceStructure consequence) {
        PtConsequenceStructure outputConsequence = new PtConsequenceStructure();
        outputConsequences.getConsequence().add(outputConsequence);
        outputConsequence.setCondition(consequence.getCondition());
        outputConsequence.setSeverity(consequence.getSeverity());

    }

    private void fillAffects(PtSituationElementStructure output, AffectsScopeStructure affects) {
        AffectsScopeStructure outputAffects = new AffectsScopeStructure();
        output.setAffects(outputAffects);
        fillVehicleJourneys(outputAffects, affects.getVehicleJourneys());
    }

    private void fillVehicleJourneys(AffectsScopeStructure output, AffectsScopeStructure.VehicleJourneys vehicleJourneys) {
        AffectsScopeStructure.VehicleJourneys outputVj = new AffectsScopeStructure.VehicleJourneys();
        output.setVehicleJourneys(outputVj);

        for (AffectedVehicleJourneyStructure affectedVehicleJourney : vehicleJourneys.getAffectedVehicleJourney()) {
            fillVehicleJourney(outputVj, affectedVehicleJourney);
        }


    }

    private void fillVehicleJourney(AffectsScopeStructure.VehicleJourneys outputVjs, AffectedVehicleJourneyStructure vehicleJourney) {
        AffectedVehicleJourneyStructure outputVj = new AffectedVehicleJourneyStructure();
        outputVjs.getAffectedVehicleJourney().add(outputVj);
        outputVj.setDirectionRef(vehicleJourney.getDirectionRef());
        outputVj.setLineRef(vehicleJourney.getLineRef());
        outputVj.setOperator(vehicleJourney.getOperator());
        outputVj.setPublishedLineName(vehicleJourney.getPublishedLineName());
    }

    private void fillSource(PtSituationElementStructure output, SituationSourceStructure source) {
        SituationSourceStructure outputSource = new SituationSourceStructure();
        outputSource.setSourceType(source.getSourceType());
        output.setSource(outputSource);
    }

    private void fillPublicationWindow(PtSituationElementStructure output, HalfOpenTimestampRangeStructure window) {
        HalfOpenTimestampRangeStructure outputWindow = new HalfOpenTimestampRangeStructure();
        output.setPublicationWindow(outputWindow);
        outputWindow.setStartTime(window.getStartTime());
        outputWindow.setEndTime(window.getEndTime());
    }
}
