package org.androidsummit.eventapp.model.enums;

/**
 * Flag for specifying the type of a {@link SessionRowViewType}
 *
 * Created by Naveed on 9/16/15.
 */
public enum SessionRowViewType {

    HEADER (0),
    SESSION (1);

    private final int val;

    SessionRowViewType(int i) {
        val = i;
    }

    public int getVal() {
        return val;
    }
}
