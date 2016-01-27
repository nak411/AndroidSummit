package org.androidsummit.eventapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.androidsummit.eventapp.managers.DataManager;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Abstract fragment used for retrieving a SINGLE parse objects.  It does not define its own UI,
 * therefore the extending fragments must define their own view.
 * <p/>
 * Created by Naveed on 4/20/15.
 */
public abstract class ParseDataObjectRetrievalFragment<T extends ParseObject> extends Fragment
        implements DataManager.DataCallbacks {

    public static final String TAG = ParseDataObjectRetrievalFragment.class.getSimpleName();

    private DataManager mDataManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDataManager == null) {
            mDataManager = new DataManager(this, getActivity().getApplicationContext());
        }
    }

    /**
     * Subclasses are required to call this method to initiate data retrieval.
     * You must initialize your view before calling this method since the data request may return
     * instantly. This will invoke the standard {@link #onDataLoaded(ParseObject)} call back after
     * fetching the data specified by {@link #getQuery()}
     */
    public void retrieveData() {
        retrieveData(getQuery());
    }

    /**
     * Retrieves the First data object found for a specified query.
     *
     * @param query the query to use for retrieving object
     * @param requestCode the request code to use when invoking the callback.
     */
    public <E extends ParseObject> void retrieveDataObjectForQuery(ParseQuery<E> query, int requestCode) {
        mDataManager.retrieveFirstDataObject(query, requestCode);
    }

    /**
     * Callers do not need to specify where to obtain the data from.  This Fragment will attempt to retrieve data from the local data store
     * If no data is found, a request will be made to the server.
     */
    protected void retrieveData(final ParseQuery<T> query) {
        mDataManager.retrieveDataObject(query, getQueryObjectId(), getServerQuery());
    }

    /**
     * Retrieve data from server using the provided query
     *
     * @param query the query to use for retrieving data
     */
    protected void retrieveFromServer(ParseQuery<T> query) {
        mDataManager.retrieveDataObjectFromServer(query, getQueryObjectId());
    }

    /**
     * This call back is received from the data retriever which will create the same instance of
     * the object that was specified in the query. Therefore it safe to cast it to T
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E extends ParseObject> void onDataRetrieved(E obj) {
        onDataLoaded((T) obj);
    }

    /**
     * This call back is received from the data retriever which will create the same instance of
     * the object that was specified in the query. Therefore it safe to cast it to T
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E extends ParseObject> void onDataRetrieved(E obj, int requestCode) {
        onDataLoaded(obj, requestCode);
    }

    @Override
    public <E extends ParseObject> void onDataRetrieved(List<E> dataList, boolean isFromServer) {
        //Nothing to do here since this fragment only retrieves objects and not lists
    }

    @Override
    public void onDataRetrievalError() {
        onDataError();
    }

    @Override
    public void onDataRetrievalError(int requestCode) {
        onDataError(requestCode);
    }

    @Override
    public void onDataSaved(boolean onServer) {
        Log.w(TAG, "Subclass invoked data save but did not override callback: onDataSaved(boolean)");
    }

    @Override
    public void onDataSaveError() {
        Log.w(TAG, "Subclass invoked data save but did not override callback: onDataSaveError()");
    }

    @Override
    public void onDataDelete(boolean onServer) {
        Log.w(TAG, "Subclass invoked data delete but did not override callback: onDataDelete(boolean)");
    }

    @Override
    public void onDataDeleteError() {
        Log.w(TAG, "Subclass invoked data delete but did not override callback: onDataDeleteError()");
    }

    /**
     * Save data to local after retrieving from the server so we do not make a network request again.
     *
     * @param obj the retrieved data.
     */
    protected void saveToLocal(T obj) {
        mDataManager.saveToLocal(obj);
    }

    /**
     * Delete the provided object from local data store
     *
     * @param obj the object to delete
     */
    protected void deleteFromLocal(T obj) {
        mDataManager.deleteFromLocal(obj);
    }


    /**
     * @return the unique id of the object to retrieve
     */
    protected abstract String getQueryObjectId();

    /**
     * Invoked if data cannot be retrieved for any reason.
     */
    protected abstract void onDataError();

    /**
     * Invoked if the data for secondary query cannot be retrieved
     *
     * @param requestCode the request used for retrieving the data
     */
    protected void onDataError(int requestCode) {
        Log.w(TAG, "Subclass requested data but did not override callback: onDataError(int)");
    }

    /**
     * Invoked when data is successfully retrieved
     *
     * @param obj the data object to pass back.
     */
    protected abstract void onDataLoaded(T obj);

    /**
     * Invoked when data is successfully retrieved for a secondary query requested by the fragment
     *
     * @param obj the retrieved object
     * @param requestCode the request code for the query
     */
    protected void onDataLoaded(ParseObject obj, int requestCode){
        Log.w(TAG, "Subclass requested data but did not override callback: onDataLoaded(T, int)");
    }

    /**
     * Subclasses must define their query which will be used to retrieve data
     *
     * @return a parse query used for retrieving data
     */
    public abstract ParseQuery<T> getQuery();

    /**
     * Invoked if query on local data store fails.  Since parse has no way of resetting the localData store flag.  The caller must provide a
     * separate query in order to retrieve from server
     *
     * @return the query used to retrieve data from server.
     */
    public abstract ParseQuery<T> getServerQuery();
}
