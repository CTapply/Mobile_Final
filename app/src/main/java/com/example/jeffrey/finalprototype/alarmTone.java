package com.example.jeffrey.finalprototype;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;

/**
 * Created by Cory on 3/1/17.
 */

public class AlarmTone {
    public String[] alarmTones;
    public String[] alarmTonePaths;

    public AlarmTone(Context c) {

        RingtoneManager ringtoneMgr = new RingtoneManager(c);

        ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);

        Cursor alarmsCursor = ringtoneMgr.getCursor();

        alarmTones = new String[alarmsCursor.getCount() + 1];
        alarmTones[0] = "Silent";
        alarmTonePaths = new String[alarmsCursor.getCount() + 1];
        alarmTonePaths[0] = "";

        if (alarmsCursor.moveToFirst())

        {
            do {
                alarmTones[alarmsCursor.getPosition() + 1] = ringtoneMgr.getRingtone(alarmsCursor.getPosition()).getTitle(c);
                alarmTonePaths[alarmsCursor.getPosition() + 1] = ringtoneMgr.getRingtoneUri(alarmsCursor.getPosition()).toString();
            } while (alarmsCursor.moveToNext());
        }

        alarmsCursor.close();
    }

}
