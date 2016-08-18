package org.androidsummit.eventapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.androidsummit.eventapp.cache.managers.DataManager;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Abstract fragment used for retrieving a list of parse objects.  It does not define its own UI, therefore the extending must
 * define their own view.
 *
 * Created on 4/20/15.
 */
public abstract class ParseDataListRetrievalFragment<T extends ParseObject> extends Fragment implements DataManager.DataCallbacks {

    public static final String TAG = ParseDataListRetrievalFragment.class.getSimpleName();

    private DataManager mDataManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDataManager == null) {
            mDataManager = new DataManager(this, getActivity().getApplicationContext());
        }
    }

    /**
     * Subclasses are required to call this method to initiate data retrieval.  You must initialize your view before calling this method
     * since the data request may return instantly.
     */
    public void retrieveData() {
        retrieveData(getQuery());
    }

    /**
     * Callers do not need to specify where to obtain the data from.  This Fragment will attempt to retrieve data from the local data store
     * If no data is found, a request will be made to the server to obtain data.
     */
    protected void retrieveData(final ParseQuery<T> query) {
        mDataManager.retrieveDataObjectList(query, getServerQuery());
    }

    protected void retrieveFromServer(final ParseQuery<T> query) {
        mDataManager.retrieveDataObjectListFromServer(query);
    }

    /**
     * Checks the local data base for the same query and clears out any data that may be in there but does not exists on server anymore.
     * Then adds/updates any data that may have changed on the server.
     *
     * @param serverList the list of objects obtained from the server
     */
    protected void syncLocalDataStore(final List<T> serverList) {
        mDataManager.syncLocalDataStore(serverList, getServerQuery());
    }

    /**
     * Save data to local after retrieving from the server so we do not make a network request again.
     *
     * @param list the retrieved data.
     */
    protected void saveToLocal(List<T> list) {
        mDataManager.saveToLocal(list);
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
    public void onDataRetrievalError(int requestCode) {
        onDataError(requestCode);
    }

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
     * Invoked if the data for secondary query cannot be retrieved
     *
     * @param requestCode the request used for retrieving the data
     */
    protected void onDataError(int requestCode) {
        Log.w(TAG, "Subclass requested data but did not override callback: onDataError(int)");
    }


    /**
     * Invoked if data cannot be retrieved for any reason.
     */
    protected abstract void onDataError();

    /**
     * Invoked when data is successfully retrieved
     *
     * @param list               the list of data
     * @param isLoadedFromServer a boolean specifying whether it was loaded from server
     */
    protected abstract void onDataLoaded(List<T> list, boolean isLoadedFromServer);

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

    /**
     * Invoked when data is successfully retrieved either from server or local data store.
     *
     * @param dataList     the list of data objects retrieved
     * @param isFromServer a boolean specifying whether data was retrieved from server or local data store
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E extends ParseObject> void onDataRetrieved(List<E> dataList, boolean isFromServer) {
        onDataLoaded((List<T>) dataList, isFromServer);
    }

    @Override
    public <E extends ParseObject> void onDataRetrieved(E data) {
        //Nothing to do here since only lists are retrieved by this fragment
    }

    @Override
    public void onDataRetrievalError() {
        onDataError();
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
}
