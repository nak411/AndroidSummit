package org.androidsummit.eventapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidsummit.eventapp.R;
import org.androidsummit.eventapp.activities.SpeakerDetailActivity;
import org.androidsummit.eventapp.enums.PersonType;
import org.androidsummit.eventapp.helpers.PersonDataHelper;
import org.androidsummit.eventapp.interfaces.AdapterCallbacks;
import org.androidsummit.eventapp.interfaces.ViewHolderCallbacks;
import org.androidsummit.eventapp.model.Speaker;
import org.androidsummit.eventapp.model.generic.Person;
import org.androidsummit.eventapp.ui.CircularParseImageView;

import java.util.List;

/**
 * An adapter for handling a generic person type
 *
 * Created by Naveed on 3/24/15.
 */
public class PeopleRecyclerAdapter<T extends Person> extends
        RecyclerView.Adapter<PeopleRecyclerAdapter.ViewHolder> implements ViewHolderCallbacks {

    private List<T> mPeople;

    private AdapterCallbacks<T> mCallbacks;

    private PersonType mPersonType;

    public PeopleRecyclerAdapter(List<T> people, PersonType personType, AdapterCallbacks<T> callbacks) {
        mPeople = people;
        mPersonType = personType;
        mCallbacks = callbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_row_layout, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(PeopleRecyclerAdapter.ViewHolder viewHolder, int index) {
        Person person = mPeople.get(index);
        viewHolder.mTvName.setText(String.format("%s %s", person.getFirstName(), person.getLastName()));
        viewHolder.mTvSubtitle.setText(PersonDataHelper.getSubtitle(person));
        viewHolder.mIvPic.setPlaceholder(ContextCompat.getDrawable(viewHolder.mIvPic.getContext(), R.drawable.profile_placeholder));
        viewHolder.mIvPic.setParseFile(person.getProfilePic());
        viewHolder.mIvPic.loadInBackground();
    }

    public void addPeople(List<T> people) {
        mPeople.addAll(people);
    }

    @Override
    public int getItemCount() {
        // One for header image and oen for title
        return mPeople.size();
    }



    @Override
    public void onItemClick(View view, int position) {
//        Intent intent = new Intent(view.getContext(), SpeakerDetailActivity.class);
//        Person person = mPeople.get(position);
//        intent.putExtra(SpeakerDetailActivity.PERSON_ID, mPeople.get(position).getObjectId());
//        //TODO make this adapter more concrete or create a speakers adapter to avoid this cast since we have only one type of person.
//        intent.putExtra(SpeakerDetailActivity.PERSON_TRACK, ((Speaker) person).getTrack());
//        intent.putExtra(SpeakerDetailActivity.PERSON_NAME, person.getFirstName() + " " + person.getLastName());
//        startPersonDetailActivity(view.getContext(), intent);
        mCallbacks.onItemClicked(mPeople.get(position));
    }

    /**
     * Subclasses can override this method to add additional fields to the intent object
     *
     * @param context the context used to start activity
     * @param intent  the intent object populated with person info
     */
    protected void startPersonDetailActivity(Context context, Intent intent) {
        context.startActivity(intent);
    }

    public void replaceData(List<T> newData) {
        mPeople = newData;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolderCallbacks mCallbacks;

        public TextView mTvName;

        public TextView mTvSubtitle;

        public CircularParseImageView mIvPic;

        public ViewHolder(View itemView, ViewHolderCallbacks onItemClickCallbacks) {
            super(itemView);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvSubtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            mIvPic = (CircularParseImageView) itemView.findViewById(R.id.iv_pic);
            mCallbacks = onItemClickCallbacks;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onItemClick(v, getAdapterPosition());
        }
    }
}
