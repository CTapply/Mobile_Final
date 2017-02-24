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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.example.jeffrey.finalprototype.R;

import java.util.Calendar;


public class AlarmAlertActivity extends Activity {

    private Alarm alarm;
//    private MediaPlayer mediaPlayer;


    private Vibrator vibrator;

    private boolean alarmActive;

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

//        this.setTitle(alarm.getAlarmName());

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

    public void stopAlarm(View v) {
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
}
