package alarmManager;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.jeffrey.finalprototype.Content;
import com.example.jeffrey.finalprototype.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.example.jeffrey.finalprototype.Content.COMMUTE_MAP;

public class AlarmService extends Service {

    /**
     * Gets the next Alarm that is active and will go off out of all alarms
     * @return
     */
    public static Alarm getNext(){
        Set<Alarm> alarmQueue = new TreeSet<Alarm>(new Comparator<Alarm>() {
            @Override
            public int compare(Alarm lhs, Alarm rhs) {
                int result = 0;
                long diff = lhs.getAlarmTime().getTimeInMillis() - rhs.getAlarmTime().getTimeInMillis();
                if(diff>0){
                    return 1;
                }else if (diff < 0){
                    return -1;
                }
                return result;
            }
        });

        List<Alarm> alarms = new LinkedList<>();
        System.out.println(COMMUTE_MAP.values());
        for (Content.Commute c :COMMUTE_MAP.values()) {
            alarms.addAll(Arrays.asList(c.alarms));
        }

        for(Alarm alarm : alarms){
            if (alarm != null && alarm.repeat && alarm.armed && alarm.getAlarmTime().getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                // In here means this alarm is in the past but needs to be repeated so we can just add 1 week to the alarm
                alarm.getAlarmTime().add(Calendar.WEEK_OF_YEAR, 1);
            }
            if(alarm != null && alarm.armed && alarm.getAlarmTime().getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
                alarmQueue.add(alarm);
        }
        if(alarmQueue.iterator().hasNext()){
            return alarmQueue.iterator().next();
        }else{
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("Inside of onStartCommand for AlarmService");
        Alarm alarm = getNext();
        if(alarm != null){
            alarm.scheduleAlarm(getApplicationContext());

        }else{
            Intent myIntent = new Intent(getApplicationContext(), AlarmAlertBroadcastReceiver.class);
            myIntent.putExtra("alarm", new Alarm());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            alarmManager.cancel(pendingIntent);
        }
        return START_NOT_STICKY;
    }

}