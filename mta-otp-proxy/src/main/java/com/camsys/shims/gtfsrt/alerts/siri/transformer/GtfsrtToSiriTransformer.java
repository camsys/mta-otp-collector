package com.camsys.shims.gtfsrt.alerts.siri.transformer;

import com.camsys.shims.service_status.transformer.MercuryUtils;
import com.google.transit.realtime.GtfsRealtime;

import com.google.transit.realtime.GtfsRealtimeServiceStatus;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.siri.siri.AffectedVehicleJourneyStructure;
import uk.org.siri.siri.AffectsScopeStructure;
import uk.org.siri.siri.DefaultedTextStructure;
import uk.org.siri.siri.DirectionRefStructure;
import uk.org.siri.siri.EntryQualifierStructure;
import uk.org.siri.siri.HalfOpenTimestampRangeStructure;
import uk.org.siri.siri.LineRefStructure;
import uk.org.siri.siri.NaturalLanguageStringStructure;
import uk.org.siri.siri.PtConsequenceStructure;
import uk.org.siri.siri.PtConsequencesStructure;
import uk.org.siri.siri.PtSituationElementStructure;
import uk.org.siri.siri.ServiceDelivery;
import uk.org.siri.siri.SeverityEnumeration;
import uk.org.siri.siri.Siri;
import uk.org.siri.siri.SituationExchangeDeliveryStructure;
import uk.org.siri.siri.SituationSourceStructure;
import uk.org.siri.siri.SituationSourceTypeEnumeration;

import java.math.BigInteger;
import java.util.Date;

/**
 * Convert Mercury GTFS-RT to SIRI and provide as a legacy integration strategy
 * for those expecting GMS feeds
 */
public class GtfsrtToSiriTransformer {

    private static final Logger _log = LoggerFactory.getLogger(GtfsrtToSiriTransformer.class);

    protected GtfsDataService _gtfsDataService;

    protected Siri _siri = null;

    private static String DEFAULT_LANG = "EN";

    private static String PLANNED_WORK = "Planned Work";

    public void setGtfsDataService(GtfsDataService gtfsDataService) {
        _gtfsDataService = gtfsDataService;
    }


    public Siri transform(GtfsRealtime.FeedMessage feedMessage) {
        Siri siri = new Siri();

        ServiceDelivery serviceDelivery = new ServiceDelivery();
        siri.setServiceDelivery(serviceDelivery);

        if (feedMessage.hasHeader() && feedMessage.getHeader().hasTimestamp()) {
            serviceDelivery.setResponseTimestamp(toDate(feedMessage.getHeader().getTimestamp()));
        } else {
            serviceDelivery.setResponseTimestamp(new Date());
        }

        if (feedMessage == null) return siri;
        SituationExchangeDeliveryStructure seds = new SituationExchangeDeliveryStructure();
        serviceDelivery.getSituationExchangeDelivery().add(seds);
        // Status according to spec is boolean.  GMS uses is as a string.  Luckily the bus feed switches status
        // to reasonName.
        seds.setStatus(true);
        SituationExchangeDeliveryStructure.Situations s = new SituationExchangeDeliveryStructure.Situations();
        seds.setSituations(s);

        for (GtfsRealtime.FeedEntity entity : feedMessage.getEntityList()) {

            if (entity.hasAlert()) {
                PtSituationElementStructure pt = new PtSituationElementStructure();
                if (entity.hasId())
                    pt.setSituationNumber(createSituationNumber(entity.getId()));
                fillPtSituationElement(pt, entity.getAlert());
                s.getPtSituationElement().add(pt);
            }
        }
        _siri = siri;
        return siri;
    }

    private EntryQualifierStructure createSituationNumber(String id) {
        EntryQualifierStructure s = new EntryQualifierStructure();
        s.setValue(id);
        return s;
    }

