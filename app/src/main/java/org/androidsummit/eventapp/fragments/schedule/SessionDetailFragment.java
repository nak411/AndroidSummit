package org.androidsummit.eventapp.fragments.schedule;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.activities.SummitSessionDetailActivity;
import org.androidsummit.eventapp.helpers.person.DateHelper;
import org.androidsummit.eventapp.interfaces.FabCallbacks;
import org.androidsummit.eventapp.model.MySummitSession;
import org.androidsummit.eventapp.model.Speaker;
import org.androidsummit.eventapp.model.SummitSession;
import org.androidsummit.eventapp.helpers.EventHelper;
import org.androidsummit.eventapp.notifications.NotificationPublisher;
import org.androidsummit.eventapp.ui.CircularParseImageView;
import org.androidsummit.eventapp.utils.BuildUtils;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

/**
 * Contains UI for session details.
 */
public class SessionDetailFragment extends SessionDetailDataFragment {


    private static final String TAG = SessionDetailFragment.class.getSimpleName();

    private MySummitSession mySummitSession;

    private FabCallbacks mFabCallbacks;

    public SessionDetailFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String session) {
        Fragment fragment = new SessionDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DATA_KEY, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null && context instanceof FabCallbacks) {
            mFabCallbacks = (FabCallbacks) context;
        } else {
            throw new IllegalArgumentException("Hosting activity must implement fab callbacks");
        }
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
    public ParseQuery<SummitSession> getQuery() {
        //Create the query
        final ParseQuery<SummitSession> query = ParseQuery.getQuery(SummitSession.class.getSimpleName());
        query.include(SummitSession.SPEAKERS);
        return query;
    }

    @Override
    public ParseQuery<SummitSession> getServerQuery() {
        final ParseQuery<SummitSession> query = ParseQuery.getQuery(SummitSession.class.getSimpleName());
        query.include(SummitSession.SPEAKERS);
        return query;
    }

    /**
     * Populate data with supplied session
     *
     * @param summitSession the session object to use in order to populate the view
     */
    @Override
    protected void populateViews(final SummitSession summitSession) {
        if (mView != null) {

            //Retrieve all fields that need to be filled out
            TextView tvTime = (TextView) mView.findViewById(R.id.tv_time);
            TextView tvLocation = (TextView) mView.findViewById(R.id.tv_location);
            TextView tvDescription = (TextView) mView.findViewById(R.id.tv_description);
            TextView tvTrack = (TextView) mView.findViewById(R.id.tv_track);
            //Set data
            //time
            tvTime.setText(EventHelper.formatEventTime(summitSession));
            //Location
            tvLocation.setText(summitSession.getLocation());
            //Description
            tvDescription.setText(EventHelper.formatDescription(summitSession.getDescription()));
            //Track
            tvTrack.setText(EventHelper.getTrackFromTypeCode(summitSession.getTypeCode()));

            //Append presenters
            appendSpeakers(summitSession.getSpeakers());

            mFabCallbacks.showFAB();
            //checkIfThisIsMySession(summitSession);

            mFabCallbacks.setFabClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mySummitSession == null) {
                        addSessionToMySchedule(summitSession);
                    } else {
                        removeSessionFromMySchedule(summitSession);
                    }
                }
            });
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_session_detail;
    }

    @Override
    protected void setupPreDataLoadViews() {
        mFabCallbacks.hideFAB();
    }

    private void appendSpeakers(List<Speaker> speakers) {
        if (speakers.size() > 0) {
            RelativeLayout container = (RelativeLayout) mView.findViewById(R.id.detail_container);
            View divider = mView.findViewById(R.id.divider);
            divider.setVisibility(View.VISIBLE);
            LayoutInflater inflater = getActivity().getLayoutInflater();

            int viewAboveId = divider.getId();

            for (Speaker speaker : speakers) {

                View speakerView = inflater.inflate(R.layout.session_speaker, null, false);
                CircularParseImageView ivPic = (CircularParseImageView) speakerView.findViewById(R.id.iv_pic);
                TextView tvName = (TextView) speakerView.findViewById(R.id.tv_name);

                RelativeLayout.LayoutParams
                        params =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, viewAboveId);

                speakerView.setId(viewAboveId + 1);
                container.addView(speakerView, params);

                tvName.setText(speaker.getFirstName() + " " + speaker.getLastName());
                ivPic.setPlaceholder(getResources().getDrawable(R.drawable.profile_placeholder));
                ivPic.setParseFile(speaker.getProfilePic());
                ivPic.loadInBackground();
                viewAboveId++;
            }
        }
    }

