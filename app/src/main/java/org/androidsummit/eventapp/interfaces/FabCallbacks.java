package org.androidsummit.eventapp.interfaces;

import android.view.View;

/**
 * Callbacks for FAB
 */
public interface FabCallbacks {

    void setFabClickListener(View.OnClickListener onClickListener);

    void setFabImageResource(int drawableResourceId);

    void showFAB();

    void hideFAB();
}
