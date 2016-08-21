package org.androidsummit.eventapp.model.wrappers;

import org.androidsummit.eventapp.model.enums.SessionRowViewType;
import org.androidsummit.eventapp.model.SummitSession;

import java.util.List;

/**
 * Wraps two types of data objects that are used by the session recycler view. Only one item will me present at any time.
 * The row is either a header of a {@link SummitSession}
 * <p/>
 * {@link SummitSession} and text for a header view.
 * <p/>
 * Created on 9/16/15.
 */
public class SessionRowItem {


    private SummitSession mSession;

    private String mHeaderText;

    private String mFormattedSpeakerNames;

    private SessionRowViewType mViewType;

    public SessionRowItem(SummitSession summitSession) {
        this(summitSession, false);
    }

    public SessionRowItem(SummitSession summitSession, boolean formatSpeakerNames) {
        mSession = summitSession;
        mViewType = SessionRowViewType.SESSION;

        if (formatSpeakerNames) {
            mFormattedSpeakerNames = formatSpeakerNames(mSession.getSpeakerNames());
        }
    }

    public SessionRowItem(String headerText) {
        mHeaderText = headerText;
        mViewType = SessionRowViewType.HEADER;
    }

    public SessionRowViewType getViewType() {
        return mViewType;
    }

    public String getFormattedSpeakerNames() {
        return mFormattedSpeakerNames;
    }

    public SummitSession getSession() {
        return mSession;
    }

    public String getHeaderText() {
        return mHeaderText;
    }

    /**
     * Formats the name of speakers using & separators.
     *
     * @param speakerNames the names of speakers to format
     * @return the formatted names
     */
    private String formatSpeakerNames(List<String> speakerNames) {
        StringBuilder sb = new StringBuilder();
        String delimiter = "";
        if (speakerNames != null) {
            for (String name : speakerNames) {
                sb.append(delimiter).append(name);
                delimiter = " & ";
            }
        }
        return sb.toString();
    }
}
