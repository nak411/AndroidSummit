package org.androidsummit.eventapp.schedule;

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
import org.androidsummit.eventapp.fragments.ParseDataListRetrievalFragment;
import org.androidsummit.eventapp.interfaces.FragmentCallbacks;
import org.androidsummit.eventapp.model.MySummitSession;
import org.androidsummit.eventapp.model.SummitSession;
import org.androidsummit.eventapp.model.wrappers.SessionRowItem;
import org.androidsummit.eventapp.utils.helpers.DateHelper;
import org.androidsummit.eventapp.cache.managers.SyncStateManager;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Displays the UI for a single day
 * <p/>
 * Created by Naveed on 3/12/15.
 */
public class SummitDayFragment extends ParseDataListRetrievalFragment<SummitSession>
        implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = SummitDayFragment.class.getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String ARG_DATE = "date";

    private FragmentCallbacks mCallbacks;

    private View mView;

    private List<SessionRowItem> mData;

    private SessionRecyclerAdapter mAdapter;

    private SwipeRefreshLayout mRefreshLayout;

    private volatile boolean mNeedsRefresh;


    public SummitDayFragment() {
        //Required empty constructor
    }

    public static SummitDayFragment newInstance(int sectionNumber, Date date) {
        SummitDayFragment fragment = new SummitDayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_summit_day, container, false);
        initialize(mView);
        populateViews(mView);
        if (SyncStateManager.scheduleRequiresSync(getActivity())) {
            onRefresh();
        } else {
            //Initiate data request
            retrieveData();
        }
        return mView;
    }

    private void initialize(View view) {
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);
        mNeedsRefresh = true;
        //Must post as a runnable otherwise the loading indicator will not show.
        //Known bug in android https://code.google.com/p/android/issues/detail?id=77712
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Its possible that setRefreshing false has already been called in which case we do not show loading indicator
                if (mNeedsRefresh) {
                    mRefreshLayout.setRefreshing(true);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initializeCallbacks(context);
        if (!mCallbacks.isMultiDay()) {
            mCallbacks.updateTitle(getArguments().getInt(ARG_SECTION_NUMBER));
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        hideLoading();
    }

    @Override
    protected void onDataLoaded(List<SummitSession> summitSessions, boolean isLoadedFromServer) {
        if (mAdapter != null) {
            if (isLoadedFromServer) {
                mData = EventHelper.generateRowItems(summitSessions);
                //Replace current
                mAdapter.replaceData(mData);
                //Update sync state
                SyncStateManager.setScheduleRequiresSync(getActivity(), false);
            } else {
                //Just populate
                mData.addAll(EventHelper.generateRowItems(summitSessions));
            }
            mAdapter.notifyDataSetChanged();
            if (SyncStateManager.shouldInitializeMySchedule(getActivity())) {
                List<MySummitSession> mySummitSessions = new ArrayList<>();
                for (SummitSession session : summitSessions) {
                    if (session.getTypeCode() == 1) {
                        mySummitSessions.add(new MySummitSession(session.getStartTime(), session));
                    }
                }
                ParseObject.pinAllInBackground(mySummitSessions, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            SyncStateManager.setInitializeMySchedule(getActivity(), false);
                        }
                    }
                });
            }
        }
        hideLoading();
    }

    @Override
    public ParseQuery<SummitSession> getQuery() {
        final long date = getArguments().getLong(ARG_DATE);
        return buildTodayQuery(date);
    }

    @Override
    public ParseQuery<SummitSession> getServerQuery() {
        final long date = getArguments().getLong(ARG_DATE);
        return buildTodayQuery(date);
    }

    private ParseQuery<SummitSession> buildTodayQuery(long date) {
        ParseQuery<SummitSession> query = ParseQuery.getQuery(SummitSession.class);
        Date startDate = new Date(date);
        Date endDate = DateHelper.addDays(startDate, 1);
        query.whereGreaterThanOrEqualTo(SummitSession.START_TIME, startDate);
        query.whereLessThan(SummitSession.START_TIME, endDate);
        query.orderByAscending(SummitSession.START_TIME);
        return query;
    }

    private void hideLoading() {
        mNeedsRefresh = false;
        if (mRefreshLayout != null && mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
            mRefreshLayout.clearAnimation();
        }
    }

    private void populateViews(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // (not sure if we'll have fixed size here)
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        if (mData == null) {
            mData = new ArrayList<>();
        }
        // specify an adapter (see also next example)
        mAdapter = new SessionRecyclerAdapter(mData);

        recyclerView.setAdapter(mAdapter);
    }

    private void initializeCallbacks(Context context) {
        if (context instanceof FragmentCallbacks) {
            mCallbacks = (FragmentCallbacks) context;
        } else {
            throw new ClassCastException("Hosting activity must implement fragment callbacks");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mCallbacks == null) {
            initializeCallbacks(getActivity());
        }
        //Only manage the shadow if its multi day
        if (!mCallbacks.isMultiDay()) {
            mCallbacks.setToolbarShadow(false);
        }
    }

    @Override
    public void onRefresh() {
        //Refresh content here
        retrieveFromServer(getServerQuery());
    }

    @Override
    protected void onDataError() {
        Log.e(TAG, "Unable to retrieve data");
        if (!isAdded()) {
            return;
        }
        Toast.makeText(getActivity(), R.string.error_msg, Toast.LENGTH_SHORT).show();
        hideLoading();
    }

    //Clean up
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Dereference all references to views
        mView = null;
        mAdapter = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
