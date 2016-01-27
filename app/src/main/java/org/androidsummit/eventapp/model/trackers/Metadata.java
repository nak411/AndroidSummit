package org.androidsummit.eventapp.model.trackers;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Model for meta data
 *
 * Created by Naveed on 7/22/15.
 */
@ParseClassName("Metadata")
public class Metadata extends ParseObject {

    public static final String FLAG_TYPE = "flagType";

    public static final String VALUE = "value";

    public Metadata() {
        // A default constructor is required for parse object
    }

    public String getFlagType() {
        return getString(FLAG_TYPE);
    }

    public boolean getValue() {
        return getBoolean(VALUE);
    }

    public static class FlagType {

        public static final String FLAG_CLEAR_DATA = "clearData";
    }
}
