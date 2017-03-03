package alarmManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.PowerManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.jeffrey.finalprototype.CommuteListActivity;
import com.example.jeffrey.finalprototype.Content.Commute;
import com.example.jeffrey.finalprototype.DirectionsHttpClient;

import org.json.JSONException;
import org.json.JSONObject;
import com.example.jeffrey.finalprototype.machinelearning.DataGatherReceiver;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Cory on 2/23/17.
 */

public class Alarm implements Serializable {

    public int arrivalHour;
    public int arrivalMinutes;
    private int prepTimeInMinutes;
    public Calendar alarmTime = Calendar.getInstance();
    private int day;
    public int type; // Saving type, if >= 7, then depart alarm, < 7 is wake up
    public boolean repeat;
    public String alarmTonePath;

    public boolean armed;
    public transient Commute commute;

    public Alarm(int arrivalHour, int arrivalMinutes, int prepTimeInMinutes, int type, boolean armed, String alarmTonePath) {
        this.arrivalHour = arrivalHour;
        this.arrivalMinutes = arrivalMinutes;
        this.prepTimeInMinutes = prepTimeInMinutes;
        this.day = type%7;
        this.type = type;
        this.armed = armed;
        this.alarmTonePath = alarmTonePath;
    }

    /**
     * Empty, just used for cancelling the alarms
     */
    public Alarm() {}

    public void setCommute(Commute c) {
        commute = c;
        this.repeat = c.weekInfo.repeat;
    }

    public Calendar getAlarmTime() {
        return alarmTime;
    }


        /**
         * Starts the alarm for the first time, should only call this in the constructor for the alarm (and possibly on system boot)
         */
    public void setAlarmTime(Context context) {
        int travelTime;
        String DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/json?";
        String origin;
        String destination;
        String key = "&key=AIzaSyCqpUlcpi5O_oYuB5KfqF4C_e0Er5c5n1E";

        // TODO: We need to do the calculation for when alarms are set and use as 2nd parameter
        //Currently setting the alarm to the time they need to be at work PLEASE CHANGE


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get current location Latitute and Longitute
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            origin = "origin=" + latitude + "," + longitude;

            // Set Destination
            destination = "&destination=";
            destination = destination + commute.destination.replaceAll("\\s","");

            // Ask google to get directions from current location to the destination via car (default is driving)
            String REQUEST = DIRECTIONS_API_URL + origin + destination + key;

            DirectionsHttpClient client = new DirectionsHttpClient();
            String response = client.getDirectionData(REQUEST);



            if (response.equals("")) {
                // Could not connect so set to the default
                // TODO asdkasjd lkjsdf;dsalkfj sdflkj sf
                travelTime = 20;
            } else {


                try {
                    JSONObject respJson = new JSONObject(response);

                    JSONObject routesJson = respJson.getJSONArray("routes").getJSONObject(0);
                    JSONObject legsJson = routesJson.getJSONArray("legs").getJSONObject(0);
                    JSONObject durationJson = legsJson.getJSONObject("duration");
//                JSONObject valueJson = durationJson.getJSONObject("value");
                    travelTime = durationJson.getInt("value") / 60; // Divide by 60 since we get the value in seconds
                    System.out.println("Travel Estimate from Google Directions: " + travelTime);

                } catch (JSONException e) {
                    // TODO asdasdahsdhasdjashdas
                    travelTime = 20;
                }
            }


        } else {
            // We cant get current location so we cant find the route time, so we should use time from previous travels
            // TODO lskjfhsad kjlhfl kasdhflkj adslfkjhasd lkfjh asldkjhg askldfh klsadf
            travelTime = 20;
        }

        if (type >= 0 && type < 7 ) { // WAKE UP ALARM

            alarmTime.set(Calendar.DAY_OF_WEEK, day+1); // +1 here because Calendar.SUNDAY = 1, not 0
            alarmTime.set(Calendar.HOUR_OF_DAY, arrivalHour);
            alarmTime.set(Calendar.MINUTE, arrivalMinutes);
            alarmTime.set(Calendar.SECOND, 0);

            alarmTime.add(Calendar.MINUTE, -(travelTime + 10 + prepTimeInMinutes));

            System.out.println("WAKE UP ALARM TIME " + alarmTime.get(Calendar.MINUTE));

        } else if ( type >= 7 && type < 14){ // DEPART ALARM

            alarmTime.set(Calendar.DAY_OF_WEEK, day+1); // +1 here because Calendar.SUNDAY = 1, not 0
            alarmTime.set(Calendar.HOUR_OF_DAY, arrivalHour);
            alarmTime.set(Calendar.MINUTE, arrivalMinutes);
            alarmTime.set(Calendar.SECOND, 0);

            alarmTime.add(Calendar.MINUTE, -(travelTime + 10)); // +10 minutes so they get to work a little early

            System.out.println("DEPART ALARM TIME " + alarmTime.get(Calendar.MINUTE));

        } else if(type >= 14 && type < 21) {

            alarmTime.set(Calendar.DAY_OF_WEEK, day+1); // +1 here because Calendar.SUNDAY = 1, not 0
            alarmTime.set(Calendar.HOUR_OF_DAY, arrivalHour);
            alarmTime.set(Calendar.MINUTE, arrivalMinutes);
            alarmTime.set(Calendar.SECOND, 0);

            alarmTime.add(Calendar.MINUTE, -(travelTime + 10 + prepTimeInMinutes + 60)); // +60 so its 1 hour in advance


        }

    }

    public void scheduleAlarm(Context context) {
        this.armed = true;
        Intent intent = new Intent(context, AlarmAlertBroadcastReceiver.class);
        intent.putExtra("alarm", this);
        intent.putExtra("destination", this.commute.destination);

        // Cancels the current alarm
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);

        // Starts the new next alarm (if something changed)
        if (type < 14) { // Want to go Wake the screen (goes to AlarmAlertBroadcastReceiver)
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        } else {
            // this means the alarm isnt a real alarm, we dont want to wake the phone but we want
            // to do the machine learning calculations
            intent = new Intent();
            intent.setAction("ALARM");
            intent.putExtra("prep_time", this.prepTimeInMinutes);
            intent.putExtra("commute_id", this.commute.id);
            intent.putExtra("destination", this.commute.destination);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
            context.sendBroadcast(intent);
        }
    }

    public String getTimeUntilNextAlarmMessage(){
        long timeDifference = getAlarmTime().getTimeInMillis() - System.currentTimeMillis();
        long days = timeDifference / (1000 * 60 * 60 * 24);
        long hours = timeDifference / (1000 * 60 * 60) - (days * 24);
        long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
        long seconds = timeDifference / (1000) - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);
        String alert = "Alarm will sound in ";
        if (days > 0) {
            alert += String.format(
                    "%d days, %d hours, %d minutes and %d seconds", days,
                    hours, minutes, seconds);
        } else {
            if (hours > 0) {
                alert += String.format("%d hours, %d minutes and %d seconds",
                        hours, minutes, seconds);
            } else {
                if (minutes > 0) {
                    alert += String.format("%d minutes, %d seconds", minutes,
                            seconds);
                } else {
                    alert += String.format("%d seconds", seconds);
                }
            }
        }
        return alert;
    }

    /**
     * This will turn on/off both the wake up and the departure alarms
     */
    public void updateAlarm() {

        if (this.getAlarmTime().getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            // In here means this alarm is in the past but needs to be repeated so we can just add 1 week to the alarm
            this.alarmTime.add(Calendar.WEEK_OF_YEAR, 1);
        }
    }
}
