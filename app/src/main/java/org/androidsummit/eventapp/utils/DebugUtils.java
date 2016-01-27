package org.androidsummit.eventapp.utils;

import android.util.Log;

/**
 * Wrapper around logger.
 *
 * Created by Naveed on 4/20/15.
 */
public class DebugUtils {

    public static final boolean LOGGING_ENABLED = true;

    public static void log(String tag, String msg) {
        if (LOGGING_ENABLED) {
            Log.d(tag, msg);
        }
    }
}
