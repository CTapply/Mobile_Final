package com.example.jeffrey.finalprototype.machinelearning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Simply updates our training data with the parameters we classify
 * Created by Gatrie on 3/1/2017.
 */

public class DataGatherReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("GEOFENCE")) {
            System.out.println("RECEIVE: " + intent.getIntExtra("hour", 0));
            System.out.println("RECEIVE: " + intent.getIntExtra("minute", 0));
            System.out.println("RECEIVE: " + intent.getStringExtra("day"));
        }

        /**
         * prep_time from commute
         * day_of_week
         * actual_prep_time = (final_time_from_geo, - 5 - commute_time - initial_alarm_time)
         *
         * Write the data to training_data.csv if there is new data
        */
        try{
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("training_data.csv", Context.MODE_PRIVATE));
            StringBuilder sb = new StringBuilder();

            sb.append(intent.getIntExtra("minute", 0));
            sb.append(',');
            sb.append(intent.getIntExtra("hour", 0));
            sb.append(',');
            sb.append(intent.getStringExtra("day"));
            sb.append('\n');

            outputStreamWriter.write(sb.toString());
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
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
}
