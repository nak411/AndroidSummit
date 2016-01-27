package org.androidsummit.eventapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.androidsummit.eventapp.interfaces.LoadingCallbacks;
import com.parse.ParseObject;

/**
 * Base class for details fragment.  Handles view inflation, data retrieval and loading callbacks
 *
 * Created on 9/21/15.
 */
public abstract class DetailsFragment<T extends ParseObject> extends ParseDataObjectRetrievalFragment<T> {

    protected static final String DATA_KEY = "data.key";

    protected LoadingCallbacks mCallbacks;

    private T mData;

    protected View mView;

    /**
     * Define your UI based on the data
     *
     * @param mData the data retrieved from server/cache
     */
    protected abstract void populateViews(T mData);

    /**
     * The id of the layout to inflate
     * @return the ide of the layout to inflate
     */
    protected abstract int getLayoutId();

    /**
     * Define all the requests that need to be made for retrieving data
     */
    protected abstract void initiateDataRequests();

    /**
     * Initialize any views that do not depend on data fetching
     */
    protected abstract void setupPreDataLoadViews();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate layout
        // Inflate the layout for this fragment
        mView = inflater.inflate(getLayoutId(), container, false);

        //Retrieve data if we are not restoring from a previous state
        if (savedInstanceState != null && mData != null) {
            //Populate view with existing data
            populateViews(mData);
        } else {
            //Initiate data request
            if (mCallbacks != null) {
                mCallbacks.showLoading();
            }
            initiateDataRequests();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mData == null) {
            setupPreDataLoadViews();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null && context instanceof LoadingCallbacks) {
            mCallbacks = (LoadingCallbacks) context;
        } else {
            throw new IllegalArgumentException("Hosting activity must implement loading callbacks");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    protected void onDataError() {
        if (mCallbacks != null) {
            mCallbacks.hideLoading();
        }
        Log.e(TAG, "Unable retrieve details data");
    }

    @Override
    protected void onDataLoaded(T data) {
        if (mCallbacks != null) {
            mCallbacks.hideLoading();
        }
        mData = data;
        populateViews(mData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mView = null;
    }

}
