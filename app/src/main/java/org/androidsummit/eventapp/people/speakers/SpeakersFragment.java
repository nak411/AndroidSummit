package org.androidsummit.eventapp.people.speakers;

import android.content.Intent;
import android.os.Bundle;

import org.androidsummit.eventapp.model.enums.PersonType;
import org.androidsummit.eventapp.model.Speaker;
import org.androidsummit.eventapp.model.trackers.DataState;
import org.androidsummit.eventapp.people.PeopleFragment;

import com.parse.ParseQuery;

/**
 * Concrete class for displaying speaker list
 *
 * Created by Naveed on 4/21/15.
 */
public class SpeakersFragment extends PeopleFragment<Speaker> {


    public static SpeakersFragment newInstance(int sectionNumber) {
        SpeakersFragment fragment = new SpeakersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public ParseQuery<Speaker> getQuery() {
        ParseQuery<Speaker> query = ParseQuery.getQuery(Speaker.class);
        query.orderByAscending(Speaker.LAST_NAME);
        return query;
    }

    @Override
    public ParseQuery<Speaker> getServerQuery() {
        ParseQuery<Speaker> query = ParseQuery.getQuery(Speaker.class);
        query.orderByAscending(Speaker.LAST_NAME);
        return query;
    }

    @Override
    protected PersonType getPersonType() {
        return PersonType.SPEAKER;
    }

    @Override
    protected String getSyncStateKey() {
        return DataState.SYNC_SPEAKERS;
    }

    @Override
    protected void onItemClickedPrimary(Speaker speaker) {
        Intent intent = SpeakerDetailActivity.makeIntent(getContext(), speaker);
        startActivity(intent);
    }
}
