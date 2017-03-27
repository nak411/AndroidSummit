package org.androidsummit.eventapp.people.speakers;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.fragments.DetailsFragment;
import org.androidsummit.eventapp.people.PersonDataHelper;
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
        TextView tvRole = (TextView) mView.findViewById(R.id.tv_role);
        TextView tvDescription = (TextView) mView.findViewById(R.id.tv_description);
        tvDescription.setText(EventHelper.formatDescription(speaker.getBio()));

        String subtitle = PersonDataHelper.getSubtitle(speaker);
        if (!TextUtils.isEmpty(subtitle)) {
            tvRole.setText(subtitle);
            tvRole.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setVisibility(View.GONE);
        }
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
