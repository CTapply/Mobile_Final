package com.example.jeffrey.finalprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.w3c.dom.Text;

/**
 * Created by Jeffrey on 2/16/2017.
 */

public class AddNewCommute extends AppCompatActivity {

    private static final int ARR_TIME_REQUEST = 1;
    private static final int PREP_TIME_REQUEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;
    EditText editTextID;
    TextView selectedArrTime;
    TextView selectedPrepTime;
    TextView selectedDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_commute);

        editTextID  = (EditText) findViewById(R.id.editTextCommuteID);
        selectedArrTime = (TextView) findViewById(R.id.selectedArrTime);
        selectedPrepTime = (TextView) findViewById(R.id.selectedPrepTime);
        selectedDestination = (TextView) findViewById(R.id.selectedDestination);

        Button arrTimeButton = (Button) findViewById(R.id.chooseArrTimeButton);
        Button prepTimeButton = (Button) findViewById(R.id.choosePrepTimeButton);
        Button destButton = (Button) findViewById(R.id.chooseDestButton);

        // TODO replace class used for intents
        final Intent chooseArrTime = new Intent(this, PickTime.class);
        final Intent choosePrepTime = new Intent(this, PickNumber.class);
        final Intent chooseDestination = new Intent(this, PickTime.class); // AND HERE

        arrTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(chooseArrTime, 1);
            }
        });

        prepTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(choosePrepTime, 2);
            }
        });

        destButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
//                    intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Intent intent = intentBuilder.build(AddNewCommute.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ARR_TIME_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                int arrHour = data.getIntExtra("hour", 12);
                int arrMin = data.getIntExtra("minute", 0);
                String time = semanticTime(arrHour, arrMin);
                selectedArrTime.setText(time);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == PREP_TIME_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                int prepMins = data.getIntExtra("mins", 0);
                String time = semanticPrep(prepMins);
                selectedPrepTime.setText(time);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                final Place place = PlacePicker.getPlace(this, data);
                final CharSequence address = place.getAddress();
                selectedDestination.setText(address);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("id", editTextID.getText().toString());
        savedInstanceState.putString("arr_time", selectedArrTime.getText().toString());
        savedInstanceState.putString("prep_time", selectedPrepTime.getText().toString());
        savedInstanceState.putString("destination", selectedDestination.getText().toString());

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String commuteID = savedInstanceState.getString("id");
        String arrTime = savedInstanceState.getString("arr_time");
        String prepTime = savedInstanceState.getString("prep_time");
        String dest = savedInstanceState.getString("destination");

        editTextID.setText(commuteID);
        selectedArrTime.setText(arrTime);
        selectedPrepTime.setText(prepTime);
        selectedDestination.setText(dest);
    }

    public String semanticPrep(int minutes){
        String prepTime = "";
        if(minutes >= 60){
            int hours = minutes / 60;
            prepTime += Integer.toString(hours);
            if(hours == 1)
                prepTime += " hours ";
            else
                prepTime += " hour ";
        }
        int mins = minutes % 60;
        prepTime += Integer.toString(mins);
        if(mins == 1)
            prepTime += " min";
        else
            prepTime += " mins";
        return prepTime;
    }

    public String semanticTime(int hour, int min){
        String time = "";
        String timeMode;
        int hold = hour;
        if(hold > 12){
            time += Integer.toString(hold-12);
            timeMode = "PM";
        } else {
            time += Integer.toString(hold);
            timeMode = "AM";
        }
        time += ":";
        hold = min;
        if(hold < 10){
            time += Integer.toString(0);
        }
        time += Integer.toString(hold);

        time += " " + timeMode;
        return time;
    }

    public int getHourFromTime(String time){
        int colonPos = time.indexOf(':');
        String hourString = time.substring(0, colonPos);
        return Integer.parseInt(hourString);
    }

    public int getMinFromTime(String time){
        int end = time.length() - 3;
        int beg = end - 2;
        String minString = time.substring(beg, end);
        return Integer.parseInt(minString);
    }

    public int getPrepMins(String prepString){
        int end = prepString.length() - 3;
        String minString = prepString.substring(0, end);
        return Integer.parseInt(minString);
    }
}
