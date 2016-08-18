package org.androidsummit.eventapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.BuildConfig;

/**
 * Created on 9/28/15.
 */
public class BuildUtils {

    private static final String APP_VERSION = "org.summit.app.version";

    /**
     * Determines if the OS is running API17 or not
     * @return true if the device has API 17 or greater
     */
    public static boolean hasAPI17() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * @return the current version of the application
     */
    public static int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    /**
     * @param context the context to use checking against shared preferences
     * @return true if the application needs to be upgraded, false otherwise
     */
    public static boolean requiresUpgrade(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int currentVersion = getVersionCode();
        int previousVersion = prefs.getInt(APP_VERSION, -1);

        return currentVersion != previousVersion;
    }

    /**
     * Sets the flag in shared preference, specifying that the upgrade was successfully completed.
     *
     * @param context the context to use for retrieving shared preferences
     */
    public static void setUpgradeComplete(Context context) {

        SharedPreferences  prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int currentVersion = getVersionCode();
        prefs.edit().putInt(APP_VERSION, currentVersion).apply();
    }
}
