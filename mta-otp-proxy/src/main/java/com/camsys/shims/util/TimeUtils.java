package com.camsys.shims.util;

import org.onebusaway.gtfs.model.calendar.ServiceDate;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

    public static String getCurrentDateTime() {
        return getFormattedDateTime(System.currentTimeMillis());
    }

    public static String getFormattedDateTime(long time){
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(time),
                TimeZone.getDefault().toZoneId()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static ServiceDate getServiceDate() {
        Date d = new Date();
        return new ServiceDate(d);
    }

}
