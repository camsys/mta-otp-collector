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
import java.util.List;

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

            if (isActive(entity)) {
                PtSituationElementStructure pt = new PtSituationElementStructure();
                if (entity.hasId())
                    pt.setSituationNumber(createSituationNumber(entity));
                fillPtSituationElement(pt, entity.getAlert());
                s.getPtSituationElement().add(pt);
            }
        }
        _siri = siri;
        return siri;
    }

    // test if date range of alert covers now
    private boolean isActive(GtfsRealtime.FeedEntity entity) {
        if (!entity.hasAlert()) return false; // not an alert
        GtfsRealtime.Alert alert = entity.getAlert();
        return isActive(alert);
    }

    private boolean isActive(GtfsRealtime.Alert alert) {
        boolean foundMatchingRange = false;
        boolean noRanges = true;

        Date pointInTime = new Date();
        for (GtfsRealtime.TimeRange activePeriod : alert.getActivePeriodList()) {
            noRanges = false;
            foundMatchingRange = isActive(pointInTime, activePeriod);
            if (foundMatchingRange) return true;
            // else keep looking
        }
        if (noRanges) {
            // no specified active period implies its active NOW
            return true;
        }
        return false;
    }

    private boolean isActive(Date pointInTime, GtfsRealtime.TimeRange timeRange) {
        Date periodStart = new Date(0);
        if (timeRange.hasStart())
            periodStart = new Date(timeRange.getStart() * 1000);
        Date periodEnd = new Date(Long.MAX_VALUE);
        if (timeRange.hasEnd())
            periodEnd = new Date(timeRange.getEnd() * 1000);

        return inRange(pointInTime, periodStart, periodEnd);
    }


    private boolean inRange(Date pointInTime, Date start, Date end) {
        return pointInTime.getTime() >= start.getTime()
                && pointInTime.getTime() <= end.getTime();
    }

    private EntryQualifierStructure createSituationNumber(GtfsRealtime.FeedEntity entity) {
        String id = entity.getId();
        String agencyId = getAgencyFromInformedEntity(entity.getAlert().getInformedEntityList());
        EntryQualifierStructure s = new EntryQualifierStructure();
        if (agencyId == null) {
            s.setValue(id);
        } else {
            s.setValue(new AgencyAndId(agencyId, id).toString());
        }
        return s;
    }

    private String getAgencyFromInformedEntity(List<GtfsRealtime.EntitySelector> informedEntityList) {
        if (informedEntityList == null || informedEntityList.isEmpty()) return null;
        for (GtfsRealtime.EntitySelector selector : informedEntityList) {
            if (selector.hasAgencyId())
                return selector.getAgencyId();
        }
        return null;
    }

    private void fillPtSituationElement(PtSituationElementStructure pt, GtfsRealtime.Alert alert) {

        pt.setSummary(getTranslation(alert.getHeaderText())); // this will be reset if screenSummary is present
        pt.setDescription(getTranslation(alert.getHeaderText())); // description is now the brief summary
        pt.setAdvice(getTranslation(alert.getDescriptionText()));// will be LongDescription for GMS output


        Date now = new Date();
        for (GtfsRealtime.TimeRange timeRange : alert.getActivePeriodList()) {
            // we only support one window -- make sure it's the active one
            // we know there is at least on active window as a precondition to this method
            if (isActive(now, timeRange)) {
                HalfOpenTimestampRangeStructure window = new HalfOpenTimestampRangeStructure();
                pt.setPublicationWindow(window);
                if (timeRange.hasStart()) {
                    window.setStartTime(toDate(timeRange.getStart()));
                }
                if (timeRange.hasEnd()) {
                    window.setEndTime(toDate(timeRange.getEnd()));
                }
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
            if (mercuryAlert.hasScreensSummary()) {
                pt.setSummary(getTranslation(mercuryAlert.getScreensSummary()));
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
                } else {
                    AffectedVehicleJourneyStructure avjClone = cloneJourney(avj);
                    avj.setDirectionRef(toDirectionRef(0));
                    avjClone.setDirectionRef(toDirectionRef(1));
                    pt.getAffects().getVehicleJourneys().getAffectedVehicleJourney().add(avjClone);
                }
            }
        }
        PtConsequenceStructure consequence = new PtConsequenceStructure();
        consequence.setSeverity(SeverityEnumeration.UNDEFINED);
        pt.setConsequences(new PtConsequencesStructure());
        pt.getConsequences().getConsequence().add(consequence);

    }

    private AffectedVehicleJourneyStructure cloneJourney(AffectedVehicleJourneyStructure avj) {
        AffectedVehicleJourneyStructure clone = new AffectedVehicleJourneyStructure();
        clone.setLineRef(toLineRef(avj.getLineRef().getValue()));
        return clone;
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