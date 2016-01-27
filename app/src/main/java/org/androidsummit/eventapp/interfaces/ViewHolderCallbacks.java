package org.androidsummit.eventapp.interfaces;

import android.view.View;

/**
 * Used reporting back click events from a view within a viw holder.
 *
 * Created by Naveed on 3/20/15.
 */
public interface ViewHolderCallbacks {

    void onItemClick(View view, int position);
}
