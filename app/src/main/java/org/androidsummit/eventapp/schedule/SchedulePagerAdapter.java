package org.androidsummit.eventapp.schedule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.androidsummit.eventapp.utils.helpers.DateHelper;

import java.util.Date;
import java.util.List;

/**
 * A view pager adapter used for paging between days of the week.
 *
 * Created by Naveed on 3/12/15.
 */
public class SchedulePagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "SchedulePagerAdapter";
    private List<Date> mDates;

    private ScheduleFragment.Type mType;

    public SchedulePagerAdapter(FragmentManager fm, List<Date> dates, ScheduleFragment.Type type)  {
        super(fm);
        mDates = dates;
        mType = type;
    }

    @Override
    public int getCount() {
        return mDates.size();
    }

    @Override
    public Fragment getItem(int position) {
        switch (mType) {
            case MY_SCHEDULE:
                return MySummitDayFragment.newInstance(position, mDates.get(position).getTime());
            case MAIN_SCHEDULE:
                return SummitDayFragment.newInstance(position, mDates.get(position));
        }
        Log.w(TAG, "Type did not match for schedule " + mType);
        return SummitDayFragment.newInstance(position, mDates.get(position));
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return DateHelper.getFormattedDate(mDates.get(position));
    }
}
