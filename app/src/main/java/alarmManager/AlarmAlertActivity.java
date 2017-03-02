package alarmManager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.example.jeffrey.finalprototype.R;

import java.util.Calendar;


public class AlarmAlertActivity extends Activity {

    private Alarm alarm;
    private String destination;
//    private MediaPlayer mediaPlayer;


    private Vibrator vibrator;

    private boolean alarmActive;
    private Button dismissButton;
    private Button directionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.alarm_alert);

        Bundle bundle = this.getIntent().getExtras();
        alarm = (Alarm) bundle.getSerializable("alarm");
        destination = bundle.getString("destination");

        TextView alarmTime = (TextView) findViewById(R.id.textViewAlarmTime);
        TextView alarmMessage = (TextView) findViewById(R.id.textViewAlarmName);

        alarmTime.setText(semanticTime(alarm));
        alarmMessage.setText(R.string.alarmWakeUp); // Wake up by default

        dismissButton = (Button) findViewById(R.id.dismissButton);
        directionsButton = (Button) findViewById(R.id.directionsButton);
        directionsButton.setVisibility(View.INVISIBLE); // Hidden by default


        if (alarm.type >= 7 && alarm.type < 14) {
            // This is the depart alarm
            alarmMessage.setText(R.string.alarmDepart);
            directionsButton.setVisibility(View.VISIBLE);
        }


//        this.setTitle(alarm.getAlarmName());

        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
                // Google Maps Launch Turn-By-Turn Navigation:
                // https://developers.google.com/maps/documentation/android-api/intents
                // TODO: Pass commute destination address instead of exampleLocation
                String exampleLocation = destination;
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(exampleLocation));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
            }
        });
        startAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmActive = true;
    }

    private void startAlarm() {

        if (true) { // Probably want to allow them to turn off vibrate??
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 1000, 200, 200, 200 };
            vibrator.vibrate(pattern, 0);
        }

//        if (alarm.getAlarmTonePath() != "") {
//            mediaPlayer = new MediaPlayer();
//            if (true) { // Probably want to allow them to turn off vibrate??
//                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//                long[] pattern = { 1000, 200, 200, 200 };
//                vibrator.vibrate(pattern, 0);
//            }
//            try {
//                mediaPlayer.setVolume(1.0f, 1.0f);
//                mediaPlayer.setDataSource(this, Uri.parse(alarm.getAlarmTonePath()));
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
//                mediaPlayer.setLooping(true);
//                mediaPlayer.prepare();
//                mediaPlayer.start();
//
//            } catch (Exception e) {
//                mediaPlayer.release();
//                alarmActive = false;
//            }
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        StaticWakeLock.lockOff(this);
    }

    @Override
    protected void onDestroy() {
        try {
            if (vibrator != null)
                vibrator.cancel();
        } catch (Exception e) {

        }
        try {
//            mediaPlayer.stop();
        } catch (Exception e) {

        }
        try {
//            mediaPlayer.release();
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    public void stopAlarm() {
//        alarmActive = false;
        if (vibrator != null)
            vibrator.cancel();
        try {
//            mediaPlayer.stop();
        } catch (IllegalStateException ise) {

        }
        try {
//            mediaPlayer.release();
        } catch (Exception e) {

        }
        this.finish();

    }

    public String semanticTime(Alarm a) {
        String time = "";
        String meridiam = "";
        int hold = a.getAlarmTime().get(Calendar.HOUR_OF_DAY);
        if(hold > 12){
            meridiam = "PM";
            time += Integer.toString(hold-12);
        } else {
            meridiam = "AM";
            time += Integer.toString(hold);
        }
        time += ":";
        hold = a.getAlarmTime().get(Calendar.MINUTE);
        if(hold < 10){
            time += Integer.toString(0);
        }
        time += Integer.toString(hold);
        time += " " + meridiam;
        return time;
    }
}
