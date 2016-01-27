package org.androidsummit.eventapp.model;

import org.androidsummit.eventapp.model.generic.Person;
import com.parse.ParseClassName;

import java.util.List;

/**
 * Represents a Speaker
 *
 * Created on 4/13/15.
 */
@ParseClassName("Speaker")
public class Speaker extends Person {

    public static final String BIO = "bio";

    public static final String TRACK = "track";

    public static final String INTERNAL_ID = "internalId";
    private int internalId;

    public Speaker() {
        // A default constructor is required
    }

    public List<String> getBio() {
        return (List<String>)get(BIO);
    }

    public String getTrack() {
        return getString(TRACK);
    }

    public int getInternalId() {
        return internalId;
    }

}
