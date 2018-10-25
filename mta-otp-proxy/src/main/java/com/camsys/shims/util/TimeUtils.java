package com.camsys.shims.util;

import org.onebusaway.gtfs.model.calendar.ServiceDate;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>TimeUtils class.</p>
 *
 */
public class TimeUtils {

    /**
     * <p>getCurrentDateTime.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String getCurrentDateTime() {
        return getFormattedDateTime(System.currentTimeMillis());
    }

    /**
     * <p>getFormattedDateTime.</p>
     *
     * @param time a long.
     * @return a {@link java.lang.String} object.
     */
    public static String getFormattedDateTime(long time){
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(time),
                TimeZone.getDefault().toZoneId()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * <p>getServiceDate.</p>
     *
     * @return a {@link org.onebusaway.gtfs.model.calendar.ServiceDate} object.
     */
    public static ServiceDate getServiceDate() {
        Date d = new Date();
        return new ServiceDate(d);
    }

}
