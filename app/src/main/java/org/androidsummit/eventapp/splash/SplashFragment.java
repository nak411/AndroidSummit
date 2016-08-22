package org.androidsummit.eventapp.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.MainActivity;
import org.androidsummit.eventapp.cache.SummitCache;
import org.androidsummit.eventapp.cache.managers.SyncStateManager;
import org.androidsummit.eventapp.fragments.ParseDataModificationFragment;
import org.androidsummit.eventapp.utils.BuildUtils;
import org.androidsummit.eventapp.utils.DebugUtils;

/**
 * Contains UI for splash screen.  Also handles upgrading logic.
 * <p/>
 * Created on 8/18/16.
 */
public class SplashFragment extends ParseDataModificationFragment {

    public static final String TAG = "SplashFragment";

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeMemCache();
        checkUpgradeStatus();
    }

    private void initializeMemCache() {
        //TODO load this data from disk/server
        SummitCache.init();
    }

    private void checkUpgradeStatus() {
        if (BuildUtils.requiresUpgrade(getContext())) {
            DebugUtils.log(TAG, "Requires upgrade - setting upgrade flags");
            clearAllLocalData();
        } else {
            startMainActivity();
        }
    }

    @Override
    public void onDataDelete(boolean onServer) {
        if (!onServer) {
            SyncStateManager.setAllSyncFlags(getContext(), true);
            markUpgradeCompleted();
        } else {
            Log.w(TAG, "Splash activity deleting data on server");
        }
    }

    private void markUpgradeCompleted() {
        BuildUtils.setUpgradeComplete(getContext());
        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = MainActivity.getStartIntent(getContext());
        startActivity(intent);
    }
}
