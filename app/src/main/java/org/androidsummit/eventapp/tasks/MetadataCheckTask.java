package org.androidsummit.eventapp.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.androidsummit.eventapp.model.trackers.DataState;
import org.androidsummit.eventapp.model.trackers.Metadata;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Task for checking whether the data requires an update or not.
 *
 * Created by Naveed on 7/22/15.
 */
public class MetadataCheckTask extends AsyncTask<Void, Void, List<Metadata>> {

    private static final String TAG = MetadataCheckTask.class.getSimpleName();

    private WeakReference<Context> mWeakRef;

    private WeakReference<MetaDataCheckCallbacks> mCallbacks;

    public MetadataCheckTask(Context context) {
        mWeakRef = new WeakReference<>(context);
    }

    public MetadataCheckTask(MetaDataCheckCallbacks callbacks, Context context) {
        this(context);
        mCallbacks = new WeakReference<>(callbacks);
    }

    @Override
    protected List<Metadata> doInBackground(Void... params) {
        if (requiresCheck()) {
            ParseQuery<Metadata> query = ParseQuery.getQuery(Metadata.class);
            try {
                List<Metadata> metadataList = query.find();
                if (metadataList != null) {
                    for (Metadata metadata : metadataList) {
                        handleMetadata(metadata);
                    }
                    setLastUpdate();
                }
                return metadataList;
            } catch (ParseException e) {
                Log.e(TAG, "Unable to retrieve meta data", e);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Metadata> metadata) {
        if (mCallbacks != null) {
            MetaDataCheckCallbacks callbacks = mCallbacks.get();
            if (callbacks != null) {
                callbacks.onCheckComplete(metadata);
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    //@SuppressLint the method is already on a background thread
    private void setLastUpdate() {
        Context context = getContext();
        if (context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            preferences.edit().putLong(DataState.METADATA_CHECK_TIME_STAMP, System.currentTimeMillis()).commit();
        }
    }

    @SuppressLint("CommitPrefEdits")
    //@SuppressLint the method is already on a background thread
    private void handleMetadata(Metadata metadata) {
        if (metadata.getFlagType().equals(Metadata.FlagType.FLAG_CLEAR_DATA)) {
            boolean clearData = metadata.getValue();
            Context context = getContext();
            if (clearData && context != null) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                preferences.edit().putBoolean(DataState.CLEAR_DATA, true).commit();
            }
        }
    }

    private Context getContext() {
        if (mWeakRef != null) {
            return mWeakRef.get();
        }
        return null;
    }

    private boolean requiresCheck() {
        Context context = getContext();
        if (context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            long timeStamp = preferences.getLong(DataState.METADATA_CHECK_TIME_STAMP, -1);
            if (timeStamp == -1 || !isWithinOneDay(timeStamp)) {
                return true;
            }
        }

        return false;
    }


    private boolean isWithinOneDay(long timeStamp) {
        long currTime = System.currentTimeMillis();
        return (currTime - timeStamp) <= TimeUnit.DAYS.toMillis(1);
    }

    public interface MetaDataCheckCallbacks {

        void onCheckComplete(List<Metadata> metadata);
    }
}
