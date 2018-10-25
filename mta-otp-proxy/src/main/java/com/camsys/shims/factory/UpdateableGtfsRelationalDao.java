package com.camsys.shims.factory;

import org.joda.time.DateTime;
import org.onebusaway.gtfs.impl.GenericDaoImpl;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.impl.MultipleCalendarsForServiceIdException;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * <p>UpdateableGtfsRelationalDao class.</p>
 *
 */
public class UpdateableGtfsRelationalDao extends GtfsRelationalDaoImpl {
    private static Logger _log = LoggerFactory.getLogger(UpdateableGtfsRelationalDao.class);

    private String _gtfsPath;

    /**
     * <p>setGtfsPath.</p>
     *
     * @param path a {@link java.lang.String} object.
     */
    public void setGtfsPath(String path) { _gtfsPath = path; }
    /**
     * <p>getGtfsPath.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGtfsPath() { return _gtfsPath; }

    GtfsRelationalDaoImpl _dao = new GtfsRelationalDaoImpl();

    /**
     * <p>getRelaodedDao.</p>
     *
     * @return a {@link org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl} object.
     */
    public GtfsRelationalDaoImpl getRelaodedDao(){
        return _dao;
    }

    /**
     * <p>setReloadedDao.</p>
     *
     * @param dao a {@link org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl} object.
     */
    public void setReloadedDao(GtfsRelationalDaoImpl dao)
    {
        _dao = dao;
    }

    /**
     * <p>load.</p>
     */
    public void load(){
        File file = new File(_gtfsPath);

        _log.info("Is loading a DAO for path {} with file size {} at time {}", _gtfsPath, file.length(), DateTime.now().toLocalTime().toString("HH:mm:ss"));

        loadDao(file);

        _log.info("Is done loading a DAO for path {} at time {}", _gtfsPath, DateTime.now().toLocalTime().toString("HH:mm:ss"));
    }

    private void loadDao(File file)
    {
        GtfsRelationalDaoImpl dao = new GtfsRelationalDaoImpl();
        GtfsReader reader = new GtfsReader();

        reader.setEntityStore(dao);

        try {
            reader.setInputLocation(file);
            reader.run();
            reader.close();

            setReloadedDao(dao);
        } catch (IOException e) {
            throw new RuntimeException("Failure while reading GTFS", e);
        }
    }

    /** {@inheritDoc} */
    public List<String> getTripAgencyIdsReferencingServiceId(AgencyAndId serviceId) {
        return getRelaodedDao().getTripAgencyIdsReferencingServiceId(serviceId);
    }

    /** {@inheritDoc} */
    public List<Route> getRoutesForAgency(Agency agency) {
        return getRelaodedDao().getRoutesForAgency(agency);
    }

    /** {@inheritDoc} */
    public List<Stop> getStopsForStation(Stop station) {
        return getRelaodedDao().getStopsForStation(station);
    }

    /**
     * <p>getAllShapeIds.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<AgencyAndId> getAllShapeIds() {
        return getRelaodedDao().getAllShapeIds();
    }

    /** {@inheritDoc} */
    public List<ShapePoint> getShapePointsForShapeId(AgencyAndId shapeId) {
        return getRelaodedDao().getShapePointsForShapeId(shapeId);
    }

    /** {@inheritDoc} */
    public List<StopTime> getStopTimesForTrip(Trip trip) {
        return getRelaodedDao().getStopTimesForTrip(trip);
    }

    /** {@inheritDoc} */
    public List<StopTime> getStopTimesForStop(Stop stop) {
        return getRelaodedDao().getStopTimesForStop(stop);
    }

    /** {@inheritDoc} */
    public List<Trip> getTripsForRoute(Route route) {
        return getRelaodedDao().getTripsForRoute(route);
    }

    /** {@inheritDoc} */
    public List<Trip> getTripsForShapeId(AgencyAndId shapeId) {
        return getRelaodedDao().getTripsForShapeId(shapeId);
    }

    /** {@inheritDoc} */
    public List<Trip> getTripsForServiceId(AgencyAndId serviceId) {
        return getRelaodedDao().getTripsForServiceId(serviceId);
    }

    /** {@inheritDoc} */
    public List<Trip> getTripsForBlockId(AgencyAndId blockId) {
        return getRelaodedDao().getTripsForBlockId(blockId);
    }

    /** {@inheritDoc} */
    public List<Frequency> getFrequenciesForTrip(Trip trip) {
        return getRelaodedDao().getFrequenciesForTrip(trip);
    }

