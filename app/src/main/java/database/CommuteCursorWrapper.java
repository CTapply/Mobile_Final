package database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.jeffrey.finalprototype.CommuteListActivity;
import com.example.jeffrey.finalprototype.Content;
import com.example.jeffrey.finalprototype.Content.Commute;

import alarmManager.Alarm;
import database.CommuteDbSchema.CommuteTable;

/**
 * Created by Jeffrey on 2/22/2017.
 */

public class CommuteCursorWrapper extends CursorWrapper {
    public CommuteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Commute getCommute(Context context){
        int uuid = getInt(getColumnIndex("_id"));
        String id = getString(getColumnIndex(CommuteTable.Cols.ID));
        int arr_hour = getInt(getColumnIndex(CommuteTable.Cols.ARR_HOUR));
        int arr_min = getInt(getColumnIndex(CommuteTable.Cols.ARR_MIN));
        int prep_mins = getInt(getColumnIndex(CommuteTable.Cols.PREP_MINS));
        String destination = getString(getColumnIndex(CommuteTable.Cols.DESTINATION));
        double latitude = getDouble(getColumnIndex(CommuteTable.Cols.LATITUDE));
        double longitude = getDouble(getColumnIndex(CommuteTable.Cols.LONGITUDE));
        boolean repeat = getInt(getColumnIndex(CommuteTable.Cols.REPEAT)) > 0;
        boolean sunday = getInt(getColumnIndex(CommuteTable.Cols.SUNDAY)) > 0;
        boolean monday = getInt(getColumnIndex(CommuteTable.Cols.MONDAY)) > 0;
        boolean tuesday = getInt(getColumnIndex(CommuteTable.Cols.TUESDAY)) > 0;
        boolean wednesday = getInt(getColumnIndex(CommuteTable.Cols.WEDNESDAY)) > 0;
        boolean thursday = getInt(getColumnIndex(CommuteTable.Cols.THURSDAY)) > 0;
        boolean friday = getInt(getColumnIndex(CommuteTable.Cols.FRIDAY)) > 0;
        boolean saturday = getInt(getColumnIndex(CommuteTable.Cols.SATURDAY)) > 0;
        boolean active = getInt(getColumnIndex(CommuteTable.Cols.ACTIVE)) > 0;

        Content.WeeklyInfo w = CommuteListActivity.makeWeek(
                sunday, monday, tuesday, wednesday, thursday,
                friday, saturday, repeat
        );
        Commute c = new Commute(id, destination, latitude, longitude, arr_hour, arr_min, prep_mins, w, uuid, active, context);

        return c;
    }
}
