package org.androidsummit.eventapp.schedule;

import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.androidsummit.eventapp.fragments.DetailsFragment;
import org.androidsummit.eventapp.model.MySummitSession;
import org.androidsummit.eventapp.model.SummitSession;

/**
 * Abstract class for Session detail.
 *
 * Created by Naveed on 10/4/15.
 */
public abstract class SessionDetailDataFragment extends DetailsFragment<SummitSession>{

    protected static final int REQUEST_CODE_MY_SESSION_CHECK = 0x1;

    protected abstract void onMySessionLoaded(MySummitSession obj);

    protected abstract void onAddToMyScheduleComplete(MySummitSession mSavedObject, boolean success);

    protected abstract void onRemoveFromMyScheduleComplete(boolean success);

    private MySummitSession mSavedObject;

    @Override
    protected void initiateDataRequests() {
        retrieveData();
        requestMySessionCheck(getQueryObjectId());
    }

    protected void requestMySessionCheck(String sessionId) {
        ParseQuery<MySummitSession> query = ParseQuery.getQuery(MySummitSession.class);
        query.fromLocalDatastore();
        query.whereEqualTo(MySummitSession.SESSION_ID, sessionId);
        retrieveDataObjectForQuery(query, REQUEST_CODE_MY_SESSION_CHECK);
    }

    @Override
    protected void onDataLoaded(ParseObject obj, int requestCode) {
        switch (requestCode) {
            case REQUEST_CODE_MY_SESSION_CHECK:
                handleMySessionCheck(obj);
                break;
        }
    }

    protected void addToMySchedule(SummitSession summitSession) {
        //Create a mySession object
        mSavedObject = new MySummitSession(summitSession.getStartTime(), summitSession);
        saveToLocal(summitSession);
    }

    protected void removeFromMySchedule(SummitSession summitSession) {
        deleteFromLocal(summitSession);
    }

    @Override
    public void onDataSaved(boolean onServer) {
        onAddToMyScheduleComplete(mSavedObject, true);
    }

    @Override
    public void onDataDelete(boolean onServer) {
        onRemoveFromMyScheduleComplete(true);
    }

    @Override
    public void onDataSaveError() {
        onAddToMyScheduleComplete(mSavedObject, false);
    }

    @Override
    public void onDataDeleteError() {
        onRemoveFromMyScheduleComplete(false);
    }

    private void handleMySessionCheck(ParseObject obj) {
        onMySessionLoaded((MySummitSession) obj);
    }

    @Override
    public void onDataRetrievalError(int requestCode) {
        switch (requestCode) {
            case REQUEST_CODE_MY_SESSION_CHECK:
                Log.e(TAG,"Failed data check for my session");
                onMySessionLoaded(null);
                break;
        }
    }
}
