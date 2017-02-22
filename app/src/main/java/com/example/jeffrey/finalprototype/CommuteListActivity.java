package com.example.jeffrey.finalprototype;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
import android.widget.TextView;


import com.example.jeffrey.finalprototype.Content.Commute;
import com.example.jeffrey.finalprototype.Content.WeeklyInfo;
import com.example.jeffrey.finalprototype.machinelearning.BaseNetwork;
import com.example.jeffrey.finalprototype.weather.JSONWeatherParser;
import com.example.jeffrey.finalprototype.weather.WeatherHttpClient;
import com.example.jeffrey.finalprototype.weather.model.Weather;

import org.json.JSONException;

import java.util.List;

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

    String defaultCity = "Worcester,us";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static final int NEW_COMMUTE_REQUEST = 50;

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

        /** Uncomment these two lines to test the weather task */
        //JSONWeatherTask task = new JSONWeatherTask();
        //task.execute(new String[]{defaultCity});

        /** Uncomment this line to test a basic neural network */
        BaseNetwork net = new BaseNetwork();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NEW_COMMUTE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra("id");
                int arrHour = data.getIntExtra("arr_hour", 12);
                int arrMin = data.getIntExtra("arr_min", 0);
                int prepMins = data.getIntExtra("prep_mins", 0);
                String destination = data.getStringExtra("destination");

                boolean sunday = data.getBooleanExtra("sunday", false);
                boolean monday = data.getBooleanExtra("monday", false);
                boolean tuesday = data.getBooleanExtra("tuesday", false);
                boolean wednesday = data.getBooleanExtra("wednesday", false);
                boolean thursday = data.getBooleanExtra("thursday", false);
                boolean friday = data.getBooleanExtra("friday", false);
                boolean saturday = data.getBooleanExtra("saturday", false);
                boolean repeat = data.getBooleanExtra("repeat", false);

                WeeklyInfo w = makeWeek(sunday, monday, tuesday, wednesday, thursday, friday,
                            saturday, repeat);

                Commute newCommute = new Commute(name, destination, arrHour, arrMin, prepMins, w);
                addItem(newCommute);

                View recyclerView = findViewById(R.id.commute_list);
                assert recyclerView != null;
                setupRecyclerView((RecyclerView) recyclerView);
            }
        }
    }

    public WeeklyInfo makeWeek(boolean su, boolean m, boolean tu, boolean w, boolean th,
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

        Context mContext = getApplicationContext();
        SQLiteDatabase mDatabase = new CommuteBaseHelper(mContext).getWritableDatabase();

//        for (Commute commute : Content.ITEMS){
//            ContentValues values = getContentValues(commute);
//            mDatabase.insert(CommuteTable.NAME, null, values);
//        }
    }

    private static ContentValues getContentValues(Commute commute){
        ContentValues values = new ContentValues();
        values.put(CommuteTable.Cols.ID, commute.id);
        values.put(CommuteTable.Cols.ARR_HOUR, commute.arrivalTimeHour);
        values.put(CommuteTable.Cols.ARR_MIN, commute.arrivalTimeMin);
        values.put(CommuteTable.Cols.PREP_MINS, commute.preparationTime);
        values.put(CommuteTable.Cols.DESTINATION, commute.destination);
        return values;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Content.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Commute> mValues;

        public SimpleItemRecyclerViewAdapter(List<Commute> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.commute_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).destination);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CommuteDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        CommuteDetailFragment fragment = new CommuteDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.commute_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CommuteDetailActivity.class);
                        intent.putExtra(CommuteDetailFragment.ARG_ITEM_ID, holder.mItem.id);

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
            final TextView mIdView;
            final TextView mContentView;
            Commute mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    /**
     * Private class for monitoring weather information in the background
     * TODO: Push this into the machine learning code
     */
    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {
        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                System.out.println("WEATHER COND: " + weather.currentCondition.getDescr());
                System.out.println("WEATHER TEMP: " + weather.temperature.getTemp() + "F");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }
    }
}
