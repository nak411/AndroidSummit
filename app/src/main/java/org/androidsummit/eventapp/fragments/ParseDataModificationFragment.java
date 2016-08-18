package org.androidsummit.eventapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.parse.ParseObject;

import org.androidsummit.eventapp.managers.DataManager;

import java.util.List;

/**
 * Top level fragment class for modifying data.  This is a wrapper around the data manger to allow
 * easy management of lifecycle events.
 * <p/>
 * Created on 8/18/16.
 */
public class ParseDataModificationFragment extends Fragment implements DataManager.DataCallbacks {

    public static final String TAG = "DataModificationFrag";

    private DataManager mDataManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDataManager == null) {
            mDataManager = new DataManager(this, getContext().getApplicationContext());
        }
    }

    public void clearAllLocalData() {
        mDataManager.clearAllData();
    }

    @Override
    public void onDataSaved(boolean onServer) {

    }

    
    @Override
    public void onDataSaveError() {

    }

    /**
     * Invoked when data is successfully deleted
     * @param onServer true if data was removed from server, false if data was only removed from local data store
     */
    @Override
    public void onDataDelete(boolean onServer) {
        Log.w(TAG, "onDataDelete completed - Ignoring callback");
    }

    /**
     * Invoked when deletion fails
     */
    @Override
    public void onDataDeleteError() {
        Log.w(TAG, "onDataDeleteError - Ignoring callback");
    }

    @Override
    public <E extends ParseObject> void onDataRetrieved(List<E> dataList, boolean isFromServer) {

    }

    @Override
    public <E extends ParseObject> void onDataRetrieved(E obj) {

    }

    @Override
    public <E extends ParseObject> void onDataRetrieved(E obj, int requestCode) {

    }

    @Override
    public void onDataRetrievalError() {

    }

    @Override
    public void onDataRetrievalError(int requestCode) {

    }
}
