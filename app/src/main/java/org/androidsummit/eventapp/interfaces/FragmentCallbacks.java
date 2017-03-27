package org.androidsummit.eventapp.interfaces;

/**
 * Callback interface used for communicating between fragments and activities.
 *
 * Created by Naveed on 3/12/15.
 */
public interface FragmentCallbacks {

    void updateTitle(int sectionNumber);

    /**
     * Enables/disables the toolbar shadow for the main activity
     */
    void setToolbarShadow(boolean showShadow);

    /**
     * Determines whether the schedule is single day or multi day
     * @return true if the schedule is multi day, false if its not
     */
    boolean isMultiDay();
}
