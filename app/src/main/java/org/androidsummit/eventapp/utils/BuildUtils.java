package org.androidsummit.eventapp.utils;

import android.os.Build;

/**
 * Created on 9/28/15.
 */
public class BuildUtils {

    public static boolean hasAPI17() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }
}
