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


//    Alarm alarm = new Alarm();
//
//    public void onCreate() {
//        super.onCreate();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        alarm.setAlarm(this);
//        return START_STICKY;
//    }
//
//    @Override
//    public void onStart(Intent intent, int startId) {
//        alarm.setAlarm(this);
//    }


//    private NotificationManager alarmNotificationManager;
//
//    public AlarmService() {
//        super("AlarmService");
//    }
//
//    @Override
//    public void onHandleIntent(Intent intent) {
//        sendNotification("Wake Up! Wake Up!");
//    }
//
//    private void sendNotification(String msg) {
//        Log.d("AlarmService", "Preparing to send notification...: " + msg);
//        alarmNotificationManager = (NotificationManager) this
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, AlarmActivity.class), 0);
//
//        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
//                this).setContentTitle("Alarm").setSmallIcon(R.mipmap.ic_launcher)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
//                .setContentText(msg);
//
//
//        alamNotificationBuilder.setContentIntent(contentIntent);
//        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
//        Log.d("AlarmService", "Notification sent.");
//    }

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
            alarms.addAll(Arrays.asList(c.alarm));
        }

        for(Alarm alarm : alarms){
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
        Log.d(this.getClass().getSimpleName(),"onStartCommand()");
        System.out.println("Inside of onStartCommand for AlarmService");
        Alarm alarm = getNext();
        if(alarm != null){
            System.out.println("Alarm Hour set to: " + alarm.arrivalHour);
            alarm.scheduleAlarm(getApplicationContext());
//            Log.d(this.getClass().getSimpleName(),alarm.getTimeUntilNextAlarmMessage());

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