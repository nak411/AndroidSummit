package org.androidsummit.eventapp.utils.helpers;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Helper class used for formatting/parsing date objects.
 *
 * Created by Naveed on 3/13/15.
 */
public class DateHelper {

    public static final String TAG = DateHelper.class.getSimpleName();

    private static SimpleDateFormat MM_DD_YYYY_HH_MM;

    private static SimpleDateFormat MM_DD_YYYY;

    private static SimpleDateFormat EEE_MMM_D;

    private static SimpleDateFormat EEE_D_MMM_YYYY_HH_MM_AAA;

    private static SimpleDateFormat H_MM_A;


    public static Date getFormattedDateAndTime(String month, String day, String year, String hr, String min) {
        if (MM_DD_YYYY_HH_MM == null) {
            MM_DD_YYYY_HH_MM = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        }
        Date formattedDate = null;
        try {
            //Month and year hard coded
            formattedDate = MM_DD_YYYY_HH_MM.parse(month + "/" + day + "/" + year + " " + hr + ":" + min);
        } catch (ParseException e) {
            Log.e(TAG, "Unable to parse date " + e.getMessage());
        }

        return formattedDate;
    }

    public static Date getFormattedFullDateAndTime(String month, String day, String year) {
        if (MM_DD_YYYY == null) {
            MM_DD_YYYY = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        }
        Date formattedDate = null;
        try {
            //Month and year hard coded
            formattedDate = MM_DD_YYYY.parse(month + "/" + day + "/" + year);
        } catch (ParseException e) {
            Log.e(TAG, "Unable to parse date " + e.getMessage());
        }

        return formattedDate;
    }

    public static long getFormattedFullDateAndTimeLong(String month, String day, String year) {
        Date formattedDate = getFormattedFullDateAndTime(month, day, year);
        return formattedDate != null ? formattedDate.getTime() : 0;
    }

    public static String getFormattedDate(Date date) {
        if (EEE_MMM_D == null) {
            EEE_MMM_D = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        }
        return EEE_MMM_D.format(date);
    }

    public static String getFormattedDate(long time) {
        Date date = new Date(time);
        return getFormattedDate(date);
    }

    public static String getFullFormattedDateAndTime(Date date) {
        if (EEE_D_MMM_YYYY_HH_MM_AAA == null) {
            EEE_D_MMM_YYYY_HH_MM_AAA = new SimpleDateFormat("EEE, d MMM yyyy HH:mm aaa", Locale.getDefault());
        }
        return EEE_D_MMM_YYYY_HH_MM_AAA.format(date);
    }

    public static String getTwelveHourTimeFromDate(Date date) {
        if (H_MM_A == null) {
            H_MM_A = new SimpleDateFormat("h:mm a", Locale.getDefault());
        }
        return H_MM_A.format(date);
    }

    /**
     * Date formatter are large objects.  We can release them in low memory
     * conditions since we will always be able to recreate them when needed.
     */
    public static void releaseResources() {
        MM_DD_YYYY_HH_MM = null;
        MM_DD_YYYY = null;
        EEE_MMM_D = null;
        EEE_D_MMM_YYYY_HH_MM_AAA = null;
        H_MM_A = null;
    }

    public static int getDayFromDate(Date date) {
        return getCalendar(date).get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthFromDate(Date date) {
        return getCalendar(date).get(Calendar.MONTH);
    }

    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Returns today's date with time set to 0
     * @return today's date
     */
    public static Date getZeroTimeDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Adds the specified number of days to the date provided
     * @param date the date to add the days to
     * @param days the number of days to add
     * @return the new date after adding the number of days
     */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    /**
     * @return the current year
     */
    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static long getTimeInMillis(Date startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+0500"));
        calendar.setTime(startTime);
        return calendar.getTimeInMillis();
    }

    public static long getCurrentTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+0500"));
        return calendar.getTimeInMillis();
    }
}

