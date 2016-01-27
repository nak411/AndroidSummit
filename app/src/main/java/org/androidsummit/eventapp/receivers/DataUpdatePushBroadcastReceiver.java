package org.androidsummit.eventapp.receivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.androidsummit.eventapp.receivers.handlers.PushHandler;
import com.parse.ParsePushBroadcastReceiver;

/**
 * Broadcast receiver for receiving push notifications to trigger data updates
 *
 * Created by Naveed on 7/9/15.
 */
public class DataUpdatePushBroadcastReceiver extends ParsePushBroadcastReceiver {

    private static final String TAG = DataUpdatePushBroadcastReceiver.class.getSimpleName();

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        Log.d(TAG, "PUSH RECEIVED !!!");
        //Set the flags so the next time user opens this fragment, the data is retrieved from the server
        //instead of local storage
        PushHandler.handlePush(context, intent);
    }
}
