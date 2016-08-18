package org.androidsummit.eventapp.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.schedule.EventHelper;

import com.parse.ParseFile;

/**
 * An abstract activity, contains a collapsing toolbar that changes color depending on provided type code.
 * <p/>
 * It provides a container view for a content fragment which must be provided by any extending activity.
 * <p/>
 * Created on 9/21/15.
 */
public abstract class CollapsingToolbarColorChangerActivity extends AppCompatActivity {

    private ProgressBar mProgress;

    private FloatingActionButton mFab;

    /**
     * Fragment that should be added to the container
     *
     * @return the fragment to be added to the container
     */
    protected abstract Fragment getContentFragment();

    /**
     * @return The title to set for the toolbar
     */
    protected abstract CharSequence getToolbarTitle();

    /**
     * @return true if you want a persistent subtitle bar that moves with the collapsible view but does not collapse. This view can support
     * multi line subtitles
     */
    protected abstract boolean isSubtitleEnabled();

    /**
     * Return the string that will be used as the subtitle. Note that subtitle must be enabled in {@link #isSubtitleEnabled()} otherwise
     * this method will yield no results.
     */
    protected abstract String getSubTitle();

    /**
     * The type code to use for setting the color of tool bar and nav bar
     *
     * @return the type code
     * @see class comment {@link EventHelper} for valid type codes
     */
    protected abstract int getTypeCode();

    /**
     * A boolean indicating whether the floating action button should be enable or not.
     *
     * @return true if the fab should be show, other wise false
     */
    protected abstract boolean enableFab();

    // protected abstract int getLayoutResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_change_toolbar);

        if (savedInstanceState == null) {
            initialize();
        }

        //Set up toolbar
        setupCollapsingToolbar();
    }

    /**
     * Override to use progress indicator if data require loading
     */
    protected void showProgress() {
        getProgressBar().setVisibility(View.VISIBLE);
    }

    /**
     * Override to use progress indicator if data requires loading
     */
    protected void hideProgress() {
        getProgressBar().setVisibility(View.GONE);
    }


    protected void showFab() {
        if (enableFab()) {
            mFab.show();
        } else {
            throw new IllegalStateException("You must call enableFab() before calling showFab()");
        }
    }

    protected void hideFab() {
        if (enableFab()) {
            mFab.hide();
        } else {
            throw new IllegalStateException("You must call enableFab() before calling hideFab()");
        }
    }
    /**
     * Sets the click listener on the fab. This method should only be called if the fab is enabled.
     *
     * @param listener the listener to set on the fab.
     */
    protected void setOnFabClickListener(View.OnClickListener listener) {
        if (enableFab()) {
            mFab.setOnClickListener(listener);
        } else {
            throw new IllegalArgumentException("You must call enableFable() before setting setOnFabClickListener");
        }
    }

    /**
     * Override this method to load an image view into collapsing toolbar backdrop.
     *
     * @param parseFile the image file to load
     */
    protected void loadBackDropImage(ParseFile parseFile) {
        CircularParseImageView imageView = (CircularParseImageView) findViewById(R.id.iv_circular_backdrop);
        imageView.setVisibility(View.VISIBLE);
        imageView.setParseFile(parseFile);
        imageView.loadInBackground();
    }

    /**
     * Provide the drawable that should be used with this fab
     *
     * @param drawableId the id of drawable to use
     */
    protected void setFabDrawable(int drawableId) {
        if  (enableFab()) {
            mFab.setImageResource(drawableId);
        } else {
            throw new IllegalArgumentException("You must call enableFable() before setting a drawable on it");
        }
    }

    /**
     * Same as {@link #setFabDrawable(int)} except it does it with rotation animation
     *
     * @param drawableId the drawable to use for fab icon
     * @param rotationDegrees the angle of rotation animation in degrees
     */
    protected void setFabDrawableWithRotationAnimation(int drawableId, int rotationDegrees) {
        setFabDrawable(drawableId);
        mFab.animate().rotationBy(rotationDegrees).start();
    }


    private ProgressBar getProgressBar() {
        if (mProgress == null) {
            mProgress = (ProgressBar) findViewById(R.id.indeterminate_horizontal_progress);
        }
        return mProgress;
    }

    private void setupCollapsingToolbar() {
        //Set up toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int colorId = EventHelper.getColorForTypeCode(getTypeCode());
        //Set up toolbar
        setupToolbar(colorId);

        //Set up app bar
        setupAppBar(colorId);

        //Handle subtitle
        handleSubtitle(colorId);

        //Handle window
        handleWindow(colorId);

        //Setup floating action button
        setupFab();
    }

    private void setupFab() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        if (enableFab()) {
            mFab.setVisibility(View.VISIBLE);
        } else {
            mFab.setVisibility(View.GONE);
            //No need to hold on to the reference if fab is not enabled for this activity
            mFab = null;
        }
    }

    private void handleWindow(int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, colorId));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, colorId));
        }
    }

    private void setupAppBar(int colorId) {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setBackgroundColor(ContextCompat.getColor(this, colorId));
    }

    private void setupToolbar(int colorId) {
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getToolbarTitle());

        //Handle color change
        collapsingToolbarLayout.setBackgroundColor(ContextCompat.getColor(this, colorId));
        collapsingToolbarLayout.setContentScrim(ContextCompat.getDrawable(this, colorId));
    }

    private void handleSubtitle(int colorId) {
        Toolbar subTitleToolbar = (Toolbar) findViewById(R.id.subtitle);
        if (isSubtitleEnabled()) {
            subTitleToolbar.setVisibility(View.VISIBLE);
            TextView tvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
            tvSubtitle.setText(getSubTitle());
            subTitleToolbar.setBackgroundColor(ContextCompat.getColor(this, colorId));

        } else {
            subTitleToolbar.setVisibility(View.GONE);
        }
    }

    private void initialize() {
        //Set content fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = getContentFragment();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }


}
