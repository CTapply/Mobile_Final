package com.example.jeffrey.finalprototype;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * A fragment representing a single Commute detail screen.
 * This fragment is either contained in a {@link CommuteListActivity}
 * in two-pane mode (on tablets) or a {@link CommuteDetailActivity}
 * on handsets.
 */
public class CommuteDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Content.Commute mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommuteDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = Content.COMMUTE_MAP.get(getArguments().getString(ARG_ITEM_ID));

//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null && mItem != null) {
//                appBarLayout.setTitle(mItem.id);
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.commute_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.textView2)).setText(mItem.id);
            ((TextView) rootView.findViewById(R.id.textView4)).setText(mItem.semanticTime());
            ((TextView) rootView.findViewById(R.id.textView6)).setText(mItem.semanticPrepTime());
            ((TextView) rootView.findViewById(R.id.textView8)).setText(mItem.destination);
            ((TextView) rootView.findViewById(R.id.textView10)).setText(mItem.weekInfo.toString());
            CheckBox alarmArmed = ((CheckBox) rootView.findViewById(R.id.AlarmCheckbox));
            alarmArmed.setChecked(mItem.active);


            Button editButton  = (Button) rootView.findViewById(R.id.editButton);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent editCommuteIntent = new Intent(getActivity(), AddNewCommute.class);
                    editCommuteIntent.putExtra("EDIT_MODE", true);
                    editCommuteIntent.putExtra("UUID", mItem.UUID);
                    editCommuteIntent.putExtra("id", mItem.id);
                    editCommuteIntent.putExtra("arr_hour", mItem.arrivalTimeHour);
                    editCommuteIntent.putExtra("arr_min", mItem.arrivalTimeMin);
                    editCommuteIntent.putExtra("prep_mins", mItem.preparationTime);
                    editCommuteIntent.putExtra("destination", mItem.destination);
                    editCommuteIntent.putExtra("sunday", mItem.weekInfo.days[0]);
                    editCommuteIntent.putExtra("monday", mItem.weekInfo.days[1]);
                    editCommuteIntent.putExtra("tuesday", mItem.weekInfo.days[2]);
                    editCommuteIntent.putExtra("wednesday", mItem.weekInfo.days[3]);
                    editCommuteIntent.putExtra("thursday", mItem.weekInfo.days[4]);
                    editCommuteIntent.putExtra("friday", mItem.weekInfo.days[5]);
                    editCommuteIntent.putExtra("saturday", mItem.weekInfo.days[6]);
                    editCommuteIntent.putExtra("repeat", mItem.weekInfo.repeat);
                    startActivity(editCommuteIntent);
                }
            });
        }

        return rootView;
    }
}
