package org.androidsummit.eventapp.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.activities.SummitSessionDetailActivity;
import org.androidsummit.eventapp.enums.SessionRowViewType;
import org.androidsummit.eventapp.interfaces.AdapterCallbacks;
import org.androidsummit.eventapp.interfaces.ViewHolderCallbacks;
import org.androidsummit.eventapp.model.SummitSession;
import org.androidsummit.eventapp.helpers.EventHelper;
import org.androidsummit.eventapp.helpers.person.DateHelper;
import org.androidsummit.eventapp.model.wrappers.SessionRowItem;

import java.util.List;

/**
 * Displays the contents of a single day
 */
public class SessionRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ViewHolderCallbacks {

    private List<SessionRowItem> mData;

    private AdapterCallbacks mAdapterCallbacks;

    public SessionRecyclerAdapter(List<SessionRowItem> data) {
        this(data, null);
    }

    public SessionRecyclerAdapter(List<SessionRowItem> data, AdapterCallbacks adapterCallbacks) {
        mData = data;
        mAdapterCallbacks = adapterCallbacks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v;
        RecyclerView.ViewHolder holder;

        if (viewType == SessionRowViewType.HEADER.getVal()) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_section_header, parent, false);
            holder = new HeaderViewHolder(v);

        } else if (viewType == SessionRowViewType.SESSION.getVal()) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.summit_session_layout,
                parent, false);
            holder = new ViewHolder(v, this);
        } else {
            throw new IllegalArgumentException("Unknown view type " + viewType);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SessionRowItem rowItem = mData.get(position);
        switch (rowItem.getViewType()) {
            case HEADER:
                handleHeader((HeaderViewHolder) holder, position);
                break;
            case SESSION:
                handleSession((ViewHolder) holder, position);
                break;
        }
    }

    private void handleHeader(HeaderViewHolder viewHolder, int position) {
        String headerText = mData.get(position).getHeaderText();
        viewHolder.mTvDate.setText(headerText);
    }

    private void handleSession(ViewHolder viewHolder, int position) {
        //Set data here
        SummitSession summitSession = mData.get(position).getSession();
        String speakers = mData.get(position).getFormattedSpeakerNames();
        String date = EventHelper.formatEventTime(summitSession);
        viewHolder.mTvTime.setText(date.toLowerCase());
        viewHolder.mTvTitle.setText(summitSession.getTitle());
        //Set speaker names
        if (!TextUtils.isEmpty(speakers)) {
            viewHolder.mTvSpeakers.setVisibility(View.VISIBLE);
            viewHolder.mTvSpeakers.setText(speakers);
        } else {
            viewHolder.mTvSpeakers.setVisibility(View.GONE);
        }
        viewHolder.mIvIcon.setImageResource(EventHelper.getIconIdForTypeCode(summitSession.getTypeCode()));
    }

    @Override
    public int getItemViewType(int position) {
        SessionRowViewType type = mData.get(position).getViewType();
        return type.getVal();
    }

    public void replaceData(List<SessionRowItem> newData) {
        mData = newData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public void onItemClick(View view, int position) {
        //Retrieve data and start activity.
        if (mAdapterCallbacks != null) {
            mAdapterCallbacks.onItemClicked(mData.get(position).getSession());
        } else {
            Intent intent = new Intent(view.getContext(), SummitSessionDetailActivity.class);
            SummitSession summitSession = mData.get(position).getSession();
            intent.putExtra(SummitSessionDetailActivity.SESSION_TITLE_KEY, DateHelper.getFormattedDate(summitSession.getStartTime()));
            intent.putExtra(SummitSessionDetailActivity.SESSION_SUB_TITLE_KEY, summitSession.getTitle());
            intent.putExtra(SummitSessionDetailActivity.SESSION_ID_KEY, summitSession.getObjectId());
            intent.putExtra(SummitSessionDetailActivity.SESSION_TYPE_CODE, summitSession.getTypeCode());
            view.getContext().startActivity(intent);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView mTvDate;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mTvDate = (TextView) itemView.findViewById(R.id.tv_row_header);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // each data item is just a string in this case
        public TextView mTvTitle;

        public TextView mTvTime;

        public TextView mTvSpeakers;

        public ImageView mIvIcon;

        private ViewHolderCallbacks mCallbacks;


        public ViewHolder(View rootView, ViewHolderCallbacks onItemClickCallback) {
            super(rootView);
            mTvTitle = (TextView) rootView.findViewById(R.id.tv_title);
            mTvSpeakers = (TextView) rootView.findViewById(R.id.tv_speakers);
            mTvTime = (TextView) rootView.findViewById(R.id.tv_time);
            mIvIcon = (ImageView) rootView.findViewById(R.id.iv_icon);
            mCallbacks = onItemClickCallback;
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Delegate clicks back to the adapter
            mCallbacks.onItemClick(v, getAdapterPosition());
        }
    }
}
