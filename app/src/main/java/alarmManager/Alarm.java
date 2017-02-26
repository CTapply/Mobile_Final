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
    public Calendar alarmTime = Calendar.getInstance();
    private int day;
    private int type; // Saving type, if >= 7, then depart alarm, < 7 is wake up
    public boolean repeat;

    public boolean armed;
    private transient Commute commute;

    public Alarm(int arrivalHour, int arrivalMinutes, int prepTimeInMinutes, int type, boolean armed) {
        this.arrivalHour = arrivalHour;
        this.arrivalMinutes = arrivalMinutes;
        this.prepTimeInMinutes = prepTimeInMinutes;
        this.day = type%7;
        this.type = type;
        this.armed = armed;
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
        System.out.println("Inside of setAlarm");

        // TODO: We need to do the calculation for when alarms are set and use as 2nd parameter
        //Currently setting the alarm to the time they need to be at work PLEASE CHANGE

        // ASSUME 20 MINUTE TRAVEL TIME FOR NOW
        int travelTime = 20;

        if (type >= 0 && type < 7 ) { // WAKE UP ALARM

            alarmTime.set(Calendar.DAY_OF_WEEK, day+1); // +1 here because Calendar.SUNDAY = 1, not 0
            alarmTime.set(Calendar.HOUR_OF_DAY, arrivalHour);
            alarmTime.set(Calendar.MINUTE, arrivalMinutes);
            alarmTime.set(Calendar.SECOND, 0);

            System.out.println(alarmTime.getTime().toString());

            alarmTime.add(Calendar.MINUTE, -travelTime);

            System.out.println(alarmTime.getTime().toString());

        } else if ( type >= 7 && type < 14){ // DEPART ALARM

            alarmTime.set(Calendar.DAY_OF_WEEK, day+1); // +1 here because Calendar.SUNDAY = 1, not 0
            alarmTime.set(Calendar.HOUR_OF_DAY, arrivalHour);
            alarmTime.set(Calendar.MINUTE, arrivalMinutes);
            alarmTime.set(Calendar.SECOND, 0);

            alarmTime.add(Calendar.MINUTE, -(travelTime + prepTimeInMinutes));

        }

    }

    public void scheduleAlarm(Context context) {
        this.armed = true;
        Intent intent = new Intent(context, AlarmAlertBroadcastReceiver.class);
        intent.putExtra("alarm", this);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
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
