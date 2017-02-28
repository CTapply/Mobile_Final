package com.example.jeffrey.finalprototype;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;


/**
 * Created by Jeffrey on 2/16/2017.
 */

public class AddNewCommute extends FragmentActivity{

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int PLACE_PICKER_REQUEST = 3;
    AlertDialog mDialog;
    EditText editTextID;
    TextView selectedArrTime;
    TextView selectedPrepTime;
    TextView selectedDestination;
    double selectedLatitude, selectedLongitude;
    boolean editMode;

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
        Button addButton = (Button) findViewById(R.id.addCommuteButton);

        final ToggleButton sunday = (ToggleButton) findViewById(R.id.sundayButton);
        final ToggleButton monday = (ToggleButton) findViewById(R.id.mondayButton);
        final ToggleButton tuesday = (ToggleButton) findViewById(R.id.tuesdayButton);
        final ToggleButton wednesday = (ToggleButton) findViewById(R.id.wednesdayButton);
        final ToggleButton thursday = (ToggleButton) findViewById(R.id.thursdayButton);
        final ToggleButton friday = (ToggleButton) findViewById(R.id.fridayButton);
        final ToggleButton saturday = (ToggleButton) findViewById(R.id.saturdayButton);
//        final CheckBox repeat = (CheckBox) findViewById(R.id.repeatCheckBox);

        final Intent addCommute = new Intent(this, CommuteListActivity.class);

        final Intent passedIntent = getIntent();
        final int uuid = passedIntent.getIntExtra("UUID", -1);
        editMode = passedIntent.getBooleanExtra("EDIT_MODE", false);
        if(editMode){
            addButton.setText(R.string.edit_commute);
            if(uuid == -1){
                System.out.println("UUID NONEXISTENT, CHECK DATA PERSISTENCE");
            }
            editTextID.setText(passedIntent.getStringExtra("id"));
            setArrivalTime(passedIntent.getIntExtra("arr_hour", 12), passedIntent.getIntExtra("arr_min", 0));
            int prepMins = passedIntent.getIntExtra("prep_mins", 0);
            selectedPrepTime.setText(semanticPrep(prepMins/60, prepMins%60));
            selectedDestination.setText(passedIntent.getStringExtra("destination"));

            selectedLatitude = passedIntent.getDoubleExtra("latitude", 0.0f);
            selectedLongitude = passedIntent.getDoubleExtra("longitude", 0.0f);

            sunday.setChecked(passedIntent.getBooleanExtra("sunday", false));
            monday.setChecked(passedIntent.getBooleanExtra("monday", false));
            tuesday.setChecked(passedIntent.getBooleanExtra("tuesday", false));
            wednesday.setChecked(passedIntent.getBooleanExtra("wednesday", false));
            thursday.setChecked(passedIntent.getBooleanExtra("thursday", false));
            friday.setChecked(passedIntent.getBooleanExtra("friday", false));
            saturday.setChecked(passedIntent.getBooleanExtra("saturday", false));
//            repeat.setChecked(passedIntent.getBooleanExtra("repeat", false));
        }


        arrTimeButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                showTimePickerDialog(view);
             }
         });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("editTextID.getText().toString() " + editTextID.getText().toString());
                addCommute.putExtra("id", editTextID.getText().toString());
                addCommute.putExtra("arr_hour", getHourFromTime(selectedArrTime.getText().toString()));
                addCommute.putExtra("arr_min", getMinFromTime(selectedArrTime.getText().toString()));
                addCommute.putExtra("prep_mins", getPrepMins(selectedPrepTime.getText().toString()));
                addCommute.putExtra("destination", selectedDestination.getText().toString());
                addCommute.putExtra("latitude", selectedLatitude);
                addCommute.putExtra("longitude", selectedLongitude);
                addCommute.putExtra("sunday", sunday.isChecked());
                addCommute.putExtra("monday", monday.isChecked());
                addCommute.putExtra("tuesday", tuesday.isChecked());
                addCommute.putExtra("wednesday", wednesday.isChecked());
                addCommute.putExtra("thursday", thursday.isChecked());
                addCommute.putExtra("friday", friday.isChecked());
                addCommute.putExtra("saturday", saturday.isChecked());
                addCommute.putExtra("repeat", true);

                if(editMode){
                    addCommute.putExtra("EDIT_MODE", editMode);
                    addCommute.putExtra("UUID", uuid);
                    startActivity(addCommute);
                } else {
                    setResult(Activity.RESULT_OK, addCommute);
                    finish();
                }

            }
        });

        checkLocationPermission();

        /**
         * This shows the dialog box for choosing Preparation time
         */
        prepTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuidler = new AlertDialog.Builder(AddNewCommute.this);
                final View mView = getLayoutInflater().inflate(R.layout.activity_pick_number, null);
                mBuidler.setView(mView);
                mDialog = mBuidler.create();

                // Hours
                final NumberPicker numPickerHours = (NumberPicker) mView.findViewById(R.id.numberPickerHours);
                numPickerHours.setMinValue(0);
                numPickerHours.setMaxValue(24);
                numPickerHours.setWrapSelectorWheel(false);

                // Minutes
                final NumberPicker numPickerMins = (NumberPicker) mView.findViewById(R.id.numberPickerMinutes);
                numPickerMins.setMinValue(0);
                numPickerMins.setMaxValue(59);
                numPickerMins.setWrapSelectorWheel(true);

                // Set Button
                final Button pickPrepTimeButton = (Button) mView.findViewById(R.id.button2);
                pickPrepTimeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String time = semanticPrep(numPickerHours.getValue(), numPickerMins.getValue());
                        selectedPrepTime.setText(time);
                        mDialog.dismiss();
                    }
                });

                mDialog.show();
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
    protected void onPause() {
        super.onPause();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                final Place place = PlacePicker.getPlace(this, data);
                final CharSequence address = place.getAddress();
                selectedLatitude = place.getLatLng().latitude;
                selectedLongitude = place.getLatLng().longitude;
                selectedDestination.setText(address);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                System.out.println("Made it to Result Canceled in Place Picker");
            }
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putString("id", editTextID.getText().toString());
//        savedInstanceState.putString("arr_time", selectedArrTime.getText().toString());
//        savedInstanceState.putString("prep_time", selectedPrepTime.getText().toString());
//        savedInstanceState.putString("destination", selectedDestination.getText().toString());
//
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        String commuteID = savedInstanceState.getString("id");
//        String arrTime = savedInstanceState.getString("arr_time");
//        String prepTime = savedInstanceState.getString("prep_time");
//        String dest = savedInstanceState.getString("destination");
//
//        editTextID.setText(commuteID);
//        selectedArrTime.setText(arrTime);
//        selectedPrepTime.setText(prepTime);
//        selectedDestination.setText(dest);
//    }

    public void setArrivalTime(int hour, int min) {
        String time = semanticTime(hour, min);
        selectedArrTime.setText(time);
    }

    /**
     * Returns the String value of prepTime for given hours and minutes
     * @param hours
     * @param minutes
     * @return Time of prep in # hours # minutes format
     */
    public String semanticPrep(int hours, int minutes){
        String prepTime = "";
        prepTime += Integer.toString(hours);
        if (hours > 1) {
            prepTime += " hours ";
        } else if (hours == 1) {
            prepTime += " hour ";
        } else {
            // do nothing
        }
        prepTime += Integer.toString(minutes);
        if (minutes == 1) {
            prepTime += " minute";
        } else {
            prepTime += " minutes";
        }
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
        if (time.toUpperCase().contains("PM")) {
            return Integer.parseInt(hourString) + 12;
        } else {
            return Integer.parseInt(hourString);
        }
    }

    public int getMinFromTime(String time){
        int end = time.length() - 3;
        int beg = end - 2;
        String minString = time.substring(beg, end);
        return Integer.parseInt(minString);
    }

    /**
     * Gets the number of minutes set for preparation (Only does in minutes, so 1 hour 15 minutes = 75 minutes)
     * @param prepString
     * @return integer number of minutes
     */
    public int getPrepMins(String prepString){
        int minutes = 0;

        String tokens[] = prepString.split(" ");
        if (tokens.length == 4) { // Means we also have a value of hours
            minutes += Integer.parseInt(tokens[0]) * 60; // This value is hours
            minutes += Integer.parseInt(tokens[2]); // This value is minutes
        } else { // Only have minutes
            minutes += Integer.parseInt(tokens[0]);
        }

        return minutes;
    }

    /**
     * Shows the dialog pop up to choose an arrival time
     * @param v
     */
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new PickTime();
        newFragment.show(getSupportFragmentManager(), "TimePicker");
    }

    /**
     * Gets the permission to use the users Location data
     * @return boolean - If we get permission = true
     */
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}
