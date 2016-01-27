package org.androidsummit.eventapp;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import org.androidsummit.eventapp.helpers.DataCacheTracker;
import org.androidsummit.eventapp.model.MySummitSession;
import org.androidsummit.eventapp.model.generic.Person;
import org.androidsummit.eventapp.model.SummitSession;
import org.androidsummit.eventapp.model.Speaker;
import org.androidsummit.eventapp.helpers.person.DateHelper;
import org.androidsummit.eventapp.model.trackers.Metadata;
import org.androidsummit.eventapp.tasks.MetadataCheckTask;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * The entry point of the application.  All global initialization should be done here.
 *
 * Created by Naveed on 3/10/15.
 */
public class MainApplication extends Application {

    //Enable for debugging
    public static final boolean DEVELOPER_MODE = false;

    @Override
    public void onCreate() {
        super.onCreate();

        initializeParse();
        subscribeToPush();
        enableStrictMode();
        if (!DataCacheTracker.isInitialLaunch(getApplicationContext())) {
            checkMetaDataState();
        }
    }

    private void checkMetaDataState() {
        //Fire off of data check task
        new MetadataCheckTask(getApplicationContext()).execute();
    }

    private void subscribeToPush() {
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "Successfully subscribed to the broadcast channel");
                } else {
                    Log.e("com.parse.push", "Failed to subscribe for push", e);
                }
            }
        });
    }

    private void enableStrictMode() {
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        }
    }

    private void initializeParse() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Person.class);
        ParseObject.registerSubclass(SummitSession.class);
        ParseObject.registerSubclass(Speaker.class);
        ParseObject.registerSubclass(Metadata.class);
        ParseObject.registerSubclass(MySummitSession.class);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        DateHelper.releaseResources();
    }
}
