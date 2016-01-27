package org.androidsummit.eventapp.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Represents a SummitSession
 *
 * Created on 3/10/15.
 */
@ParseClassName("SummitSession")
public class SummitSession extends ParseObject{
    
    public static final String TITLE= "title";
    public static final String LOCATION = "location";
    public static final String DESCRIPTION = "description";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String TYPE_CODE = "typeCode";
    public static final String IMAGE = "image";
    public static final String SPEAKER_NAMES = "speakerNames";
    public static final String SPEAKERS = "speakers";
    
    public SummitSession(){
        //Required default constructor 
    }

    public String getTitle() {
        return getString(TITLE);
    }

    public String getLocation() {
        return getString(LOCATION);
    }

    public List<String> getDescription() {
        Object obj = get(DESCRIPTION);
        return obj == null ? null : (List<String>) obj;
    }

    public Date getStartTime() {
        return getDate(START_TIME);
    }

    public Date getEndTime() {
        return getDate(END_TIME);
    }

    public int getTypeCode() {
        return getInt(TYPE_CODE);
    }

    public void setTypeCode(int typeCode) {
        put(TYPE_CODE, typeCode);
    }

    public ParseFile getImage() {
        return getParseFile(IMAGE);
    }

    public List<String> getSpeakerNames() {
        return getList(SPEAKER_NAMES);
    }

    /**
     * This is a relational list, when querying for a session, this object will need to be
     * fetched separately because Parse does lazy loading.  Calling getPresenters without querying
     * for it could potentially throw a null pointer or a image_class cast exception;
     * @return list of presenters if present.
     */
    public List<Speaker> getSpeakers() {
        Object obj = get(SPEAKERS);
        return obj == null ? null : (List<Speaker>) obj;
    }
}
