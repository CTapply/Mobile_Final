package com.example.jeffrey.finalprototype.machinelearning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.jeffrey.finalprototype.Content;
import com.example.jeffrey.finalprototype.weather.JSONWeatherParser;
import com.example.jeffrey.finalprototype.weather.WeatherHttpClient;
import com.example.jeffrey.finalprototype.weather.model.Weather;

import org.json.JSONException;

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

    private Integer hours, minutes, prepTime, newPrepTime, day;
    private Integer snowing = 0;
    private String commuteID;
    private JSONWeatherTask task;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("GEOFENCE")) {
            hours = intent.getIntExtra("hour", 0);
            minutes = intent.getIntExtra("minute", 0);
            day = intent.getIntExtra("day", 0);

        } else if (intent.getAction().equals("ALARM")) {
            commuteID = intent.getStringExtra("commuteID");
            prepTime = intent.getIntExtra("prep_time", 0);

            // We should see if is snowing today, this sets the snowing variable
            String city = parseCity(intent.getStringExtra("destination"));
            task = new JSONWeatherTask();
            task.execute(new String[]{city});
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

            // Update the training file
            writeToFile(context);
            readFromFile(context);

            // we need to set the commute to have this new prep time
            setCommutePrep();

            // cheese but this way we know when we have all of the data
            resetValues();
        }
    }

    /**
     * Reads from the csv file containing the training data and prints out to the console
     * @param context
     */
    private void readFromFile(Context context){
        InputStream inputStream = null;
        try {
            inputStream = context.openFileInput("training_data_2.csv");

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
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("training_data_2.csv", Context.MODE_PRIVATE));
            StringBuilder sb = new StringBuilder();

            // write to the file the fields we want; the first 3 are input and the last is output
            sb.append(prepTime);
            sb.append(',');
            sb.append(day);
            sb.append(',');
            sb.append(snowing);
            sb.append(',');
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
     * @return Difference between departure alarm and geofence enter, tells us how much extra time we have leftover
     */
    private int getExtraTime(int arrivalTimeGeo){
        for(Content.Commute c : COMMUTE_MAP.values()){
            if(c.id.equals(this.commuteID)){
                // use the user time to arrive to estimate the extra time
                int arrivalCommute = hoursToMinutes(c.arrivalTimeHour) + c.arrivalTimeMin;
                return arrivalTimeGeo - 5 - arrivalCommute;
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

    /**
     * Pulls out the city name from the destination of the commute object to know which city to
     * get weather data for
     * @param destination Destination string from the place picker (stored in commute)
     * @return City named in format: [city,state/country]
     */
    private String parseCity(String destination){
        String[] strings = destination.split(",");
        String city = strings[strings.length - 3].replace(" ", "");
        String state = strings[strings.length -2].split(" ")[1].replace(" ", "");

        return city + "," + state;
    }

    /**
     * Set the commute to have the new prep time and also
     * call to update the database entry
     */
    private void setCommutePrep(){
        for(Content.Commute c : COMMUTE_MAP.values()) {
            if (c.id.equals(this.commuteID)) {
                c.preparationTime = newPrepTime;
                c.updateCommute();
            }
        }
    }

    /**
     * Private class for monitoring weather information in the background
     */
    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {
        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);
                snowing = (int)weather.snow.getAmount();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }
    }
}
