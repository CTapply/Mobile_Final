package com.example.jeffrey.finalprototype;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.jeffrey.finalprototype.Content.Commute;
import com.example.jeffrey.finalprototype.Content.WeeklyInfo;

import java.util.LinkedList;
import java.util.List;

import alarmManager.Alarm;
import alarmManager.AlarmService;
import alarmManager.AlarmServiceReceiver;
import database.CommuteBaseHelper;
import database.CommuteDbSchema;
import database.CommuteDbSchema.CommuteTable;

import static com.example.jeffrey.finalprototype.Content.addItem;

/**
 * An activity representing a list of Commutes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CommuteDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CommuteListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static final int NEW_COMMUTE_REQUEST = 50;
    public static SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commute_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        final Intent addCommuteIntent = new Intent(this, AddNewCommute.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(addCommuteIntent, NEW_COMMUTE_REQUEST);

            }
        });

        Context mContext = getApplicationContext();
        mDatabase = new CommuteBaseHelper(mContext).getWritableDatabase();
        Intent passedIntent = getIntent();
        if(passedIntent.getBooleanExtra("EDIT_MODE", false)){
            boolean[] days = new boolean[7];
            days[0] = passedIntent.getBooleanExtra("sunday", false);
            days[1] = passedIntent.getBooleanExtra("monday", false);
            days[2] = passedIntent.getBooleanExtra("tuesday", false);
            days[3] = passedIntent.getBooleanExtra("wednesday", false);
            days[4] = passedIntent.getBooleanExtra("thursday", false);
            days[5] = passedIntent.getBooleanExtra("friday", false);
            days[6] = passedIntent.getBooleanExtra("saturday", false);
            boolean repeat = passedIntent.getBooleanExtra("repeat", false);
            WeeklyInfo week = new WeeklyInfo(days, repeat);
            Commute toUpdate = new Commute(
                    passedIntent.getStringExtra("id"),
                    passedIntent.getStringExtra("destination"),
                    passedIntent.getIntExtra("arr_hour", 12),
                    passedIntent.getIntExtra("arr_min", 0),
                    passedIntent.getIntExtra("prep_mins", 0),
                    week,
                    passedIntent.getIntExtra("UUID", -1),
                    passedIntent.getBooleanExtra("true", true),
                    passedIntent.getStringExtra("alarmTone"),
                    passedIntent.getStringExtra("alarmTonePath"),
                    mContext
            );
            toUpdate.updateCommute();
        }

        Content.populate(this);
        View recyclerView = findViewById(R.id.commute_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.commute_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        callAlarmScheduleService();


    }

    protected void callAlarmScheduleService() {
        Intent AlarmServiceIntent = new Intent(this, AlarmServiceReceiver.class);
        sendBroadcast(AlarmServiceIntent, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NEW_COMMUTE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                System.out.println("Inside of RESULT_OK for new commute request");
                String name = data.getStringExtra("id");
                int arrHour = data.getIntExtra("arr_hour", 12);
                int arrMin = data.getIntExtra("arr_min", 0);
                int prepMins = data.getIntExtra("prep_mins", 0);
                String destination = data.getStringExtra("destination");
                String tone = data.getStringExtra("alarmTone");
                String tonePath = data.getStringExtra("alarmTonePath");

                boolean sunday = data.getBooleanExtra("sunday", false);
                boolean monday = data.getBooleanExtra("monday", false);
                boolean tuesday = data.getBooleanExtra("tuesday", false);
                boolean wednesday = data.getBooleanExtra("wednesday", false);
                boolean thursday = data.getBooleanExtra("thursday", false);
                boolean friday = data.getBooleanExtra("friday", false);
                boolean saturday = data.getBooleanExtra("saturday", false);
                boolean repeat = data.getBooleanExtra("repeat", false);

                WeeklyInfo w = makeWeek(sunday, monday, tuesday, wednesday, thursday, friday, saturday, repeat);

                Commute newCommute = new Commute(name, destination, arrHour, arrMin, prepMins, w, true, tone, tonePath, this);
                Context mContext = getApplicationContext();
                SQLiteDatabase mDatabase = new CommuteBaseHelper(mContext).getWritableDatabase();
                addItem(newCommute, mDatabase);

                Content.populate(this);
                View recyclerView = findViewById(R.id.commute_list);
                assert recyclerView != null;
                setupRecyclerView((RecyclerView) recyclerView);
                callAlarmScheduleService();
                if (newCommute.getNextAlarm() != null) {
                    Toast.makeText(CommuteListActivity.this, newCommute.getNextAlarm().getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                System.out.println("Inside of RESULT_NOT_OK for new commute request");

            }
        }
    }

    public static WeeklyInfo makeWeek(boolean su, boolean m, boolean tu, boolean w, boolean th,
                               boolean f, boolean sa, boolean r){
        boolean[] week = new boolean[7];
        week[0] = su;
        week[1] = m;
        week[2] = tu;
        week[3] = w;
        week[4] = th;
        week[5] = f;
        week[6] = sa;
        return new WeeklyInfo(week, r);
    }

    @Override
    protected void onPause(){
        super.onPause();

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Content.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        private int i = 0;

        private final List<Commute> mValues;

        public SimpleItemRecyclerViewAdapter(List<Commute> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.commute_list_content, parent, false);
            if (i%2==0) {
                view.findViewById(R.id.layoutBackground).setBackgroundColor(Color.parseColor("#E1F5FE")); // Light
            } else {
                view.findViewById(R.id.layoutBackground).setBackgroundColor(Color.parseColor("#B3E5FC")); // Dark
            }
            i++; // For swapping of colors in the list
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mCommute = mValues.get(position);

            // Set as inactive if there is no alarm that exists in the future and is not repeating
            if (holder.mCommute.getNextAlarm() == null) {
                holder.mCommute.disarmAlarms();
            }

            // Sets the name of the Commute in the list
            holder.mContentView.setText(holder.mCommute.id);
            holder.mTimeView.setText(holder.mCommute.semanticTime());

            //If the commute is active, we then set the colors and Switch as active
            if (holder.mCommute.active) {
                holder.mAlarmSwitch.setChecked(true);

                // Set the color of the Selected days of the alarm
//                if (holder.mCommute.weekInfo.repeat == true) {
//                    holder.mAlarmRepeat.setColorFilter(Color.parseColor("#ff4081")); // Pink
//                } else {
//                    holder.mAlarmRepeat.setColorFilter(Color.parseColor("#757575")); // Dark gray
//                }
                for (int i = 0; i < holder.mCommute.weekInfo.days.length; i++) {
                    if (holder.mCommute.weekInfo.days[i]) {
                        holder.mDayList.get(i).setTextColor(Color.parseColor("#ff4081"));// Pink
                    } else {
                        holder.mDayList.get(i).setTextColor(Color.parseColor("#757575")); // Dark gray
                    }
                }

//                // Set the alarm
//                if (holder.mAlarmSwitch.isChecked()) {
//                    for (Alarm a : holder.mCommute.alarm) {
//                        if (a != null) {
////                        a.setAlarm(getApplicationContext());
//                        }
//                    }
//                }
            } else { // not active
                holder.mAlarmSwitch.setChecked(false);
//                holder.mAlarmRepeat.setColorFilter(Color.parseColor("#BDBDBD"));// Light gray
                for (int i = 0; i < holder.mCommute.weekInfo.days.length; i++) {
                    holder.mDayList.get(i).setTextColor(Color.parseColor("#BDBDBD"));// Light gray
                }
            }
            // Tell the service to check for the next alarm again in case its changed
            callAlarmScheduleService();

            holder.mAlarmSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Set the alarm as active/inactive
                    if (holder.mAlarmSwitch.isChecked()) {
                        holder.mCommute.activateAlarms();

                        // TODO We probably want to update the DB here (Though it could be very slow, if its a problem we can find another spot)
                    } else {
                        holder.mCommute.disarmAlarms();

                        // TODO We probably want to update the DB here (Though it could be very slow, if its a problem we can find another spot)
                    }

                    // Call the service
                    callAlarmScheduleService();

                    // Setting the colors
//                    if (holder.mCommute.weekInfo.repeat == true) {
//                        holder.mAlarmRepeat.setColorFilter(Color.parseColor("#ff4081"));// Pink
//                    } else {
//                        holder.mAlarmRepeat.setColorFilter(Color.parseColor("#757575")); // Dark gray
//                    }
                    for (int i = 0; i < holder.mCommute.weekInfo.days.length; i ++) {
                        if (holder.mAlarmSwitch.isChecked()) {
                            if (holder.mCommute.weekInfo.days[i]) {
                                holder.mDayList.get(i).setTextColor(Color.parseColor("#ff4081"));// Pink
                            } else {
                                holder.mDayList.get(i).setTextColor(Color.parseColor("#757575")); // Dark gray
                            }
                        } else {
//                            holder.mAlarmRepeat.setColorFilter(Color.parseColor("#BDBDBD")); // Light gray
                            holder.mDayList.get(i).setTextColor(Color.parseColor("#BDBDBD")); // Light gray
                        }
                    }

                    // Show the toast
                    if (holder.mAlarmSwitch.isChecked()) {
                        if (holder.mCommute.getNextAlarm() != null) {
                            Toast.makeText(CommuteListActivity.this, holder.mCommute.getNextAlarm().getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            // Probably wont ever get here now but just in case
                            // (if alarm is in the past we move it 1 week ahead by the activateAlarm then UpdateAlarm calls)
                            Toast.makeText(CommuteListActivity.this, "Cannot set alarm for a time in the past", Toast.LENGTH_LONG).show();
                        }
                    }


                    }
            });

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CommuteDetailFragment.ARG_ITEM_ID, holder.mCommute.id);
                        CommuteDetailFragment fragment = new CommuteDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.commute_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CommuteDetailActivity.class);
                        intent.putExtra(CommuteDetailFragment.ARG_ITEM_ID, holder.mCommute.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mContentView;
            final Switch mAlarmSwitch;
            final TextView mTimeView;
//            final ImageView mAlarmRepeat;
            final TextView mSunday, mMonday, mTuesday, mWednesday, mThursday, mFriday, mSaturday;
            final LinkedList<TextView> mDayList = new LinkedList<>();
            Commute mCommute;

            public ViewHolder(View view) {
                super(view);
                mView = view;
//                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mTimeView = (TextView) view.findViewById(R.id.timeTextView);
                mAlarmSwitch = (Switch) view.findViewById(R.id.alarmSwitch);
//                mAlarmRepeat = (ImageView) view.findViewById(R.id.alarmRepeat);
                mSunday = (TextView) view.findViewById(R.id.textViewSun);
                mMonday = (TextView) view.findViewById(R.id.textViewMon);
                mTuesday = (TextView) view.findViewById(R.id.textViewTues);
                mWednesday = (TextView) view.findViewById(R.id.textViewWed);
                mThursday = (TextView) view.findViewById(R.id.textViewThur);
                mFriday = (TextView) view.findViewById(R.id.textViewFri);
                mSaturday = (TextView) view.findViewById(R.id.textViewSat);
                mDayList.add(mSunday);
                mDayList.add(mMonday);
                mDayList.add(mTuesday);
                mDayList.add(mWednesday);
                mDayList.add(mThursday);
                mDayList.add(mFriday);
                mDayList.add(mSaturday);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