    /**
     * <p>getAllServiceIds.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<AgencyAndId> getAllServiceIds() {
        return getRelaodedDao().getAllServiceIds();
    }

    /** {@inheritDoc} */
    public List<ServiceCalendarDate> getCalendarDatesForServiceId(AgencyAndId serviceId) {
        return getRelaodedDao().getCalendarDatesForServiceId(serviceId);
    }

    /** {@inheritDoc} */
    public ServiceCalendar getCalendarForServiceId(AgencyAndId serviceId) {
        return getRelaodedDao().getCalendarForServiceId(serviceId);
    }

    /** {@inheritDoc} */
    public List<FareRule> getFareRulesForFareAttribute(FareAttribute fareAttribute) {
        return getRelaodedDao().getFareRulesForFareAttribute(fareAttribute);
    }

    /**
     * <p>clearAllCaches.</p>
     */
    public void clearAllCaches() {
        getRelaodedDao().clearAllCaches();
    }

    /**
     * <p>isPackStopTimes.</p>
     *
     * @return a boolean.
     */
    public boolean isPackStopTimes() {
        return getRelaodedDao().isPackStopTimes();
    }

    /** {@inheritDoc} */
    public void setPackStopTimes(boolean packStopTimes) {
        getRelaodedDao().setPackStopTimes(packStopTimes);
    }

    /**
     * <p>isPackShapePoints.</p>
     *
     * @return a boolean.
     */
    public boolean isPackShapePoints() {
        return getRelaodedDao().isPackShapePoints();
    }

    /** {@inheritDoc} */
    public void setPackShapePoints(boolean packShapePoints) {
        getRelaodedDao().setPackShapePoints(packShapePoints);
    }

    /** {@inheritDoc} */
    public Agency getAgencyForId(String id) {
        return (Agency)getRelaodedDao().getEntityForId(Agency.class, id);
    }

    /**
     * <p>getAllAgencies.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Agency> getAllAgencies() {
        return getRelaodedDao().getAllEntitiesForType(Agency.class);
    }

    /**
     * <p>getAllBlocks.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Block> getAllBlocks() {
        return getRelaodedDao().getAllEntitiesForType(Block.class);
    }

    /**
     * <p>getAllCalendarDates.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<ServiceCalendarDate> getAllCalendarDates() {
        return getRelaodedDao().getAllEntitiesForType(ServiceCalendarDate.class);
    }

    /**
     * <p>getAllCalendars.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<ServiceCalendar> getAllCalendars() {
        return getRelaodedDao().getAllEntitiesForType(ServiceCalendar.class);
    }

    /**
     * <p>getAllFareAttributes.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<FareAttribute> getAllFareAttributes() {
        return getRelaodedDao().getAllEntitiesForType(FareAttribute.class);
    }

    /**
     * <p>getAllFareRules.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<FareRule> getAllFareRules() {
        return getRelaodedDao().getAllEntitiesForType(FareRule.class);
    }

    /**
     * <p>getAllFeedInfos.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<FeedInfo> getAllFeedInfos() {
        return getRelaodedDao().getAllEntitiesForType(FeedInfo.class);
    }

    /**
     * <p>getAllFrequencies.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Frequency> getAllFrequencies() {
        return getRelaodedDao().getAllEntitiesForType(Frequency.class);
    }

    /**
     * <p>getAllRoutes.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Route> getAllRoutes() {
        return getRelaodedDao().getAllEntitiesForType(Route.class);
    }

    /**
     * <p>getAllShapePoints.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<ShapePoint> getAllShapePoints() {
        return getRelaodedDao().getAllShapePoints();
    }

    /**
     * <p>getAllStopTimes.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<StopTime> getAllStopTimes() {
        return getRelaodedDao().getAllStopTimes();
    }

    /**
     * <p>getAllStops.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Stop> getAllStops() {
        return getRelaodedDao().getAllStops();
    }

    /**
     * <p>getAllTransfers.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Transfer> getAllTransfers() {
        return getRelaodedDao().getAllTransfers();
    }

    /**
     * <p>getAllTrips.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Trip> getAllTrips() {
        return getRelaodedDao().getAllTrips();
    }

    /** {@inheritDoc} */
    public Block getBlockForId(int id) {
        return getRelaodedDao().getBlockForId(id);
    }

