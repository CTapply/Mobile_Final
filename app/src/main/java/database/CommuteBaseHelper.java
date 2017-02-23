package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import database.CommuteDbSchema.CommuteTable;

/**
 * Created by Jeffrey on 2/19/2017.
 */

public class CommuteBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 2;
    private static final String DATABASE_NAME  = "commuteBaseThree.db";

    public CommuteBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + CommuteTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CommuteTable.Cols.ID + ", " +
                CommuteTable.Cols.ARR_HOUR + ", " +
                CommuteTable.Cols.ARR_MIN + ", "  +
                CommuteTable.Cols.PREP_MINS + ", " +
                CommuteTable.Cols.DESTINATION + ", " +
                CommuteTable.Cols.REPEAT + ", " +
                CommuteTable.Cols.SUNDAY + ", " +
                CommuteTable.Cols.MONDAY + ", " +
                CommuteTable.Cols.TUESDAY + ", " +
                CommuteTable.Cols.WEDNESDAY + ", " +
                CommuteTable.Cols.THURSDAY + ", " +
                CommuteTable.Cols.FRIDAY + ", " +
                CommuteTable.Cols.SATURDAY + ", " +
                CommuteTable.Cols.ARMED +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
