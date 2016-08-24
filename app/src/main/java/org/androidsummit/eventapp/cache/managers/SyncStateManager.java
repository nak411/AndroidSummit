package org.androidsummit.eventapp.cache.managers;

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

    //Bits are set as follows
    // 1 = page requires updating
    // 0 = page does not require updating
    private static final int MAX_SHIFT_COUNT = 31;

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
     * Checks whether sync is required or not.  Sets the flag for each schedule page to true if sync is required. No action is taken other
     * wise
     *
     * @param context   the context to use for retrieving the shared preferences
     * @param pageCount the total number of pages in the schedule
     */
    public static void setupDataSyncIfNeeded(Context context, int pageCount) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //Check if sync is required
        boolean needsSync = retrieveSyncState(preferences);
        if (needsSync) {
            SharedPreferences.Editor editor = preferences.edit();
            int syncDaysGroupCounter = 0;
            //All schedule pages should sync with server next time the user opens them
            if (pageCount <= MAX_SHIFT_COUNT) {
                int flag = 1;
                //The value can be stored in one integer.  This will handle a max of 32 pages 0-31 bit indices.
                for (int i = 0; i < pageCount; i++) {
                    //Set all bits to 1 so the next time user opens the page, it updates
                    flag |= (1 << i);
                }
                editor.putInt(DataState.SYNC_DAY + syncDaysGroupCounter, flag);
                editor.apply();
            } else {
                //The value needs to be stored in multiple integers
                //TODO implement for now we are assuming that number of pages in schedule will be less than 32
            }
        }
    }

    /**
     * Sets the state of each flag to require sync so the next time page is opened, it'll pull
     * data from server
     *
     * @param context      the context to use for retrieving shared preference
     * @param requiresSync true if the data requires sync, false otherwise.
     */
    public static void setAllSyncFlags(Context context, boolean requiresSync) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(DataState.SYNC_MY_SCHEDULE, requiresSync);
        editor.putBoolean(DataState.SYNC_SCHEDULE, requiresSync);
        editor.putBoolean(DataState.SYNC_SPEAKERS, requiresSync);
        editor.apply();
    }

    private static boolean retrieveSyncState(SharedPreferences preferences) {
        if (preferences.contains(DataState.SYNC_SCHEDULE)) {
            //Not the first launch
            return preferences.getBoolean(DataState.SYNC_SCHEDULE, false);
        } else {
            return true;
        }

    }

}
