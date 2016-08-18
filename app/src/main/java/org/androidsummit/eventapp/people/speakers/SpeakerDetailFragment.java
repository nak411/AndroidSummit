package org.androidsummit.eventapp.people.speakers;

import android.os.Bundle;
import android.widget.TextView;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.fragments.DetailsFragment;
import org.androidsummit.eventapp.schedule.EventHelper;
import org.androidsummit.eventapp.model.Speaker;
import com.parse.ParseQuery;

/**
 * Concrete fragment: defines detail data for a speaker.
 *
 * Created by Naveed on 4/23/15.
 */
public class SpeakerDetailFragment extends DetailsFragment<Speaker> {

    public static final String TAG = SpeakerDetailFragment.class.getSimpleName();


    public SpeakerDetailFragment() {
        // Required empty public constructor
    }

    public static SpeakerDetailFragment newInstance(String personId) {
        SpeakerDetailFragment fragment = new SpeakerDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DATA_KEY, personId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This will hold on to instance variable on rotation.  Views will always be destroyed and recreated so make sure
        //not to hold on to any strong references to any views.
        //setRetainInstance(true);
    }
    
    @Override
    protected String getQueryObjectId() {
        return getArguments().getString(DATA_KEY);
    }

    @Override
    public ParseQuery<Speaker> getQuery() {
        return ParseQuery.getQuery(Speaker.class);
    }

    @Override
    public ParseQuery<Speaker> getServerQuery() {
        return ParseQuery.getQuery(Speaker.class);
    }

    @Override
    protected void populateViews(Speaker speaker) {
        mCallbacks.loadBackdropImage(speaker.getProfilePic());
        TextView tvDescription = (TextView) mView.findViewById(R.id.tv_description);
        tvDescription.setText(EventHelper.formatDescription(speaker.getBio()));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_speaker_detail;
    }

    @Override
    protected void initiateDataRequests() {
        retrieveData();
    }

    @Override
    protected void setupPreDataLoadViews() {
        //TODO FILL OUT
    }
}
