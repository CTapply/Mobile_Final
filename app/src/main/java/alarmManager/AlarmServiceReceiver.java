package alarmManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        System.out.println("Inside of AlarmServiceReceiver onReceive");


        Intent serviceIntent = new Intent(context, AlarmService.class);
        context.startService(serviceIntent);



    }
}