    private void fillPtSituationElement(PtSituationElementStructure pt, GtfsRealtime.Alert alert) {

        // summary and description are the same
        pt.setDescription(getTranslation(alert.getHeaderText()));
        pt.setSummary(getTranslation(alert.getHeaderText()));
        // custom long description
        pt.setDescription(getTranslation(alert.getDescriptionText()));

        for (GtfsRealtime.TimeRange timeRange : alert.getActivePeriodList()) {
                HalfOpenTimestampRangeStructure window = new HalfOpenTimestampRangeStructure();
            pt.setPublicationWindow(window);
            if (timeRange.hasStart()) {
                window.setStartTime(toDate(timeRange.getStart()));
            }
            if (timeRange.hasEnd()) {
                window.setEndTime(toDate(timeRange.getEnd()));
            }

        }

        GtfsRealtimeServiceStatus.MercuryAlert mercuryAlert = null;
        if (alert.hasExtension(GtfsRealtimeServiceStatus.mercuryAlert)) {
            mercuryAlert =
                    alert.getExtension(GtfsRealtimeServiceStatus.mercuryAlert);
            // creationDate
            if (mercuryAlert.hasCreatedAt())
            pt.setCreationTime(toDate(mercuryAlert.getCreatedAt()));
            // reasonName
            if (mercuryAlert.hasAlertType()) {
                pt.setReasonName(toNL(mercuryAlert.getAlertType()));
            }
        }

        if (pt.getCreationTime() == null && pt.getPublicationWindow() != null) {
            pt.setCreationTime(pt.getPublicationWindow().getStartTime());
        }

        // planned work
        if (mercuryAlert != null && mercuryAlert.hasAlertType()) {
            pt.setPlanned(PLANNED_WORK.equals(mercuryAlert.getAlertType()));
        } else {
            pt.setPlanned(false);
        }

        // source -> sourceType
        SituationSourceStructure source = new SituationSourceStructure();
        source.setSourceType(SituationSourceTypeEnumeration.DIRECT_REPORT);
        pt.setSource(source);
        // affects -> vehicleJourneys -> AffectedVehicleJourney
        for (GtfsRealtime.EntitySelector informedEntity : alert.getInformedEntityList()) {

            if (informedEntity.hasExtension(GtfsRealtimeServiceStatus.mercuryEntitySelector)) {
                GtfsRealtimeServiceStatus.MercuryEntitySelector mercuryEntitySelector =
                        informedEntity.getExtension(GtfsRealtimeServiceStatus.mercuryEntitySelector);
                // message priority
                if (mercuryEntitySelector.hasSortOrder())
                    pt.setPriority(parseSortOrder(mercuryEntitySelector.getSortOrder()));
            }

            if (pt.getAffects() == null) {
                AffectsScopeStructure affects = new AffectsScopeStructure();
                pt.setAffects(affects);
                AffectsScopeStructure.VehicleJourneys vj = new AffectsScopeStructure.VehicleJourneys();
                affects.setVehicleJourneys(vj);
            }
            AffectedVehicleJourneyStructure avj = new AffectedVehicleJourneyStructure();
            // carefully add a new avj per informed entity!
            pt.getAffects().getVehicleJourneys().getAffectedVehicleJourney().add(avj);
            // lineRef
            if (informedEntity.hasRouteId())
                avj.setLineRef(withRouteAgency(informedEntity.getRouteId()));
            if (informedEntity.hasTrip()) {
                if (informedEntity.getTrip().hasRouteId()) {
                    avj.setLineRef(toLineRef(informedEntity.getTrip().getRouteId()));
                }
                if (informedEntity.getTrip().hasDirectionId()) {
                    // directionRef
                    avj.setDirectionRef(toDirectionRef(informedEntity.getTrip().getDirectionId()));
                }
            }
        }
        PtConsequenceStructure consequence = new PtConsequenceStructure();
        consequence.setSeverity(SeverityEnumeration.UNDEFINED);
        pt.setConsequences(new PtConsequencesStructure());
        pt.getConsequences().getConsequence().add(consequence);

    }

    private DirectionRefStructure toDirectionRef(int directionId) {
        DirectionRefStructure s = new DirectionRefStructure();
        s.setValue(String.valueOf(directionId));
        return s;
    }

    private LineRefStructure toLineRef(String routeId) {
        LineRefStructure s = new LineRefStructure();
        s.setValue(routeId);
        return s;
    }

    private NaturalLanguageStringStructure toNL(String alertType) {
        NaturalLanguageStringStructure nl = new NaturalLanguageStringStructure();
        nl.setLang(DEFAULT_LANG);
        nl.setValue(alertType);
        return nl;
    }

    // prepend agencyId to routeId
    private LineRefStructure withRouteAgency(String routeId) {

        for (Agency agency : _gtfsDataService.getAllAgencies()) {
            AgencyAndId testId = new AgencyAndId(agency.getId(), routeId);
            Route route = _gtfsDataService.getRouteForId(testId);
            if (route != null) return toLineRef(testId.toString());
        }
        // we fell through, return as is
        return toLineRef(routeId);
    }

    private Date toDate(long time) {
        return new Date(time * 1000);
    }

    private BigInteger parseSortOrder(String sortOrder) {
        BigInteger bi = new MercuryUtils().parseSortOrder(sortOrder);
        if (bi == null) return BigInteger.valueOf(MercuryUtils.DEFAULT_SORT_ORDER);
        return bi;
    }
    private DefaultedTextStructure getTranslation(GtfsRealtime.TranslatedString descriptionText) {
        if (descriptionText != null && descriptionText.getTranslationList().size() > 0)
            return toText(descriptionText.getTranslation(0).getText());
        return null;
    }

    private DefaultedTextStructure toText(String text) {
        DefaultedTextStructure s = new DefaultedTextStructure();
        s.setLang(DEFAULT_LANG);
        s.setValue(text);
        return s;
    }

}