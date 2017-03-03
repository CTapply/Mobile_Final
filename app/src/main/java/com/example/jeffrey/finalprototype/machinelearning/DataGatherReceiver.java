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

    private Integer hours, minutes, prepTime, newPrepTime;
    private String day;
    private String commuteID;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("GEOFENCE")) {
            hours = intent.getIntExtra("hour", 0);
            minutes = intent.getIntExtra("minute", 0);
            day = intent.getStringExtra("day");

            // We should see if is snowing today


        } else if (intent.getAction().equals("ALARM")) {
            commuteID = intent.getStringExtra("commuteID");
            prepTime = intent.getIntExtra("prep_time", 0);
        }

        // check if we have everything and should record data, it's late just let it happen
        if(hours != null && minutes != null && day != null && prepTime != null && commuteID != null){
            // calculate the actual prep time:
            int extraTime = getExtraTime((hoursToMinutes(hours) + minutes));

            // now how do we estimate the new prep time?? Some fraction of the extra time?
            // if we were late, give extra time; else, give slightly more
            if(extraTime <= 0)
                newPrepTime = prepTime - (int)Math.ceil(extraTime * 1.3);
            else
                newPrepTime = prepTime + (int)Math.ceil(extraTime * 0.7);

            writeToFile(context);
            readFromFile(context);


            resetValues(); // cheese but this way we know when we have all of the data
        }
    }

    /**
     * Reads from the csv file containing the training data and prints out to the console
     * @param context
     */
    private void readFromFile(Context context){
        InputStream inputStream = null;
        try {
            inputStream = context.openFileInput("training_data.csv");

            if (inputStream != null) {
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

            /**
             * learning parameters
             *
             * <input>
             * prepTime,
             * day (0, 1, 2, 3, 4, 5, 6)
             * snowing (0 - no, 1 - yes)
             *
             * <output>
             * newPrepTime --> should the output be the amount of "adjustment time"?
             */

            sb.append(prepTime);
            sb.append(',');
            sb.append(day);
            sb.append(',');
            // sb.append(snowing)
            // sb.append(',')
            sb.append(newPrepTime);
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
     * @return Difference between departure alarm and geofence enter, tells us how much extra time we have leftover
     */
    private int getExtraTime(int arrivalTimeGeo){
        for(Content.Commute c : COMMUTE_MAP.values()){
            if(c.id.equals(this.commuteID)){
                // we want to get the departure alarm for today
                /**
                int dayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                Alarm commuteDeparture = c.alarms[14 % dayIndex - 1];

                int hourDep = commuteDeparture.alarmTime.get(Calendar.HOUR_OF_DAY);
                int minDep = commuteDeparture.alarmTime.get(Calendar.MINUTE);
                minDep += hoursToMinutes(hourDep); // put it all into minutes

                System.out.println("DEPARTURE: " + hourDep);
                System.out.println("DEPARTURE: " + minDep);
                 */

                // use the user time to arrive to estimate the extra time
                int arrivalCommute = hoursToMinutes(c.arrivalTimeHour) + c.arrivalTimeMin;
                return arrivalTimeGeo - 5 - arrivalCommute;


                // return arrivalTimeGeo - minDep;
            }
        }

        return 0; // commute not found
    }

    /**
     * Reset our variables to anticipate the next set of data
     */
    public void resetValues(){
        hours = null;
        minutes = null;
        prepTime = null;
        newPrepTime = null;
        day = null;
        commuteID = null;
    }
}
