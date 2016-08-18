package org.androidsummit.eventapp.schedule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.androidsummit.eventapp.utils.helpers.DateHelper;

import java.util.Date;
import java.util.List;

/**
 * A view pager adapter used for paging between days of the week.
 *
 * Created by Naveed on 3/12/15.
 */
public abstract class SchedulePagerAdapter extends FragmentStatePagerAdapter {

    private List<Date> mDates;

    public SchedulePagerAdapter(FragmentManager fm, List<Date> dates)  {
        super(fm);
        mDates = dates;
    }

    @Override
    public int getCount() {
        return mDates.size();
    }

    @Override
    public Fragment getItem(int position) {
        return getFragment(position, mDates.get(position));
    }

    /**
     * Return the fragment that should be displayed at the specified position
     * @param position the position of the current page
     * @param date the date for the current page
     * @return the fragment to display
     */
    protected abstract Fragment getFragment(int position, Date date);

    @Override
    public CharSequence getPageTitle(int position) {
        return DateHelper.getFormattedDate(mDates.get(position));
    }
}
