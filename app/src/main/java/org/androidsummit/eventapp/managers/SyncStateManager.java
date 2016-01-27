package org.androidsummit.eventapp.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.androidsummit.eventapp.model.trackers.DataState;

/**
 * Helper class for managing the sync state of data
 * <p/>
 * Created on 7/10/15.
 */
public class SyncStateManager {

    /**
     * Determines whether the schedule requires sync or not
     *
     * @param context the context to use for retrieving shared preferences
     * @return true if the schedule requires sync, false other wise
     */
    public static boolean scheduleRequiresSync(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.contains(DataState.SYNC_SCHEDULE)) {
            //Not the first launch
            return preferences.getBoolean(DataState.SYNC_SCHEDULE, false);
        } else {
            //First launch should sync schedule
            return true;
        }
    }

    /**
     * Specifies whether my schedule needs to initialized or not
     *
     * @param context the context to use for fetching shared preferences
     * @return true if my schedule should be initialized false otherwise.
     */
    public static boolean shouldInitializeMySchedule(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //Not the first launch
        return !preferences.contains(DataState.SYNC_MY_SCHEDULE) || preferences.getBoolean(DataState.SYNC_MY_SCHEDULE, false);
    }

    /**
     * Generic requires sync check for a given key.
     *
     * @param context the context to use for retrieving shared preferences
     * @param key     the key to check against
     * @return true if the data requires sync, false otherwise.
     */
    public static boolean requiresSync(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return !preferences.contains(key) || preferences.getBoolean(key, true);
    }

    /**
     * Updates the sync state for the provided key with the provided value.
     *
     * @param context the context to use for retrieving shared preferences.
     * @param key     the key to update
     * @param value   the new value to set
     */
    public static void updateSyncState(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Marks the schedule state to be synced
     *
     * @param context      the context to use for retrieving shared preferences.
     * @param requiresSync a boolean indicating whether the schedule requires sync or not
     */
    public static void setScheduleRequiresSync(Context context, boolean requiresSync) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //If preferences do not contain the key then we write it right away ignoring the second condition
        //If there is a key
        //      false value indicates that schedule does not require sync.
        //      true indicates that schedule needs to be synced
        //Here we want to avoid extra rights so we only write the value when schedule needs to be re-synced
        if (!preferences.contains(DataState.SYNC_SCHEDULE) || (preferences.getBoolean(DataState.SYNC_SCHEDULE, false) != requiresSync)) {
            preferences.edit().putBoolean(DataState.SYNC_SCHEDULE, requiresSync).apply();
        }
    }

    /**
     * Marks the my schedule state
     *
     * @param context      the context to use for retrieving shared preferences.
     * @param requiresSync true if my schedule requires initialization, false other wise.
     */
    public static void setInitializeMySchedule(Context context, boolean requiresSync) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //If preferences do not contain the key then we write it right away ignoring the second condition
        //If there is a key
        //      false value indicates that schedule does not require sync.
        //      true indicates that schedule needs to be synced
        //Here we want to avoid extra rights so we only write the value when schedule needs to be re-synced
        if (!preferences.contains(DataState.SYNC_MY_SCHEDULE)) {
            preferences.edit().putBoolean(DataState.SYNC_MY_SCHEDULE, requiresSync).apply();
        }
    }

}
