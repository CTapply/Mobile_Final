package com.example.jeffrey.finalprototype.machinelearning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.jeffrey.finalprototype.Content;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

import alarmManager.Alarm;

import static com.example.jeffrey.finalprototype.Content.COMMUTE_MAP;

/**
 * Simply updates our training data with the parameters we classify
 * Created by Gatrie on 3/1/2017.
 */
public class DataGatherReceiver extends BroadcastReceiver {

    private Integer hours, minutes, prepTime, extraTime;
    private String day;
    private String commuteID;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("GEOFENCE")) {
            hours = intent.getIntExtra("hour", 0);
            minutes = intent.getIntExtra("minute", 0);
            day = "" + intent.getStringExtra("day");
        } else if (intent.getAction().equals("ALARM")) {
            commuteID = intent.getStringExtra("commuteID");
            prepTime = intent.getIntExtra("prep_time", 0);
        }

        // check if we have everything and should record data, it's late just let it happen
        if(hours != null && minutes != null && day != null && prepTime != null && commuteID != null){
            // record data to the csv
            writeToFile(context);

            // calculate the actual prep time
            extraTime = getDepartureDifference((hoursToMinutes(hours) + minutes)) - 5 - prepTime;

            writeToFile(context);
            readFromFile(context);

            resetValues(); // cheese but this way we know when we have all of the data
        }
    }

    // file reading
    private void readFromFile(Context context){
        // read from it
        InputStream inputStream = null;
        try {
            inputStream = context.openFileInput("training_data.csv");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                System.out.println("RECEIVED: " + stringBuilder.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void writeToFile(Context context){
        try{
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("training_data.csv", Context.MODE_PRIVATE));
            StringBuilder sb = new StringBuilder();

            sb.append(prepTime);
            sb.append(',');
            sb.append(minutes);
            sb.append(',');
            sb.append(hours);
            sb.append(',');
            sb.append(day);
            sb.append(',');
            sb.append(extraTime);
            sb.append('\n');

            outputStreamWriter.write(sb.toString());
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert hours to minutes
     * @param hours Number of hours
     * @return Time in minutes
     */
    private int hoursToMinutes(int hours){
        return hours * 60;
    }

    /**
     * Calculate the travel time between their commute and when they enter the geofence
     * @pararm arrivalTime Timestamp from geofence
     * @return Difference between departure alarm and geofence enter
     */
    private int getDepartureDifference(int arrivalTime){
        for(Content.Commute c : COMMUTE_MAP.values()){
            if(c.id.equals(this.commuteID)){
                // we want to get the departure alarm for today
                int dayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                Alarm commuteDeparture = c.alarms[14 % dayIndex - 1];

                int hourDep = commuteDeparture.alarmTime.get(Calendar.HOUR_OF_DAY);
                int minDep = commuteDeparture.alarmTime.get(Calendar.MINUTE);
                minDep += hoursToMinutes(hourDep); // put it all into minutes

                System.out.println("DEPARTURE: " + hourDep);
                System.out.println("DEPARTURE: " + minDep);

                return arrivalTime - minDep;
            }
        }

        return 0; // commute not found
    }

    public void resetValues(){
        hours = null;
        minutes = null;
        prepTime = null;
        extraTime = null;
        day = null;
        commuteID = null;
    }
}