    /** {@inheritDoc} */
    public ServiceCalendarDate getCalendarDateForId(int id) {
        return getRelaodedDao().getCalendarDateForId(id);
    }

    /** {@inheritDoc} */
    public ServiceCalendar getCalendarForId(int id) {
        return getRelaodedDao().getCalendarForId(id);
    }

    /** {@inheritDoc} */
    public FareAttribute getFareAttributeForId(AgencyAndId id) {
        return getRelaodedDao().getFareAttributeForId(id);
    }

    /** {@inheritDoc} */
    public FareRule getFareRuleForId(int id) {
        return getRelaodedDao().getFareRuleForId(id);
    }

    /** {@inheritDoc} */
    public FeedInfo getFeedInfoForId(String id) {
        return (FeedInfo)this.getEntityForId(FeedInfo.class, id);
    }

    /** {@inheritDoc} */
    public Frequency getFrequencyForId(int id) {
        return (Frequency)this.getEntityForId(Frequency.class, id);
    }

    /**
     * <p>getAllPathways.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Pathway> getAllPathways() {
        return this.getAllEntitiesForType(Pathway.class);
    }

    /** {@inheritDoc} */
    public Pathway getPathwayForId(AgencyAndId id) {
        return (Pathway)this.getEntityForId(Pathway.class, id);
    }

    /** {@inheritDoc} */
    public Route getRouteForId(AgencyAndId id) {
        return (Route)this.getEntityForId(Route.class, id);
    }

    /** {@inheritDoc} */
    public ShapePoint getShapePointForId(int id) {
        return getRelaodedDao().getShapePointForId(id);
    }

    /** {@inheritDoc} */
    public Stop getStopForId(AgencyAndId id) {
        return getRelaodedDao().getStopForId(id);
    }

    /** {@inheritDoc} */
    public StopTime getStopTimeForId(int id) {
        return getRelaodedDao().getStopTimeForId(id);
    }

    /** {@inheritDoc} */
    public Transfer getTransferForId(int id) {
        return getRelaodedDao().getTransferForId(id);
    }

    /** {@inheritDoc} */
    public Trip getTripForId(AgencyAndId id) {
        return getRelaodedDao().getTripForId(id);
    }

    /** {@inheritDoc} */
    public <K, V> Map<K, V> getEntitiesByIdForEntityType(Class<K> keyType, Class<V> entityType) {
        return getRelaodedDao().getEntitiesByIdForEntityType(keyType, entityType);
    }

    /** {@inheritDoc} */
    public <T> Collection<T> getAllEntitiesForType(Class<T> type) {
        return getRelaodedDao().getAllEntitiesForType(type);
    }

    /** {@inheritDoc} */
    public <T> T getEntityForId(Class<T> type, Serializable id) {
        return getRelaodedDao().getEntityForId(type, id);
    }

    /** {@inheritDoc} */
    public void saveEntity(Object entity) {
        getRelaodedDao().saveEntity(entity);
    }

    /** {@inheritDoc} */
    public <T> void clearAllEntitiesForType(Class<T> type) {
        getRelaodedDao().clearAllEntitiesForType(type);
    }

    /**
     * <p>removeEntity.</p>
     *
     * @param entity a T object.
     * @param <K> a K object.
     * @param <T> a T object.
     */
    public <K extends Serializable, T extends IdentityBean<K>> void removeEntity(T entity) {
        getRelaodedDao().removeEntity(entity);
    }

    /**
     * <p>close.</p>
     */
    public void close() {
        getRelaodedDao().close();
    }

    /** {@inheritDoc} */
    public void setGenerateIds(boolean generateIds) {
        getRelaodedDao().setGenerateIds(generateIds);
    }

    /**
     * <p>getEntityClasses.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Class<?>> getEntityClasses() {
        return getRelaodedDao().getEntityClasses();
    }

    /**
     * <p>clear.</p>
     */
    public void clear() {
        getRelaodedDao().clear();
    }

    /** {@inheritDoc} */
    public void updateEntity(Object entity) {
    }

    /** {@inheritDoc} */
    public void saveOrUpdateEntity(Object entity) {
        IdentityBean<Serializable> bean = (IdentityBean)entity;
        Object existing = this.getEntityForId(entity.getClass(), bean.getId());
        if (existing != entity) {
            this.saveEntity(entity);
        }
    }


    /**
     * <p>open.</p>
     */
    public void open() {
    }

    /**
     * <p>flush.</p>
     */
    public void flush() {
    }
}
