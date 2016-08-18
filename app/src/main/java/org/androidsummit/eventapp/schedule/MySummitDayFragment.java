package org.androidsummit.eventapp.schedule;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.fragments.ParseDataListRetrievalFragment;
import org.androidsummit.eventapp.utils.helpers.DateHelper;
import org.androidsummit.eventapp.interfaces.AdapterCallbacks;
import org.androidsummit.eventapp.interfaces.FragmentCallbacks;
import org.androidsummit.eventapp.model.MySummitSession;
import org.androidsummit.eventapp.model.SummitSession;
import org.androidsummit.eventapp.model.wrappers.SessionRowItem;

import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MySummitDayFragment extends ParseDataListRetrievalFragment<MySummitSession>
    implements SwipeRefreshLayout.OnRefreshListener, AdapterCallbacks<SummitSession> {

    public static final String TAG = MySummitDayFragment.class.getSimpleName();

    public static final int SESSION_DETAILS_REQUEST_CODE = 1000;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String ARG_DATE = "date";

    private FragmentCallbacks mCallbacks;

    private View mView;

    private List<SessionRowItem> mData;

    private SessionRecyclerAdapter mAdapter;

    private SwipeRefreshLayout mRefreshLayout;

    private volatile boolean mNeedsRefresh;

    public MySummitDayFragment() {
        //Required empty constructor
    }

    public static MySummitDayFragment newInstance(int sectionNumber, long date) {
        MySummitDayFragment fragment = new MySummitDayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putLong(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_my_summit_day, container, false);
        initialize(mView);
        populateViews(mView);
        retrieveData();
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
        mCallbacks.updateTitle(getArguments().getInt(ARG_SECTION_NUMBER));

    }

    @Override
    public void onPause() {
        super.onPause();
        hideLoading();
    }

    @Override
    protected void onDataLoaded(List<MySummitSession> mySummitSessions, boolean isLoadedFromServer) {
        if (mAdapter != null) {
            if (mData == null) {
                mData = EventHelper.generateMySessionRowItems(mySummitSessions);
            } else {
                //Just populate
                mData.clear();
                mData.addAll(EventHelper.generateMySessionRowItems(mySummitSessions));
            }
            //Replace current
            mAdapter.replaceData(mData);
            mAdapter.notifyDataSetChanged();
        }
        hideLoading();
    }

    @Override
    public ParseQuery<MySummitSession> getQuery() {
        return buildTodayQuery();
    }

    @Override
    public ParseQuery<MySummitSession> getServerQuery() {
        return buildTodayQuery();
    }

    private ParseQuery<MySummitSession> buildTodayQuery() {
        ParseQuery<MySummitSession> parseQuery = ParseQuery.getQuery(MySummitSession.class);
        parseQuery.orderByAscending(MySummitSession.START_TIME);
        return parseQuery;
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
        mAdapter = new SessionRecyclerAdapter(mData, this);

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
        if (!mCallbacks.isMultiDay()) {
            mCallbacks.setToolbarShadow(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SESSION_DETAILS_REQUEST_CODE) {
            onRefresh();
        }
    }

    @Override
    public void onItemClicked(SummitSession summitSession) {
        Intent intent = new Intent(getContext(), SummitSessionDetailActivity.class);
        intent.putExtra(SummitSessionDetailActivity.SESSION_TITLE_KEY, DateHelper.getFormattedDate(summitSession.getStartTime()));
        intent.putExtra(SummitSessionDetailActivity.SESSION_SUB_TITLE_KEY, summitSession.getTitle());
        intent.putExtra(SummitSessionDetailActivity.SESSION_ID_KEY, summitSession.getObjectId());
        intent.putExtra(SummitSessionDetailActivity.SESSION_TYPE_CODE, summitSession.getTypeCode());
        //start from fragment to result in fragment
        startActivityForResult(intent, MySummitDayFragment.SESSION_DETAILS_REQUEST_CODE);
    }

    @Override
    public void onRefresh() {
        //Refresh content here
        retrieveData(getQuery());
    }

    @Override
    protected void onDataError() {
        if (!isAdded()) {
            return;
        }
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