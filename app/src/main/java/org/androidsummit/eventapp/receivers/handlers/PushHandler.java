package org.androidsummit.eventapp.receivers.handlers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.androidsummit.eventapp.model.trackers.DataState;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parses the push data and sets appropriate flags as needed.
 *
 * Handles push with following format:
 *      {
 *           "dataState":{
 *               "clearData": false
 *           },
 *           "syncState":{
 *               "syncSchedule" : true,
 *               "syncSpeakers": true
 *           }
 *       }
 *
 * Created on 7/21/15.
 */
public class PushHandler {

    private static final String DATA_KEY = "com.parse.Data";

    private static final String TAG = PushHandler.class.getSimpleName();

    public static void handlePush(Context context, Intent intent) {

        JSONObject pushData = retrievePushData(intent);
        if (pushData != null) {
            setDataStateFlags(context, pushData);
        }
        Log.d(TAG, pushData != null ? pushData.toString() : "");
    }

    private static void setDataStateFlags(Context context, JSONObject pushData) {
        updateDataState(context, pushData.optJSONObject(DataState.DATA_STATE));
        updateSyncState(context, pushData.optJSONObject(DataState.SYNC_STATE));
    }

    private static void updateSyncState(Context context, JSONObject objSyncState) {
        if (objSyncState != null) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putBoolean(DataState.SYNC_SCHEDULE, objSyncState.optBoolean(DataState.SYNC_SCHEDULE));
            editor.putBoolean(DataState.SYNC_SPEAKERS, objSyncState.optBoolean(DataState.SYNC_SPEAKERS));
            editor.apply();
        }
    }

    private static void updateDataState(Context context, JSONObject objDataState) {
        if (objDataState != null) {
            boolean clearData = objDataState.optBoolean(DataState.CLEAR_DATA);
            if (clearData) {
                //Ony modify shared prefs if data needs to be wiped
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putBoolean(DataState.CLEAR_DATA, true);
                editor.apply();
            }

        }
    }

    public static JSONObject retrievePushData(Intent intent) {
        JSONObject objPushData = null;
        if (intent.hasExtra(DATA_KEY)) {
            String strPushData = intent.getStringExtra(DATA_KEY);
            if (!TextUtils.isEmpty(strPushData)) {
                try {
                    objPushData = new JSONObject(strPushData);
                } catch (JSONException e) {
                    Log.e(TAG, "Unable to convert push data to json " + strPushData, e);
                }
            }
        }
        return objPushData;
    }
}
