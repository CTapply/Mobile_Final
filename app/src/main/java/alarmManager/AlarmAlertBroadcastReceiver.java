package alarmManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Cory on 2/23/17.
 */

public class AlarmAlertBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent AlarmServiceIntent = new Intent(context, AlarmServiceReceiver.class);
        context.sendBroadcast(AlarmServiceIntent, null);

        StaticWakeLock.lockOn(context);
        Bundle bundle = intent.getExtras();
        final Alarm alarm = (Alarm) bundle.getSerializable("alarm");
        final String destination = bundle.getString("destination");


        Intent AlertActivityIntent;

        AlertActivityIntent = new Intent(context, AlarmAlertActivity.class);

        AlertActivityIntent.putExtra("alarm", alarm);
        AlertActivityIntent.putExtra("destination", destination);

        AlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(AlertActivityIntent);
    }
}
