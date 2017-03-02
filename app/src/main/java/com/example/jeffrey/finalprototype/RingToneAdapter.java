package com.example.jeffrey.finalprototype;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.jeffrey.finalprototype.AddNewCommute.alarmToneTimer;
import static com.example.jeffrey.finalprototype.AddNewCommute.mediaPlayer;
import static com.example.jeffrey.finalprototype.AddNewCommute.selectedPosition;
import static com.example.jeffrey.finalprototype.Content.Tones;

/**
 * Created by Cory on 3/1/17.
 */

public class RingToneAdapter extends ArrayAdapter<String> {
    public RingToneAdapter(Context context, ArrayList<String> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        String tone = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.ringtone_item, parent, false);
        }

        TextView toneName = (TextView) view.findViewById(R.id.toneName);
        // Set Background Colors to be swapping
        if (position % 2 == 1) {
            toneName.setBackgroundColor(Color.parseColor("#E1F5FE")); // Light
        } else {
            toneName.setBackgroundColor(Color.parseColor("#B3E5FC")); // Dark
        }




        toneName.setText(tone);
        toneName.setTextColor(Color.BLACK);

        toneName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Play the sound
                selectedPosition = position;

                if (Tones.alarmTonePaths[position] != null) {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                    } else {
                        if (mediaPlayer.isPlaying())
                            mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                    try {
                        // mediaPlayer.setVolume(1.0f, 1.0f);
                        mediaPlayer.setVolume(0.2f, 0.2f);
                        mediaPlayer.setDataSource(view.getContext(), Uri.parse(Tones.alarmTonePaths[position]));
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                        mediaPlayer.setLooping(false);
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        // Force the mediaPlayer to stop after 3
                        // seconds...
                        if (alarmToneTimer != null)
                            alarmToneTimer.cancel();
                        alarmToneTimer = new CountDownTimer(3000, 3000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                try {
                                    if (mediaPlayer.isPlaying())
                                        mediaPlayer.stop();
                                } catch (Exception e) {

                                }
                            }
                        };
                        alarmToneTimer.start();
                    } catch (Exception e) {
                        try {
                            if (mediaPlayer.isPlaying())
                                mediaPlayer.stop();
                        } catch (Exception e2) {

                        }
                    }
                }
            }
        });


        // Return the completed view to render on screen
        return view;
    }
}
