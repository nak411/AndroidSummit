package org.androidsummit.eventapp.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.interfaces.FragmentCallbacks;


/**
 * Simple fragment that displays static data
 *
 * Created by Naveed on 4/24/15.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {

    private FragmentCallbacks mCallbacks;

    public static final String MAP_URI = "google.navigation:q=1680 Capital One Drive McLean, VA 22102";

    /**
     * The fragment argument representing the section number for this fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance(int sectionNumber) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        initialize(view);;
        return view;
    }

    private void initialize(View view) {
        view.findViewById(R.id.btn_code_of_conduct).setOnClickListener(this);
        view.findViewById(R.id.btn_email).setOnClickListener(this);
        view.findViewById(R.id.btn_get_directions).setOnClickListener(this);
        view.findViewById(R.id.btn_twitter).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_code_of_conduct:
                launchBrowser(getString(R.string.code_of_conduct_uri));
                break;
            case R.id.btn_email:
                launchEmailClient(getString(R.string.email_address));
                break;
            case R.id.btn_get_directions:
                launchNavigation();
                break;
            case R.id.btn_twitter:
                launchTwitter("androidsummit");
                break;
        }
    }

    private void launchTwitter(String twitterId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=3646012763"));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Launch the official twitter app
            startActivity(intent);
        } else {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + twitterId));
            launchValidActivity(intent);
        }
    }

    private void launchNavigation() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MAP_URI));
        launchValidActivity(intent);
    }


    private void launchEmailClient(String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        launchValidActivity(intent);
    }

    private void launchBrowser(String uri) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        launchValidActivity(browserIntent);
    }

    private void launchValidActivity(Intent intent) {
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), getString(R.string.no_valid_activity), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentCallbacks) {
            mCallbacks = (FragmentCallbacks) activity;
            mCallbacks.updateTitle(getArguments().getInt(ARG_SECTION_NUMBER));

        } else {
            throw new ClassCastException("Hosting activity must implement fragment callbacks");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            mCallbacks.setToolbarShadow(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
