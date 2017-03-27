package org.androidsummit.eventapp.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.cache.SummitCache;
import org.androidsummit.eventapp.cache.managers.SyncStateManager;
import org.androidsummit.eventapp.utils.helpers.DateHelper;
import org.androidsummit.eventapp.interfaces.FragmentCallbacks;
import org.androidsummit.eventapp.ui.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    public static final String TAG = ScheduleFragment.class.getSimpleName();

    public enum Type {
        MAIN_SCHEDULE,
        MY_SCHEDULE
    }

    private FragmentCallbacks mCallbacks;

    private List<Date> mDates;

    /**
     * The fragment argument representing the section number for this fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance(int sectionNumber) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SummitCache.getScheduleDates() == null) {
            SummitCache.init();
        }
        mDates = SummitCache.getScheduleDates();
        if (mDates == null) {
            mDates = getAsList();
        }
        //TODO handle case dates are empty
        if (getActivity() != null) {
            SyncStateManager.setupDataSyncIfNeeded(getActivity(), mDates.size());
        }
    }

    private List<Date> getAsList() {
        List<Date> list = new ArrayList<>();
        list.add(DateHelper.getFormattedFullDateAndTime("08", "26", "2016"));
        list.add(DateHelper.getFormattedFullDateAndTime("08", "27", "2016"));
        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        return view;
    }

    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished. Here we can pick out the {@link
     * View}s we need to configure from the content view.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initialize(view, savedInstanceState != null);
    }

    private void initialize(View view, boolean isRestoring) {
        //Set up view pager
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        int position =  getArguments().getInt(ARG_SECTION_NUMBER);
        Type type = position == 2 ? Type.MY_SCHEDULE : Type.MAIN_SCHEDULE;
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(getActivity().getSupportFragmentManager(), mDates, type);
        viewPager.setAdapter(adapter);
        if (!isRestoring) {
            viewPager.setCurrentItem(getCurrentItemIndex());
        }
        //Set up sliding tab layout
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.session_tab, R.id.tv_title);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(getContext(), R.color.medium_white));
        mSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(getContext(), R.color.medium_white));
        mSlidingTabLayout.setViewPager(viewPager);
    }

    private int getCurrentItemIndex() {
        Date today = DateHelper.getZeroTimeDate();
        for (int i = 0; i < mDates.size(); i++) {
            Date date = mDates.get(i);
            if (date.compareTo(today) == 0) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initializeCallbacks(context);
        mCallbacks.updateTitle(getArguments().getInt(ARG_SECTION_NUMBER));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mCallbacks == null) {
            initializeCallbacks(getActivity());
        }
        mCallbacks.setToolbarShadow(false);
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
}
