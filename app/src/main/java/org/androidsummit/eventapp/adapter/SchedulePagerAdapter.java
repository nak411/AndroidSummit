package org.androidsummit.eventapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.androidsummit.eventapp.helpers.person.DateHelper;

import java.util.Date;

/**
 * A view pager adapter used for paging between days of the week.
 *
 * Created by Naveed on 3/12/15.
 */
public class SchedulePagerAdapter extends FragmentStatePagerAdapter {

    private Date[] mTitles;

    public SchedulePagerAdapter(FragmentManager fm, Date[] titles)  {
        super(fm);
        mTitles = titles;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public Fragment getItem(int psition) {
        //return SummitDayFragment.newInstance(mTitles[position], position);
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return DateHelper.getFormattedDate(mTitles[position]);
    }
}
