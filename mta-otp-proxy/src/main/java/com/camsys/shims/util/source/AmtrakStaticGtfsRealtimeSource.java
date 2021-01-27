package com.camsys.shims.util.source;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtimeConstants;
import org.apache.commons.lang.NotImplementedException;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeIncrementalListener;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeSource;

import java.util.*;

public class AmtrakStaticGtfsRealtimeSource implements GtfsRealtimeSource {

    private GtfsDataService _gtfs;

    public void setGtfsDataService(GtfsDataService gtfs) {
        _gtfs = gtfs;
    }

    @Override
    public GtfsRealtime.FeedMessage getFeed() {
        GtfsRealtime.FeedMessage.Builder message = GtfsRealtime.FeedMessage.newBuilder();

        GtfsRealtime.FeedHeader.Builder header = GtfsRealtime.FeedHeader.newBuilder();
        //header.setIncrementality(GtfsRealtime.FeedHeader.Incrementality.FULL_DATASET);
        header.setTimestamp(System.currentTimeMillis() / 1000);
        header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
        message.setHeader(header);

        //get service ids active today and the previous week and tomorrow since we need active trips and
        // trips can span many days or actually start on the previous day (negative stop times)

        ServiceDate today = new ServiceDate(new Date());
        Set<AgencyAndId> currentServiceIds = _gtfs.getServiceIdsOnDate(today);
        currentServiceIds.addAll(_gtfs.getServiceIdsOnDate(today.shift(-1)));
        currentServiceIds.addAll(_gtfs.getServiceIdsOnDate(today.shift(-2)));
        currentServiceIds.addAll(_gtfs.getServiceIdsOnDate(today.shift(-3)));
        currentServiceIds.addAll(_gtfs.getServiceIdsOnDate(today.shift(-4)));
        currentServiceIds.addAll(_gtfs.getServiceIdsOnDate(today.shift(-5)));
        currentServiceIds.addAll(_gtfs.getServiceIdsOnDate(today.shift(-6)));
        currentServiceIds.addAll(_gtfs.getServiceIdsOnDate(today.shift(1)));


        for (AgencyAndId serviceId : currentServiceIds) {

            //get trips for service id
            for (Trip trip : _gtfs.getTripsForServiceId(serviceId)) {

                ServiceDate startDate = getActiveStartDateForTrip(trip);
                if (startDate != null) {
                    String vehicleLabel = trip.getTripShortName();//assume vehicle label is stored as trip short name
                    GtfsRealtime.FeedEntity.Builder entity = GtfsRealtime.FeedEntity.newBuilder();
                    entity.setId(vehicleLabel);

                    GtfsRealtime.VehiclePosition.Builder vehiclePosition = GtfsRealtime.VehiclePosition.newBuilder();

                    vehiclePosition.setTimestamp(System.currentTimeMillis() / 1000);

                    //add trip descriptor
                    GtfsRealtime.TripDescriptor.Builder tripDescriptor = GtfsRealtime.TripDescriptor.newBuilder();
                    tripDescriptor.setTripId(trip.getId().getId());
                    if (trip.getRoute() != null) tripDescriptor.setRouteId(trip.getRoute().getId().getId());
                    tripDescriptor.setScheduleRelationship(GtfsRealtime.TripDescriptor.ScheduleRelationship.SCHEDULED);//????
                    tripDescriptor.setStartDate(startDate.getAsString());
                    vehiclePosition.setTrip(tripDescriptor.build());

                    //add vehicle descriptor
                    GtfsRealtime.VehicleDescriptor.Builder vehicleDescriptor = GtfsRealtime.VehicleDescriptor.newBuilder();
                    vehicleDescriptor.setLabel(vehicleLabel);
                    vehiclePosition.setVehicle(vehicleDescriptor.build());

                    entity.setVehicle(vehiclePosition.build());

                    message.addEntity(entity.build());
                }
            }
        }

        //create entity for each trip with the vehicle label as the trip_short_name

        return message.build();
    }

    @Override
    public void addIncrementalListener(GtfsRealtimeIncrementalListener gtfsRealtimeIncrementalListener) {
        throw new NotImplementedException();
    }

    @Override
    public void removeIncrementalListener(GtfsRealtimeIncrementalListener gtfsRealtimeIncrementalListener) {
        throw new NotImplementedException();
    }

    // return the start date for the trip if it is active today otherwise return null
    private ServiceDate getActiveStartDateForTrip(Trip trip) {
        List<StopTime> stopTimes = _gtfs.getStopTimesForTrip(trip);
        ServiceCalendar calendar = _gtfs.getCalendarForServiceId(trip.getServiceId());
        StopTime startTime = stopTimes.get(0);
        StopTime endTime = stopTimes.get(stopTimes.size() - 1);

        //Amtrak doesn't seem to have any negative start times but this is here for completeness
        int startDayShift = startTime.getArrivalTime() < 0 ? -1 : startTime.getArrivalTime() / (24*60*60);
        int endDayShift = endTime.getArrivalTime() / (24*60*60);
        int extraDays = endDayShift - startDayShift;

        boolean[] calendarAsArray = {false,false,false,false,false,false,false};//SUN, MON, TUES, WED, THURS, FRI, SAT
        boolean[] activeDays = {false,false,false,false,false,false,false};//SUN, MON, TUES, WED, THURS, FRI, SAT

        for (int i = 0; i <= extraDays; i++) {
            if (calendar.getSunday() == 1) {
                activeDays[(Calendar.SUNDAY + 6 + startDayShift + i) % 7] = true;
                calendarAsArray[0] = true;
            }
            if (calendar.getMonday() == 1) {
                activeDays[(Calendar.MONDAY + 6 + startDayShift + i) % 7] = true;
                calendarAsArray[1] = true;
            }
            if (calendar.getTuesday() == 1) {
                activeDays[(Calendar.TUESDAY + 6 + startDayShift + i) % 7] = true;
                calendarAsArray[2] = true;
            }
            if (calendar.getWednesday() == 1) {
                activeDays[(Calendar.WEDNESDAY + 6 + startDayShift + i) % 7] = true;
                calendarAsArray[3] = true;
            }
            if (calendar.getThursday() == 1) {
                activeDays[(Calendar.THURSDAY + 6 + startDayShift + i) % 7] = true;
                calendarAsArray[4] = true;
            }
            if (calendar.getFriday() == 1) {
                activeDays[(Calendar.FRIDAY + 6 + startDayShift + i) % 7] = true;
                calendarAsArray[5] = true;
            }
            if (calendar.getSaturday() == 1) {
                activeDays[(Calendar.SATURDAY + 6 + startDayShift + i) % 7] = true;
                calendarAsArray[6] = true;
            }
        }

        ServiceDate startDate = new ServiceDate(new Date());
        Calendar today = Calendar.getInstance();
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        if (activeDays[dayOfWeek - 1]) {
            for (int i = 6; i >= 0; i--) {
                if (calendarAsArray[(dayOfWeek + i) % 7]) return startDate.shift(i - 6);
            }
        }
        return null;
    }
}
