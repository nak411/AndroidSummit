package org.androidsummit.eventapp.interfaces;
import com.parse.ParseObject;

/**
 * Callbacks for communicating between the session adapter and fragment
 */
public interface AdapterCallbacks<T extends ParseObject> {
    /**
     * Invoked when my session is clicked in list view
     *
     * @param object the selected object
     */
    void onItemClicked(T object);
}
