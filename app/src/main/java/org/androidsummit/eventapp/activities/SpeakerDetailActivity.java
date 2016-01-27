package org.androidsummit.eventapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.fragments.people.SpeakerDetailFragment;
import org.androidsummit.eventapp.helpers.EventHelper;
import org.androidsummit.eventapp.interfaces.LoadingCallbacks;
import org.androidsummit.eventapp.model.Speaker;
import org.androidsummit.eventapp.model.generic.Person;

import com.parse.ParseFile;

public class SpeakerDetailActivity extends CollapsingToolbarColorChangerActivity implements LoadingCallbacks {

    public static final String PERSON_ID = "person.id";

    public static final String PERSON_NAME = "person.name";

    public static final String PERSON_TRACK = "person.track";

    /**
     * Creates an intent with the appropriate fields used for this activity
     *
     * @param context the context to use for creating new intent object
     * @param speaker the speaker object to extract needed field from
     * @return the created intent
     */
    public static Intent makeIntent(Context context, Speaker speaker) {
        Intent intent = new Intent(context, SpeakerDetailActivity.class);
        intent.putExtra(PERSON_ID, speaker.getObjectId());
        intent.putExtra(PERSON_TRACK, speaker.getTrack());
        intent.putExtra(PERSON_NAME, String.format("%s %s", speaker.getFirstName(),
                speaker.getLastName()));
        return intent;
    }

    @Override
    protected Fragment getContentFragment() {
        return SpeakerDetailFragment.newInstance(getIntent().getStringExtra(PERSON_ID));
    }

    @Override
    protected CharSequence getToolbarTitle() {
        return getIntent().getStringExtra(PERSON_NAME);
    }

    @Override
    protected boolean isSubtitleEnabled() {
        return false;
    }

    @Override
    protected String getSubTitle() {
        return null;
    }

    @Override
    protected int getTypeCode() {
        return EventHelper.getTypeCodeForTrack(getIntent().getStringExtra(PERSON_TRACK));
    }

    @Override
    protected boolean enableFab() {
        return false;
    }

    @Override
    public void showLoading() {
        super.showProgress();
    }

    @Override
    public void hideLoading() {
        super.hideProgress();
    }

    @Override
    public void loadBackdropImage(ParseFile parseFile) {
        super.loadBackDropImage(parseFile);
    }
}
