package org.androidsummit.eventapp.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Window;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.helpers.DataCacheTracker;
import org.androidsummit.eventapp.managers.DataManager;
import com.parse.ParseObject;

import java.util.List;


/**
 * Simple static screen that is displayed if the app is remotely wiped.
 */
public class EventEndedActivity extends FragmentActivity implements DataManager.DataCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_event_ended);
        //Clear local data if needed
        clearData();
    }

    private void clearData() {
        if (DataCacheTracker.localDataExists(this)) {
            DataManager dataManager = new DataManager(this);
            dataManager.clearAllData();
        }
    }

    @Override
    public void onDataSaved(boolean onServer) {

    }

    @Override
    public void onDataSaveError() {

    }

    @Override
    public void onDataDelete(boolean onServer) {

    }

    @Override
    public void onDataDeleteError() {

    }

    @Override
    public <E extends ParseObject> void onDataRetrieved(List<E> dataList, boolean isFromServer) {
        //Nothing to do
    }

    @Override
    public <E extends ParseObject> void onDataRetrieved(E obj) {
        //Nothing to do
    }

    @Override
    public <E extends ParseObject> void onDataRetrieved(E obj, int requestCode) {
        //No-op
    }

    @Override
    public void onDataRetrievalError() {
        //Nothing to do
    }

    @Override
    public void onDataRetrievalError(int requestCode) {
        //No-op
    }
}
