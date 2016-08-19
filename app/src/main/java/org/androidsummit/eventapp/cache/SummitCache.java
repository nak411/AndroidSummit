package org.androidsummit.eventapp.cache;

import org.androidsummit.eventapp.utils.helpers.DateHelper;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Helper class for retrieving cached objects specific to this application
 * <p/>
 * Created on 8/18/16.
 */
public class SummitCache {

    private static final String SCHEDULE_DATES = "schedule.dates";

    private static final String START_DATE = "start.date";


    public static void setStartDate(Date startDate) {
        MemCache.getInstance().putObject(START_DATE, startDate);;
    }

    public static Date getStartDate() {
        return (Date) MemCache.getInstance().getObject(START_DATE);
    }

    public static void setScheduleDates(List<Date> dates) {
        MemCache.getInstance().putObject(SCHEDULE_DATES, dates);
    }

    public static List<Date> getScheduleDates() {
        //noinspection unchecked
        return (List<Date>) MemCache.getInstance().getObject(SCHEDULE_DATES);
    }


    public static void init() {
        //TODO load this data from server;
        //Set start data
        setStartDate(DateHelper.getFormattedFullDateAndTime("08", "26", "2016"));

        //TODO make more dynamic
        Date[] mTitles = {
                DateHelper.getFormattedFullDateAndTime("08", "26", "2016"),
                DateHelper.getFormattedFullDateAndTime("08", "27", "2016")
        };
        setScheduleDates(Arrays.asList(mTitles));
    }
}
