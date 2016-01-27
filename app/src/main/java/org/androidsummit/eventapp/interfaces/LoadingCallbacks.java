package org.androidsummit.eventapp.interfaces;

import com.parse.ParseFile;

/**
 * Used for communicating loading callbacks between an activity and fragment.
 *
 * Created by Naveed on 9/21/15.
 */
public interface LoadingCallbacks {

    void showLoading();

    void hideLoading();

    void loadBackdropImage(ParseFile parseFile);
}
