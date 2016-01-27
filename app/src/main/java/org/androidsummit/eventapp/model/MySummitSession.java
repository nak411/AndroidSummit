package org.androidsummit.eventapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Represents MySummitSession
 *
 */
@ParseClassName("MySummitSession")
public class MySummitSession extends ParseObject {

    private final String TAG = MySummitSession.class.getSimpleName();

    public static final String START_TIME = "MySummitSessionStartTime";

    public static final String SESSION_ID = "MySummitSessionId";

    public static final String SESSION = "MySummitSessionColumn";

    public MySummitSession() {
        //Required default constructor
    }

    public MySummitSession(Date startTime, SummitSession summitSession) {
        put(SESSION, summitSession);
        put(START_TIME, startTime);
        put(SESSION_ID, summitSession.getObjectId());
    }

    public SummitSession getSummitSession() {
        return (SummitSession) get(SESSION);
    }

    public void setSummitSession(SummitSession summitSession) {
        put(SESSION, summitSession);
        put(SESSION_ID, summitSession.getObjectId());
    }

    public Date getStartTime() {
        return (Date) get(START_TIME);
    }

    public void setStartTime(Date startTime) {
        put(START_TIME, startTime);
    }

    public String getSessionId() {
        return (String) get(SESSION_ID);
    }
}
