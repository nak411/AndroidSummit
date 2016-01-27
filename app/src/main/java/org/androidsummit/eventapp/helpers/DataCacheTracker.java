package org.androidsummit.eventapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.androidsummit.eventapp.model.trackers.DataState;

/**
 * Contains helper methods for managing local data storage.
 *
 * Created by Naveed on 7/22/15.
 */
public class DataCacheTracker {

    /**
     * Sets the flag in shared preferences to show that data exists in local data store.
     *
     * @param context the context to use for fetching shared preferences.
     */
    public static void setDataExists(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean exists = preferences.getBoolean(DataState.DATA_EXISTS, false);
        //Only write if needed
        if (!exists) {
            preferences.edit().putBoolean(DataState.DATA_EXISTS, true).apply();
        }
    }

    /**
     * Sets the flag specifying that local data store has been cleared.
     *
     * @param context the context to use for retrieving shared preferences.
     */
    public static void setDataCleared(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean exists = preferences.getBoolean(DataState.DATA_EXISTS, false);
        //Only write if needed
        if (exists) {
            preferences.edit().putBoolean(DataState.DATA_EXISTS, false).apply();
        }
    }

    /**
     * Specifies whether data exists in local data store or not
     *
     * @param context the context to use for retrieving shared preferences
     *
     * @return true if local data store contains data, false other if there is no data.
     */
    public static boolean localDataExists(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(DataState.DATA_EXISTS, false);
    }

    /**
     *
     * @param context the context to use for shared preferences
     *
     * @return a boolean specifying with it is the initial launch or not
     */
    public static boolean isInitialLaunch(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(DataState.INITIAL_LAUNCH, true);
    }


    /**
     * Set the flag so it indicates that this is not the initial launch
     *
     * @param context the context to use for pulling shared preferences
     */
    public static void markInitialLaunchFlag(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.contains(DataState.INITIAL_LAUNCH)) {
            preferences.edit().putBoolean(DataState.INITIAL_LAUNCH, true).apply();
        }
    }
}
