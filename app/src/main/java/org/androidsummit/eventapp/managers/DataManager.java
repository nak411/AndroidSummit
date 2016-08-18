package org.androidsummit.eventapp.managers;

import android.content.Context;
import android.util.Log;

import org.androidsummit.eventapp.helpers.DataCacheTracker;
import org.androidsummit.eventapp.utils.DebugUtils;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manages data retrieval and syncing.  This class will weakly hold on to any callbacks provided to it to prevent leaks during rotation. The
 * data retrieved is cached therefore a subsequent call with same query will return instantly.
 * <p/>
 * This class requires a context to manage data state therefore an illegal argument exception will be thrown if the provided call backs are
 * not an instance of context
 * <p/>
 * The generic type T is defined on method level instead of class level to prevent unchecked lint warnings for the client.
 * <p/>
 * Created on 7/9/15.
 */
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();

    private static final String OBJECT_ID = "objectId";

    private WeakReference<DataCallbacks> mCallbackRef;

    private Context mContext;

    /**
     * Create a new data manager with the provided callbacks
     *
     * @param dataCallbacks the call backs to report for various data load/save events.  Note that
     *                      these calls backs must extend from context
     */
    public DataManager(DataCallbacks dataCallbacks) {

        if (dataCallbacks instanceof Context) {
            mCallbackRef = new WeakReference<>(dataCallbacks);
        } else {
            throw new IllegalArgumentException("The callbacks must be subclass of context");
        }
    }

    /**
     * Creaate a new data manager with the provided callbacks and context
     *
     * @param dataCallbacks the data callbacks to use
     * @param context       a context object
     */
    public DataManager(DataCallbacks dataCallbacks, Context context) {
        if (context != null) {
            mCallbackRef = new WeakReference<>(dataCallbacks);
            mContext = context.getApplicationContext();
        } else {
            throw new IllegalArgumentException("Context cannot be null");
        }
    }

    /**
     * Attempt to retrieve data list from the local data store. If no data is found, a request will be made to the server to get data.
     *
     * @param query       the query used for retrieving the data object
     * @param serverQuery the query used fro retrieving the data from the server if no data is found locally
     */
    public <T extends ParseObject> void retrieveDataObjectList(ParseQuery<T> query, final ParseQuery<T> serverQuery) {
        //Retrieve from local data store first
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<T>() {
            @Override
            public void done(List<T> list, ParseException e) {
                if (e == null) {
                    //Query was successful
                    if (list != null && !list.isEmpty()) {
                        //Successfully retrieved data
                        //onDataLoaded(list, false);
                        reportDataListLoaded(list, false);
                    } else {
                        //No data was found retrieve from server
                        DebugUtils.log(TAG, "No data found in local data store, retrieving from server");
                        retrieveDataObjectListFromServer(serverQuery);
                    }
                } else {
                    //Query failed on local data store or local data store does not exist yet. Try to retrieve data from server.
                    Log.e(TAG, "Query failed or data store doesn't exist, attempting to retrieve from server", e);
                    retrieveDataObjectListFromServer(serverQuery);
                }
            }
        });
    }

    /**
     * Attempt to retrieve data object from the local data store. If no data is found, a request will be made to the server to get data.
     *
     * @param query         the query used for retrieving the data object
     * @param queryObjectId the id of the object to retrieve
     * @param serverQuery   the query used fro retrieving the data from the server if no data is found locally
     */
    public <T extends ParseObject> void retrieveDataObject(ParseQuery<T> query, final String queryObjectId,
                                                           final ParseQuery<T> serverQuery) {
        //Retrieve from local data store first
        query.fromLocalDatastore();
        query.getInBackground(queryObjectId, new GetCallback<T>() {
            @Override
            public void done(T obj, ParseException e) {
                if (e == null) {
                    //Query was successful
                    if (obj != null) {
                        //Successfully retrieved data
                        reportDataLoaded(obj);
                    } else {
                        //No data was found retrieve from server
                        DebugUtils.log(TAG, "No data found in local data store, retrieving from server");
                        retrieveDataObjectFromServer(serverQuery, queryObjectId);
                    }
                } else {
                    //Query failed on local data store or local data store does not exist yet. Try to retrieve data from server.
                    Log.e(TAG, "Query failed or data store doesn't exist, attempting to retrieve from server", e);
                    retrieveDataObjectFromServer(serverQuery, queryObjectId);
                }
            }
        });
    }

    /**
     * Retrieve data from server using the provided query and object id
     *
     * @param query         the query to use for retrieving data.
     * @param queryObjectId the id of the object to retrieve.
     */
    public <T extends ParseObject> void retrieveDataObjectFromServer(ParseQuery<T> query, String queryObjectId) {
        //Retrieve from server using network
        query.getInBackground(queryObjectId, new GetCallback<T>() {
            @Override
            public void done(T obj, ParseException e) {
                if (e == null) {
                    //Query was successful
                    if (obj != null) {
                        //Successfully retrieved data
                        reportDataLoaded(obj);
                        saveToLocal(obj, false);
                    } else {
                        //No data was found
                        DebugUtils.log(TAG, "No data found on server for this query");
                        reportError();
                    }
                } else {
                    //Query failed there was a network error or data does not exist.
                    Log.e(TAG, "Query failed or data store doesn't exist, attempting to retrieve from server", e);
                    reportError();
                }
            }
        });
    }

    /**
     * Retrieves the first object found for the provided the query. Null will be returned if no object
     * is found.
     *
     * @param query       the query to use for retrieving the object
     * @param requestCode the request for notifying the caller
     */
    public <T extends ParseObject> void retrieveFirstDataObject(ParseQuery<T> query, final int requestCode) {
        query.getFirstInBackground(new GetCallback<T>() {
            @Override
            public void done(T obj, ParseException e) {
                //Query was successful
                if (e == null) {
                    //Successfully retrieved data
                    //TODO test the request code for simultaneous calls from different threads
                    reportDataLoaded(obj, requestCode);
                } else {
                    //No data object found for the provided query
                    reportError(requestCode);
                    DebugUtils.log(TAG, "No data found for this query, request code: " + requestCode);
                }

            }
        });
    }

    /**
     * Retrieve data from server using the provided query and object id
     *
     * @param query the query to use for retrieving data.
     */
    public <T extends ParseObject> void retrieveDataObjectListFromServer(final ParseQuery<T> query) {
        //Retrieve from network
        query.findInBackground(new FindCallback<T>() {
            @Override
            public void done(List<T> list, ParseException e) {
                if (e == null) {
                    //Query was successful
                    if (list != null && !list.isEmpty()) {
                        //Successfully retrieved data
                        reportDataListLoaded(list, true);
                        syncLocalDataStore(list, query);
                        // saveToLocal(list);
                    } else {
                        //No data was found
                        DebugUtils.log(TAG, "No data found on server for this query");
                        reportError();
                    }
                } else {
                    //Query failed there was a network error or data does not exist.
                    Log.e(TAG, "Query failed or data store doesn't exist, attempting to retrieve from server", e);
                    reportError();
                }
            }
        });
    }

    /**
     * Saves the provided object to local data store
     *
     * @param obj the object to save to local data store
     */
    public <T extends ParseObject> void saveToLocal(final T obj) {
        saveToLocal(obj, true);
    }

    private <T extends ParseObject> void saveToLocal(final T obj, final boolean invokeCallback) {
        obj.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    DebugUtils.log(TAG, "Successfully saved data to local data store.");
                    Context context = retrieveContext();
                    if (context != null) {
                        DataCacheTracker.setDataExists(context);
                    }
                    if (invokeCallback) {
                        reportDataSaved(obj, false);
                    }
                } else {
                    Log.e(TAG, "Failed to save data to local data store", e);
                    if (invokeCallback) {
                        reportDataSaveError();
                    }
                }
            }
        });
    }

    /**
     * Saves the provided list to local data store
     *
     * @param list the list of objects to save
     */
    public <T extends ParseObject> void saveToLocal(List<T> list) {
        ParseObject.pinAllInBackground(list, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    DebugUtils.log(TAG, "Successfully saved data to local data store.");
                    Context context = retrieveContext();
                    if (context != null) {
                        DataCacheTracker.setDataExists(context);
                    }
                    reportDataSaved(null, false);
                } else {
                    Log.e(TAG, "Failed to save data to local data store", e);
                    reportDataSaveError();
                }
            }
        });
    }

    public <T extends ParseObject> void deleteFromLocal(T obj) {
        obj.unpinInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    reportDataDeleted(false);
                } else {
                    Log.e(TAG, "Failed to delete data in local data store");
                    reportDataDeleteError();
                }
            }
        });
    }

    private Context retrieveContext() {
        DataCallbacks callbacks = getValidCallBacks();
        if (mCallbackRef != null) {
            return mContext;
        } else if (callbacks != null && callbacks instanceof Context) {
            return (Context) callbacks;
        }
        return null;
    }

    /**
     * Clears the local data store.
     * <p/>
     * Note: Use this method with caution as it will clear everything from the local data store.
     */
    public void clearAllData() {
        ParseObject.unpinAllInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Context context = retrieveContext();
                    reportDataDeleted(false);
                    if (context != null) {
                        DataCacheTracker.setDataCleared(context);
                    }
                    DebugUtils.log(TAG, "Successfully cleared database");
                } else {
                    Log.e(TAG, "Failed to clear database", e);
                    reportDataDeleteError();
                }
            }
        });
    }

    /**
     * Syncs the data obtained from the server wit the local data store
     *
     * @param serverList the list to use for syncing
     * @param query      the query used for obtaining the list
     */
    public <T extends ParseObject> void syncLocalDataStore(final List<T> serverList, ParseQuery<T> query) {

        query.fromLocalDatastore();
        query.whereNotContainedIn(OBJECT_ID, collectValues(serverList));
        query.findInBackground(new FindCallback<T>() {
            @Override
            public void done(List<T> list, ParseException e) {
                if (e == null) {
                    if (list != null && !list.isEmpty()) {
                        updateLocalDataStore(serverList);
                    } else {
                        saveToLocal(serverList);
                    }
                } else {
                    Log.d("TAG", "No results found in local data store or an error occurred", e);
                    saveToLocal(serverList);
                }
            }
        });
    }

    private <T extends ParseObject> Collection<?> collectValues(List<T> serverList) {
        List<String> objIds = new ArrayList<>();
        for (T obj : serverList) {
            objIds.add(obj.getObjectId());
        }
        return objIds;
    }

    private <T extends ParseObject> void updateLocalDataStore(final List<T> serverList) {
        ParseObject.unpinAllInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error removing from local data store", e);
                }
                saveToLocal(serverList);
            }
        });
    }

    private void reportError() {
        DataCallbacks callbacks = getValidCallBacks();
        if (callbacks != null) {
            callbacks.onDataRetrievalError();
        }
    }

    private void reportError(int requestCode) {
        DataCallbacks callbacks = getValidCallBacks();
        if (callbacks != null) {
            callbacks.onDataRetrievalError(requestCode);
        }
    }

    private <T extends ParseObject> void reportDataLoaded(T obj) {
        DataCallbacks callbacks = getValidCallBacks();
        if (callbacks != null) {
            callbacks.onDataRetrieved(obj);
        }
    }

    private <T extends ParseObject> void reportDataLoaded(T obj, int requestCode) {
        DataCallbacks callbacks = getValidCallBacks();
        if (callbacks != null) {
            callbacks.onDataRetrieved(obj, requestCode);
        }
    }

    private <T extends ParseObject> void reportDataListLoaded(List<T> list, boolean isFromServer) {
        DataCallbacks callbacks = getValidCallBacks();
        if (callbacks != null) {
            callbacks.onDataRetrieved(list, isFromServer);
        }
    }

    private <T extends ParseObject> void reportDataSaved(T obj, boolean onServer) {
        DataCallbacks callbacks = getValidCallBacks();
        if (callbacks != null) {
            callbacks.onDataSaved(onServer);
        }
    }

    private void reportDataSaveError() {
        DataCallbacks callbacks = getValidCallBacks();
        if (callbacks != null) {
            callbacks.onDataSaveError();
        }
    }

    private void reportDataDeleted(boolean onServer) {
        DataCallbacks callbacks = getValidCallBacks();
        if (callbacks != null) {
            callbacks.onDataDelete(onServer);
        }
    }

    private void reportDataDeleteError() {
        DataCallbacks callbacks = getValidCallBacks();
        if (callbacks != null) {
            callbacks.onDataDeleteError();
        }
    }

    private DataCallbacks getValidCallBacks() {
        if (mCallbackRef != null) {
            return mCallbackRef.get();
        }
        return null;
    }

    /**
     * Callbacks used for reporting back to the client after data has loaded/modified.
     * <p/>
     * All callbacks are invoked with same type that the query uses therefore it is safe to cast the returned object to the query type
     */
    public interface DataCallbacks {

        /**
         * Invoked when data is saved successfully
         *
         * @param onServer true if it was saved on server, false if it was saved locally
         */
        void onDataSaved(boolean onServer);

        /**
         * Invoked if there was an error saving data
         */
        void onDataSaveError();

        /**
         * Invoked if data was removed successfully
         *
         * @param onServer true if data was removed from server, false if data was only removed from
         *                 local storage
         */
        void onDataDelete(boolean onServer);

        /**
         * Invoked if there was an error deleting data
         */
        void onDataDeleteError();

        /**
         * Invoked when data is successfully retrieved
         *
         * @param dataList     the list of data objects retrieved
         * @param isFromServer a boolean specifying whether data was retrieved from server or local data store
         */
        <E extends ParseObject> void onDataRetrieved(List<E> dataList, boolean isFromServer);

        /**
         * Invoked when the data object is retrieved
         *
         * @param obj the retrieved object
         */
        <E extends ParseObject> void onDataRetrieved(E obj);

        /**
         * Invoked when the data object is retrieved for a specified request code
         *
         * @param obj the retrieved object
         */
        <E extends ParseObject> void onDataRetrieved(E obj, int requestCode);

        /**
         * Invoked if there is an error while trying to retrieve data
         */
        void onDataRetrievalError();

        /**
         * Invoked if there is an error while trying to retrieve data
         *
         * @param requestCode the request to use for the query
         */
        void onDataRetrievalError(int requestCode);
    }
}
