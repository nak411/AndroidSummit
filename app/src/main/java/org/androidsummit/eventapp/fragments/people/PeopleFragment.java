package org.androidsummit.eventapp.fragments.people;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.adapter.PeopleRecyclerAdapter;
import org.androidsummit.eventapp.enums.PersonType;
import org.androidsummit.eventapp.fragments.ParseDataListRetrievalFragment;
import org.androidsummit.eventapp.interfaces.AdapterCallbacks;
import org.androidsummit.eventapp.interfaces.FragmentCallbacks;
import org.androidsummit.eventapp.managers.SyncStateManager;
import org.androidsummit.eventapp.model.generic.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract fragment for handling Person call backs. <p/> Uses the {@link PeopleRecyclerAdapter}
 * for its layout
 */
public abstract class PeopleFragment<T extends Person> extends ParseDataListRetrievalFragment<T>
        implements SwipeRefreshLayout.OnRefreshListener, AdapterCallbacks<T> {

    /**
     * The fragment argument representing the section number for this fragment.
     */
    protected static final String ARG_SECTION_NUMBER = "section_number";

    private static final String TAG = PeopleFragment.class.getSimpleName();

    private FragmentCallbacks mCallbacks;

    private PeopleRecyclerAdapter mAdapter;

    private List<T> mData;

    private SwipeRefreshLayout mRefreshLayout;

    private volatile boolean mNeedsRefresh;

    private boolean mDelaySyncState;

    public PeopleFragment() {
        // Required empty public constructor
    }

    /**
     * @return the type of person that will be used to create the person adapter
     */
    protected abstract PersonType getPersonType();

    /**
     * @return the key used for checking the sync status
     */
    protected abstract String getSyncStateKey();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        mDelaySyncState = false;
        initialize(view);
        populateView(view);
        if (requiresSync()) {
            onRefresh();
        } else {
            //Initiate data request
            retrieveData();
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mCallbacks == null) {
            initializeCallbacks(getActivity());
        }
        if (mDelaySyncState) {
            updateSyncState(false);
        }
        mCallbacks.setToolbarShadow(false);
    }

    private boolean requiresSync() {
        return SyncStateManager.requiresSync(getContext(), getSyncStateKey());
    }


    protected void initialize(View view) {
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);
        mNeedsRefresh = true;
        //Must be posted as a runnable otherwise the loading indicator will not show.
        //Known bug in android https://code.google.com/p/android/issues/detail?id=77712
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Its possible that setRefreshing false has already been called in which case we do not show loading indicator
                if (mNeedsRefresh) {
                    mRefreshLayout.setRefreshing(true);
                    Log.d(TAG, "Setting refresh to true");
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onDataLoaded(List<T> data, boolean isLoadedFromServer) {
        if (mAdapter != null) {
            if (isLoadedFromServer) {
                mData = data;
                //Replace current
                mAdapter.replaceData(mData);
                //Loaded from server
                updateSyncState(false);
            } else {
                //Just populate
                mData.addAll(data);
            }
            mAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "Setting refresh to false");
        hideLoading();
    }


    private void populateView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mAdapter = new PeopleRecyclerAdapter<>(mData, getPersonType(), this);

        recyclerView.setAdapter(mAdapter);
    }

    private void updateSyncState(boolean needsUpdate) {
        if (getActivity() != null) {
//            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
//            editor.putBoolean(getSyncStateKey(), needsUpdate);
//            editor.apply();
            SyncStateManager.updateSyncState(getContext(), getSyncStateKey(), needsUpdate);
        } else {
            //Delay the update if the activity is not ready yet
            mDelaySyncState = true;
        }
    }


    @Override
    public void onRefresh() {
        //Refresh content here
        retrieveFromServer(getServerQuery());
    }

    @Override
    public void onPause() {
        super.onPause();
        hideLoading();
    }

    private void hideLoading() {
        mNeedsRefresh = false;
        if (mRefreshLayout != null && mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
            mRefreshLayout.clearAnimation();
        }
    }

    @Override
    protected void onDataError() {
        if (isAdded()) {
            hideLoading();
            Toast.makeText(getActivity(), R.string.error_msg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Unable to retrieve people data");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initializeCallbacks(context);
        mCallbacks.updateTitle(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void initializeCallbacks(Context context) {
        if (context instanceof FragmentCallbacks) {
            mCallbacks = (FragmentCallbacks) context;
        } else {
            throw new ClassCastException("Hosting activity must implement fragment callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onItemClicked(T object) {
        onItemClickedPrimary(object);
    }

    /**
     * Invoked when a person row is clicked
     *
     * @param person the person object that was clicked
     */
    protected abstract void onItemClickedPrimary(T person);
}
