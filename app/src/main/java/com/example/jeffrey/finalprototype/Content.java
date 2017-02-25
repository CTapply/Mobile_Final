package com.example.jeffrey.finalprototype;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import alarmManager.Alarm;
import database.CommuteBaseHelper;
import database.CommuteCursorWrapper;
import database.CommuteDbSchema;
import database.CommuteDbSchema.CommuteTable.Cols;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Content implements Serializable {

    /**
     * An array of sample (Commute) items.
     */
    public static List<Commute> ITEMS = new ArrayList<Commute>();

    /**
     * A map of commutes, by ID.
     */
    public static Map<String, Commute> COMMUTE_MAP = new HashMap<String, Commute>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
        //addItem(createDummyItem(1));
    }

    public static void addItem(Commute item, SQLiteDatabase db) {
        ITEMS.add(item);
        COMMUTE_MAP.put(item.id, item);

        ContentValues values = getContentValues(item);
        db.insert(CommuteDbSchema.CommuteTable.NAME, null, values);
    }

    private static CommuteCursorWrapper queryCommutes(String whereClause, String[] whereArgs){
        Cursor c = CommuteListActivity.mDatabase.query(
                CommuteDbSchema.CommuteTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CommuteCursorWrapper(c);
    }

    private static ContentValues getContentValues(Commute commute){
        ContentValues values = new ContentValues();
        values.put(Cols.ID, commute.id);
        values.put(Cols.ARR_HOUR, commute.arrivalTimeHour);
        values.put(Cols.ARR_MIN, commute.arrivalTimeMin);
        values.put(Cols.PREP_MINS, commute.preparationTime);
        values.put(Cols.DESTINATION, commute.destination);
        values.put(Cols.REPEAT, boolToInt(commute.weekInfo.repeat));
        values.put(Cols.SUNDAY, boolToInt(commute.weekInfo.days[0]));
        values.put(Cols.MONDAY, boolToInt(commute.weekInfo.days[1]));
        values.put(Cols.TUESDAY, boolToInt(commute.weekInfo.days[2]));
        values.put(Cols.WEDNESDAY, boolToInt(commute.weekInfo.days[3]));
        values.put(Cols.THURSDAY, boolToInt(commute.weekInfo.days[4]));
        values.put(Cols.FRIDAY, boolToInt(commute.weekInfo.days[5]));
        values.put(Cols.SATURDAY, boolToInt(commute.weekInfo.days[6]));
        values.put(Cols.ACTIVE, boolToInt(commute.active));
        return values;
    }

    public static int boolToInt(boolean b){
        if(b)
            return 1;
        else
            return 0;
    }

    public static void populate(Context c) {
        // Pull records from SQLite DB and populate ITEMS
        List<Commute> commuteList = new ArrayList<>();
        Map<String, Commute> commuteMap = new HashMap<String, Commute>();
        CommuteCursorWrapper cursor = queryCommutes(null, null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Commute com = cursor.getCommute(c);
                commuteList.add(com);
                commuteMap.put(com.id, com);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
            ITEMS = commuteList;
            COMMUTE_MAP = commuteMap;
        }
    }

//    private static Commute createDummyItem(int position) {
//        return new Commute(String.valueOf(position), "Location " + position,
//                ThreadLocalRandom.current().nextInt(1, 12 + 1), // Random hour
//                ThreadLocalRandom.current().nextInt(0, 59 + 1), // Random minute
//                ThreadLocalRandom.current().nextInt(0, 15 + 1), // Random prep time
//                new WeeklyInfo(), ); // Empty week info
//    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public static class WeeklyInfo implements Serializable {

        public boolean[] days;
        public boolean repeat;

        public WeeklyInfo(){
            this.days = new boolean[7];
            for(int i = 0; i < this.days.length; ++i){
                this.days[i] = false;
            }
            this.repeat = false;
        }

        public WeeklyInfo(boolean[] days, boolean repeat){
            if(days.length == 7)
                this.days = days;
            this.repeat = repeat;
        }

        public void setDayOn(int day){
            if(day >= 0 && day <= 6)
                this.days[day] = true;
            else
                System.out.println("SET DAY ON index out of range");
        }
        public void setDayOff(int day){
            if(day >= 0 && day <= 6)
                this.days[day] = false;
            else
                System.out.println("SET DAY OFF index out of range");
        }
        public void setRepeat(boolean val){
            this.repeat = val;
        }

        public String toString(){
            if(!this.repeat)
                return "None";
            boolean notFirst = false;
            String result = "Every ";
            if(days[0]) {
                result += "Sunday";
                notFirst = true;
            }
            if(days[1]){
                if(notFirst)
                    result += ", ";
                result += "Monday";
                notFirst = true;
            }
            if(days[2]){
                if(notFirst)
                    result += ", ";
                result += "Tuesday";
                notFirst = true;
            }
            if(days[3]){
                if(notFirst)
                    result += ", ";
                result += "Wednesday";
                notFirst = true;
            }
            if(days[4]){
                if(notFirst)
                    result += ", ";
                result += "Thursday";
                notFirst = true;
            }
            if(days[5]){
                if(notFirst)
                    result += ", ";
                result += "Friday";
                notFirst = true;
            }
            if(days[6]){
                if(notFirst)
                    result += ", ";
                result += "Saturday";
            }
            if(Objects.equals(result, "Every ")){
                return "None";
            } else {
                return result;
            }
        }
    }

    /**
     * A commute to set alarms for
     */
    public static class Commute implements Serializable {
        public final String id;
        public final int UUID;
        public String destination;
        public int arrivalTimeHour;
        public int arrivalTimeMin;
        public String timeMode;
        public int preparationTime;
        public WeeklyInfo weekInfo;
        public Alarm[] alarm = new Alarm[7];
        public Context context;
        public boolean active;

        public Commute(String id, String destination, int arrivalTimeHour,
                       int arrivalTimeMin, int preparationTime, WeeklyInfo weekInfo, boolean active, Context c) {
            this.id = id;
            this.destination = destination;
            this.arrivalTimeHour = arrivalTimeHour;
            this.arrivalTimeMin = arrivalTimeMin;
            this.preparationTime = preparationTime;
            if(arrivalTimeHour >= 12){
                this.timeMode = "PM";
            } else {
                this.timeMode = "AM";
            }
            this.weekInfo = weekInfo;
            this.UUID = 0;
            this.context = c;
            this.active = active;

            // Set an alarm for each day of the
            for (int i = 0; i < this.weekInfo.days.length; i++) {
                if (this.weekInfo.days[i] == true) {
                    this.alarm[i] = new Alarm(arrivalTimeHour, arrivalTimeMin, preparationTime, i);
                    this.alarm[i].setCommute(this);
                    this.alarm[i].setAlarmTime(c);
                }

            }

        }

        public Commute(String id, String destination, int arrivalTimeHour,
                       int arrivalTimeMin, int preparationTime, WeeklyInfo weekInfo,
                       int uuid, boolean active, Context c) {
            this.id = id;
            this.destination = destination;
            this.arrivalTimeHour = arrivalTimeHour;
            this.arrivalTimeMin = arrivalTimeMin;
            this.preparationTime = preparationTime;
            if(arrivalTimeHour >= 12){
                this.timeMode = "PM";
            } else {
                this.timeMode = "AM";
            }
            this.weekInfo = weekInfo;

            this.UUID = uuid;
            this.context = c;
            this.active = active;

            // Set an alarm for each day of the
            for (int i = 0; i < this.weekInfo.days.length; i++) {
                if (this.weekInfo.days[i] == true) {
                    this.alarm[i] = new Alarm(arrivalTimeHour, arrivalTimeMin, preparationTime, i);
                    this.alarm[i].setCommute(this);
                    this.alarm[i].setAlarmTime(c);
                }

            }
        }

        /**
         * Gets the alarm that is nearest in the future for this commute
         * @return
         */
        public Alarm getNextAlarm() {
            Alarm next = new Alarm();
            for (Alarm a : alarm) {
                if (a != null) {
                    next = a;
                    break;
                }
            }
            for (Alarm a : alarm) {
                if (a != null) {
                    if (next.getAlarmTime().getTimeInMillis() > a.getAlarmTime().getTimeInMillis()) {
                        next = a;
                    }


                }
            }
            return next;
        }

        public void setDestination(String dest){
            this.destination = dest;
        }
        public void setArrTimeHour(int hour){
            this.arrivalTimeHour = hour;
        }
        public void setArrTimeMin(int min){
            this.arrivalTimeMin = min;
        }
        public void switchTimeMode(){
            if(this.timeMode == "PM"){
                this.timeMode = "AM";
            } else {
                this.timeMode = "PM";
            }
        }
        public void setPrepTime(int time){
            this.preparationTime = time;
        }

        @Override
        public String toString() {
            return destination;
        }

        /**
         * Gets the number of minutes as an integer in preparationTime
         * @return Prep time in # hours $ minutes
         */
        public String semanticPrepTime(){
            int minutes = this.preparationTime;
            int hours = 0;
            if (minutes >= 60) {
                hours = (int) Math.floor(minutes/60);
                minutes = minutes % 60;
                if (hours > 1) {
                    if (minutes == 0) {
                        return hours + " hours";
                    } else if (minutes == 1) {
                        return hours + " hours " + minutes + " minute";
                    } else {
                        return hours + " hours " + minutes + " minutes";
                    }
                } else if (hours == 1) {
                    if (minutes == 0) {
                        return hours + " hour";
                    } else if (minutes == 1) {
                        return hours + " hour " + minutes + " minute";
                    } else {
                        return hours + " hour " + minutes + " minutes";
                    }
                } else { // 0 hours
                    if (minutes == 1) {
                        return minutes + " minute";
                    } else {
                        return minutes + " minutes";
                    }
                }
            } else {
                // Minutes is < 60 so we are good
                if (minutes == 1) {
                    return minutes + " minute";
                } else {
                    return minutes + " minutes";
                }
            }
        }

        public String semanticTime(){
            String time = "";
            int hold = this.arrivalTimeHour;
            if(hold > 12){
                time += Integer.toString(hold-12);
            } else {
                time += Integer.toString(hold);
            }
            time += ":";
            hold = this.arrivalTimeMin;
            if(hold < 10){
                time += Integer.toString(0);
            }
            time += Integer.toString(hold);
            time += " " + this.timeMode;
            return time;
        }
    }
}
