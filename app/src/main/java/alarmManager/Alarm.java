package alarmManager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.jeffrey.finalprototype.CommuteListActivity;
import com.example.jeffrey.finalprototype.Content.Commute;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Cory on 2/23/17.
 */

public class Alarm implements Serializable {

    public int arrivalHour;
    private int arrivalMinutes;
    private int prepTimeInMinutes;
    private Calendar departTime = Calendar.getInstance();
    private Calendar wakeUpTime = Calendar.getInstance();
    private int day;

    public boolean armed;
    private transient Commute commute;

    public Alarm(int arrivalHour, int arrivalMinutes, int prepTimeInMinutes, int day) {
        this.arrivalHour = arrivalHour;
        this.arrivalMinutes = arrivalMinutes;
        this.prepTimeInMinutes = prepTimeInMinutes;
        this.day = day;
        this.armed = true;
    }

    /**
     * Empty, just used for cancelling the alarms
     */
    public Alarm() {}

    public void setCommute(Commute c) {
        commute = c;
    }

    public Calendar getAlarmTime() {
        return wakeUpTime;
    }


        /**
         * Starts the alarm for the first time, should only call this in the constructor for the alarm (and possibly on system boot)
         */
    public void setAlarmTime(Context context) {
        System.out.println("Inside of setAlarm");

//        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60 * 1000, alarmIntent);

        // TODO: We need to do the calculation for when alarms are set and use as 2nd parameter
        //Currently setting the alarm to the time they need to be at work PLEASE CHANGE
        wakeUpTime.set(Calendar.DAY_OF_WEEK, day+1); // +1 here because Calendar.SUNDAY = 1, not 0
        wakeUpTime.set(Calendar.HOUR_OF_DAY, arrivalHour);
        wakeUpTime.set(Calendar.MINUTE, arrivalMinutes);
        wakeUpTime.set(Calendar.SECOND, 0);

//        departTime.set(Calendar.DAY_OF_WEEK, day+1); // +1 here because Calendar.SUNDAY = 1, not 0
//        departTime.set(Calendar.HOUR_OF_DAY, arrivalHour);
//        departTime.set(Calendar.MINUTE, arrivalMinutes);
//        departTime.set(Calendar.SECOND, 0);



    }

    public void scheduleAlarm(Context context) {
        this.armed = true;
        Intent intent = new Intent(context, AlarmAlertBroadcastReceiver.class);
        intent.putExtra("alarm", this);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, wakeUpTime.getTimeInMillis(), pendingIntent);
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
//        if (commute.alarmArmed) { // We need to change to OFF
//            commute.alarmArmed = false;
//            alarmManager.cancel(pendingIntent);
//            setAlarmText("");
//            Log.d("MyActivity", "Alarm Off");
//        } else { // We need to change to ON
//            commute.alarmArmed = true;
//            Log.d("MyActivity", "Alarm On");
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
//            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
//            Intent myIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
//            pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, myIntent, 0);
//            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
//        }
    }
}