//    private void checkIfThisIsMySession(final SummitSession summitSession) {
//        ParseQuery<MySummitSession> query = ParseQuery.getQuery(MySummitSession.class);
//        query.fromLocalDatastore();
//        query.whereEqualTo(MySummitSession.SESSION_ID, summitSession.getObjectId());
//        query.getFirstInBackground(new GetCallback<MySummitSession>() {
//            @Override
//            public void done(MySummitSession object, ParseException e) {
//                mySummitSession = object;
//                if (mySummitSession == null) {
//                    mFabCallbacks.setFabImageResource(R.drawable.ic_add);
//                } else {
//                    mFabCallbacks.setFabImageResource(R.drawable.ic_remove);
//                }
//            }
//        });
//    }

    @Override
    protected void onMySessionLoaded(MySummitSession object) {
        mySummitSession = object;
        if (mySummitSession == null) {
            mFabCallbacks.setFabImageResource(R.drawable.ic_add);
        } else {
            mFabCallbacks.setFabImageResource(R.drawable.ic_remove);
        }
    }

    @Override
    protected void onAddToMyScheduleComplete(MySummitSession mSavedSession, boolean success) {
        if (success) {
            mySummitSession = mSavedSession;
            mFabCallbacks.setFabImageResource(R.drawable.ic_remove);
            showSnackBar(R.string.my_session_added);
            setupNotifications(mySummitSession.getSummitSession());
        }
    }

    @Override
    protected void onRemoveFromMyScheduleComplete(boolean success) {
        if (success) {
            mySummitSession = null;
            mFabCallbacks.setFabImageResource(R.drawable.ic_add);
            showSnackBar(R.string.my_session_removed);
            removeNotifications(mySummitSession.getSummitSession());
        }
    }

    private boolean isSessionFinished(Date startTime) {
        return DateHelper.getTimeInMillis(startTime) <= DateHelper.getCurrentTimeInMillis();
    }

    private void addSessionToMySchedule(final SummitSession summitSession) {
        if (isSessionFinished(summitSession.getStartTime())) {
            showSnackBar(R.string.my_session_ended);
        } else {
//            final MySummitSession thisSession = new MySummitSession(summitSession.getStartTime(), summitSession);
//            thisSession.pinInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e == null) {
//                        mySummitSession = thisSession;
//                        mFabCallbacks.setFabImageResource(R.drawable.ic_remove);
//                        showSnackBar(R.string.my_session_added);
//                        setupNotifications(summitSession);
//                    }
//                }
//            });
            addToMySchedule(summitSession);
        }
    }

    private void removeSessionFromMySchedule(SummitSession summitSession) {
//        mySummitSession.unpinInBackground(new DeleteCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    mySummitSession = null;
//                    mFabCallbacks.setFabImageResource(R.drawable.ic_add);
//                    showSnackBar(R.string.my_session_removed);
//                    removeNotifications(summitSession);
//                }
//            }
//        });
        removeFromMySchedule(summitSession);
    }

    private void showSnackBar(int stringResId) {
        if (getView() == null) {
            return;
        }
        Snackbar.make(getView(), getString(stringResId), Snackbar.LENGTH_LONG).show();
    }

    //TODO THIS DOES NOT BELONG IN THIS FRAGMENT MOVE TO NOTIFICATION HELPER CLASS
    @TargetApi(16)
    private void setupNotifications(SummitSession summitSession) {
        Intent notificationIntent = new Intent(getActivity(), NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, summitSession.getObjectId().hashCode());
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, getNotification(summitSession));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), summitSession.getObjectId().hashCode(),
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // five minutes before session starts;
        long futureInMillis = DateHelper.getTimeInMillis(summitSession.getStartTime()) - (1000 * 60 * 5);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }

    @TargetApi(16)
    private void removeNotifications(SummitSession summitSession) {
        Intent notificationIntent = new Intent(getActivity(), NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, summitSession.getObjectId().hashCode());
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, getNotification(summitSession));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), summitSession.getObjectId().hashCode(), notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @TargetApi(16)
    @SuppressWarnings("NewApi")
    private Notification getNotification(SummitSession summitSession) {
        Notification.Builder builder = new Notification.Builder(getActivity());
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(summitSession.getTitle());
        builder.setSmallIcon(R.drawable.summit_icon);
        builder.setWhen(DateHelper.getTimeInMillis(summitSession.getStartTime()));
        if (BuildUtils.hasAPI17()) {
            builder.setShowWhen(true);
        }
        builder.setContentIntent(getContentIntent(summitSession));
        builder.setAutoCancel(true);
        return builder.build();
    }

    private PendingIntent getContentIntent(SummitSession summitSession) {
        Intent intent = new Intent(getActivity(), SummitSessionDetailActivity.class);
        intent.putExtra(SummitSessionDetailActivity.SESSION_TITLE_KEY, DateHelper.getFormattedDate(summitSession.getStartTime()));
        intent.putExtra(SummitSessionDetailActivity.SESSION_SUB_TITLE_KEY, summitSession.getTitle());
        intent.putExtra(SummitSessionDetailActivity.SESSION_ID_KEY, summitSession.getObjectId());
        intent.putExtra(SummitSessionDetailActivity.SESSION_TYPE_CODE, summitSession.getTypeCode());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        stackBuilder.addParentStack(SummitSessionDetailActivity.class);
        stackBuilder.addNextIntent(intent);

        return stackBuilder.getPendingIntent(summitSession.getObjectId().hashCode(), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
