package org.androidsummit.eventapp.schedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import org.androidsummit.eventapp.schedule.SessionDetailFragment;
import org.androidsummit.eventapp.interfaces.FabCallbacks;
import org.androidsummit.eventapp.interfaces.LoadingCallbacks;
import org.androidsummit.eventapp.ui.CollapsingToolbarColorChangerActivity;

import com.parse.ParseFile;

/**
 * Defines the UI and logic for session details.
 */
public class SummitSessionDetailActivity extends CollapsingToolbarColorChangerActivity implements LoadingCallbacks, FabCallbacks {

    public static final String SESSION_ID_KEY = "session.data.key";

    public static final String SESSION_TITLE_KEY = "title.key";

    public static final String SESSION_SUB_TITLE_KEY = "subtitle.key";

    public static final String SESSION_TYPE_CODE = "session.type.code";

    public static final int FAB_ROTATION_DEGREE = 180;

    @Override
    protected Fragment getContentFragment() {
        return SessionDetailFragment.newInstance(getIntent().getStringExtra(SESSION_ID_KEY));
    }

    @Override
    protected CharSequence getToolbarTitle() {
        return getIntent().getStringExtra(SESSION_TITLE_KEY);
    }

    @Override
    protected boolean isSubtitleEnabled() {
        return true;
    }

    @Override
    protected String getSubTitle() {
        return getIntent().getStringExtra(SESSION_SUB_TITLE_KEY);
    }

    @Override
    protected int getTypeCode() {
        return getIntent().getIntExtra(SESSION_TYPE_CODE, -1);
    }

    @Override
    protected boolean enableFab() {
        return true;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setFabClickListener(View.OnClickListener onClickListener) {
        setOnFabClickListener(onClickListener);
    }

    @Override
    public void setFabImageResource(int drawableResourceId) {
        setFabDrawableWithRotationAnimation(drawableResourceId, FAB_ROTATION_DEGREE);
    }

    @Override
    public void showFAB() {
        showFab();
    }

    @Override
    public void hideFAB() {
        hideFab();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
