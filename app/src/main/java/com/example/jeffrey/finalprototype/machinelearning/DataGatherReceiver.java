package com.example.jeffrey.finalprototype.machinelearning;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;

import com.example.jeffrey.finalprototype.Content;
import com.example.jeffrey.finalprototype.weather.JSONWeatherParser;
import com.example.jeffrey.finalprototype.weather.WeatherHttpClient;
import com.example.jeffrey.finalprototype.weather.model.Weather;

import org.json.JSONException;

import java.io.OutputStream;

import static com.example.jeffrey.finalprototype.Content.COMMUTE_MAP;

/**
 * Simply updates our training data with the parameters we classify
 * Created by Gatrie on 3/1/2017.
 */
public class DataGatherReceiver extends Service {
    private static BroadcastReceiver m_ScreenOffReceiver;
    private Integer hours, minutes, prepTime, newPrepTime, day;
    private Integer snowing = 0;
    private String commuteID;
    private DataGatherReceiver.JSONWeatherTask task;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        registerScreenOffReceiver();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(m_ScreenOffReceiver);
        m_ScreenOffReceiver = null;
    }

    private void registerScreenOffReceiver() {
        m_ScreenOffReceiver = new BroadcastReceiver() {
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
                    task = new DataGatherReceiver.JSONWeatherTask();
                    task.execute(new String[]{city});
                }

                // check if we have everything and should record data, it's late just let it happen
                if(hours != null && minutes != null && day != null && prepTime != null && commuteID != null){
                    // calculate the actual prep time:
                    int extraTime = getExtraTime((hoursToMinutes(hours) + minutes));
                    int snowDelay = calcSnowDelay();

                    // now how do we estimate the new prep time?? Some fraction of the extra time?
                    // if we were late, give extra time; else, give slightly more
                    if(extraTime <= 0)
                        newPrepTime = prepTime - (int)Math.ceil(extraTime * 1.3) + snowDelay;
                    else
                        newPrepTime = prepTime + (int)Math.ceil(extraTime * 0.7) + snowDelay;

                    // Update the training file
                    writeToFile(context);

                    // Now call the machine learning model to get the true prep time
                    Intent intentMachine = new Intent();
                    intentMachine.setAction("MACHINE");

                    intentMachine.putExtra("prepTime", "" + prepTime);
                    intentMachine.putExtra("day", "" + day);
                    intentMachine.putExtra("snowfall", "" + snowing);
                    intentMachine.putExtra("newPrepTime", "" + newPrepTime);
                    intentMachine.putExtra("commuteID", commuteID);

                    context.sendBroadcast(intentMachine);

                    // cheese but this way we know when we have all of the data
                    resetValues();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("GEOFENCE");
        filter.addAction("ALARM");
        registerReceiver(m_ScreenOffReceiver, filter);
    }

    private void writeToFile(Context context){
        try{
            StringBuilder sb = new StringBuilder();
            OutputStream str = context.openFileOutput("training_data_fin.csv", MODE_APPEND);

                sb.append(prepTime);
                sb.append(',');
                sb.append(day);
                sb.append(',');
                sb.append(snowing);
                sb.append(',');
                sb.append(newPrepTime);
                sb.append('\n');

                System.out.println("WRITING: " + sb.toString());

                str.write(sb.toString().getBytes());

                str.close();
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
                return arrivalTimeGeo - 5 - arrivalCommute - 10;
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
     * Helper function for calcSnowDelay();
     * Calculates the coefficient between [0.25, 1.25] based on the input latitude
     * @param latitude Latitude in degrees of the selected location
     * @return Coefficient in range [0.25, 1.25]
     */
    public double calcCoefficient(double latitude){
        final float latMax = 55, latMin = 20;
        final float coefficientMax = 1.25f, coefficientMin = 0.25f;

        /**
         * We bind the range of the latitude between 55 degrees (Thompson, Manitoba)
         * and 20 degrees (Mexico City) for simplicity. Anything above or below
         * is set to these thresholds
         */
        if(latitude > latMax)
            latitude = latMax;

        if(latitude < latMin)
            latitude = latMin;

        // The offset is the amount per degree latitude we add to the coefficient
        float offset = (coefficientMax - coefficientMin) / (latMax - latMin);

        // Calculated coefficient based on the offset from the input latitude
        double coefficient = coefficientMin + ((latMax - latitude) * offset);

        return coefficient;
    }

    /**
     * Calculate the snow delay time (in minutes) based on the latitude and amount of snowfall
     * @return Number of minutes delayed by the snow
     */
    public int calcSnowDelay(){
        for(Content.Commute c : COMMUTE_MAP.values()) {
            if (c.id.equals(this.commuteID)) {
                return (int)(calcCoefficient(c.latitude)*(snowing + 10));
            }
        }
        return 0;
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
