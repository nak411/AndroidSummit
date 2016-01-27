package org.androidsummit.eventapp.interfaces;

/**
 * Callback interface used for communicating between fragments and activities.
 *
 * Created by Naveed on 3/12/15.
 */
public interface FragmentCallbacks {

    void updateTitle(int sectionNumber);

    void setToolbarShadow(boolean showShadow);
}
