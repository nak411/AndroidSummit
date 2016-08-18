package org.androidsummit.eventapp.schedule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.Date;
import java.util.List;

/**
 * Concrete class for main schedule adapter
 * <p/>
 * Created on 8/18/16.
 */
public class MainScheduleAdapter extends SchedulePagerAdapter {

    public MainScheduleAdapter(FragmentManager fm, List<Date> dates) {
        super(fm, dates);
    }

    @Override
    protected Fragment getFragment(int position, Date date) {
        return SummitDayFragment.newInstance(position, date);
    }
}
