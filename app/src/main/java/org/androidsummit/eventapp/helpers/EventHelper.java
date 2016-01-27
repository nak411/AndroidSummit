package org.androidsummit.eventapp.helpers;

import android.text.TextUtils;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.enums.EventCategory;
import org.androidsummit.eventapp.model.MySummitSession;
import org.androidsummit.eventapp.model.SummitSession;
import org.androidsummit.eventapp.helpers.person.DateHelper;
import org.androidsummit.eventapp.model.wrappers.SessionRowItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contains utility method for retrieving drawable ids for events
 * Type code key:
 * 1 - Food (breakfast/lunch)
 * 2 - Keynote
 * 3 - Development track
 * 4 - Design track
 * 5 - Other
 * <p/>
 * Created by Naveed on 5/1/15.
 */
public class EventHelper {

    public static final String TRACK_DEVELOP = "Develop";
    public static final String TRACK_DESIGN = "Design";
    public static final String KEY_NOTE = "Keynote";

    private static final String OTHER = "Other";

    /**
     * Returns the drawable id for the provided event category
     *
     * @param typeCode the category to supply icon for
     * @return id of the drawable
     */
    public static int getIconIdForTypeCode(int typeCode) {
        switch (typeCode) {
            case 1:
                return R.drawable.ic_food;
            case 2:
                return R.drawable.ic_key_note;
            case 3:
                return R.drawable.ic_develop;
            case 4:
                return R.drawable.ic_design;
            default:
                return R.drawable.ic_action_android;
        }
    }


    /**
     * Compares the summit session to date to return either a range of time startTime - endTime or
     * a single time string if the start time and end time is the same.
     *
     * @param summitSession the summit sesssion to use for formatting time
     * @return the formatted time string
     */
    public static String formatEventTime(SummitSession summitSession) {
        String date;
        if (summitSession.getStartTime().compareTo(summitSession.getEndTime()) == 0) {
            date = DateHelper.getTwelveHourTimeFromDate(summitSession.getStartTime());
        } else {
            date = DateHelper.getTwelveHourTimeFromDate(summitSession.getStartTime()) + " - " + DateHelper.getTwelveHourTimeFromDate(
                    summitSession.getEndTime());
        }
        return date;
    }

    /**
     * Creates a list with {@link SessionRowItem} which includes the headers along summit sessions in the chronological order.
     * <p/>
     * Note this lists assumes a session list sorted by date.  Passing an unsorted list will produce unexpected results.
     * This method iterates through the entire list so it might be necessary to call it from a background thread for a large data set.
     *
     * @param summitSessions the summit sessions to process
     * @return a list including headers for the provided sessions
     */
    public static List<SessionRowItem> generateRowItems(List<SummitSession> summitSessions) {
        List<SessionRowItem> items = new ArrayList<>();
        Date currStartTime = null;
        Date currEndTime = null;

        for (SummitSession session : summitSessions) {
            //Base case, it is the first item in the list
            if (currStartTime == null) {
                currStartTime = session.getStartTime();
                currEndTime = session.getEndTime();
                //Add header for this item
                items.add(new SessionRowItem(DateHelper.getTwelveHourTimeFromDate(currStartTime)));
                //Add the session under the header
                items.add(new SessionRowItem(session, true));
            } else {
                if (currEndTime.compareTo(session.getStartTime()) <= 0) {
                    //Update pointers
                    currStartTime = session.getStartTime();
                    currEndTime = session.getEndTime();
                    //Add a header for the next session since it will be after this session and can be placed into
                    items.add(new SessionRowItem(DateHelper.getTwelveHourTimeFromDate(currStartTime)));
                    items.add(new SessionRowItem(session, true));
                } else {
                    if (currEndTime.compareTo(session.getEndTime()) < 0) {
                        currEndTime = session.getEndTime();
                    }
                    //Group under same header
                    items.add(new SessionRowItem(session, true));
                }
            }
        }
        return items;

    }

    public static List<SessionRowItem> generateMySessionRowItems(List<MySummitSession> summitSessions) {
        List<SessionRowItem> items = new ArrayList<>();
        Date currStartTime = null;
        Date currEndTime = null;

        for (MySummitSession session : summitSessions) {
            //Base case, it is the first item in the list
            if (currStartTime == null) {
                currStartTime = session.getSummitSession().getStartTime();
                currEndTime = session.getSummitSession().getEndTime();
                //Add header for this item
                items.add(new SessionRowItem(DateHelper.getTwelveHourTimeFromDate(currStartTime)));
                //Add the session under the header
                items.add(new SessionRowItem(session.getSummitSession(), true));
            } else {
                if (currEndTime.compareTo(session.getSummitSession().getStartTime()) <= 0) {
                    //Update pointers
                    currStartTime = session.getSummitSession().getStartTime();
                    currEndTime = session.getSummitSession().getEndTime();
                    //Add a header for the next session since it will be after this session and can be placed into
                    items.add(new SessionRowItem(DateHelper.getTwelveHourTimeFromDate(currStartTime)));
                    items.add(new SessionRowItem(session.getSummitSession(), true));
                } else {
                    if (currEndTime.compareTo(session.getSummitSession().getEndTime()) < 0) {
                        currEndTime = session.getSummitSession().getEndTime();
                    }
                    //Group under same header
                    items.add(new SessionRowItem(session.getSummitSession(), true));
                }
            }
        }
        return items;

    }

    /**
     * Takes a list of string and appends 2 new lines at the end to create paragraphs
     *
     * @param description the list of strings to process
     * @return the formatted string with new lines appended.
     */
    public static String formatDescription(List<String> description) {
        StringBuilder sb = new StringBuilder();
        if (description != null) {
            String delim = "";
            for (String paragraph : description) {
                sb.append(delim).append(paragraph);
                delim = "\n\n";
            }
        }
        return sb.toString();
    }

    /**
     * Returns a string specifying the track for the provided type code
     *
     * @param typeCode an int specifying the type code
     * @return a string for the provided type code
     */
    public static String getTrackFromTypeCode(int typeCode) {
        switch (typeCode) {
            case 1:
                return "";
            case 2:
                return KEY_NOTE;
            case 3:
                return TRACK_DEVELOP;
            case 4:
                return TRACK_DESIGN;
            case 5:
                return OTHER;
        }
        return null;
    }

    public static int getColorForTypeCode(int typeCode) {
        switch (typeCode) {
            case 1:
                return R.color.cap_one_blue;
            case 2:
                return R.color.medium_black;
            case 4:
                return R.color.pink;
            case 5:
                return R.color.android_green;
            default:
                return R.color.blue;

        }
    }

    public static int getTypeCodeForTrack(String track) {
        if (!TextUtils.isEmpty(track)) {
            if (track.equalsIgnoreCase(TRACK_DESIGN)) {
                return 4;
            }
        }
        //Default to develop
        return 3;
    }
}
