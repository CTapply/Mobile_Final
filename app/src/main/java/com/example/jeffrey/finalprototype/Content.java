package com.example.jeffrey.finalprototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Content {

    /**
     * An array of sample (Commute) items.
     */
    public static final List<Commute> ITEMS = new ArrayList<Commute>();

    /**
     * A map of commutes, by ID.
     */
    public static final Map<String, Commute> COMMUTE_MAP = new HashMap<String, Commute>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
        addItem(createDummyItem(1));
    }

    public static void addItem(Commute item) {
        ITEMS.add(item);
        COMMUTE_MAP.put(item.id, item);
    }

    private static Commute createDummyItem(int position) {
        return new Commute(String.valueOf(position), "Location " + position,
                ThreadLocalRandom.current().nextInt(1, 12 + 1), // Random hour
                ThreadLocalRandom.current().nextInt(0, 59 + 1), // Random minute
                ThreadLocalRandom.current().nextInt(0, 15 + 1), // Random prep time
                new WeeklyInfo()); // Empty week info
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public static class WeeklyInfo {

        public boolean[] days;
        public boolean repeat;

        public WeeklyInfo(){
            this.days = new boolean[7];
            for(boolean day : this.days){
                day = false;
            }
            this.repeat = false;
        }

        public WeeklyInfo(boolean[] days, boolean repeat){
            if(days.length == 7)
                this.days = days;
            this.repeat = repeat;
        }

        public void setDayOn(int day){
            this.days[day] = true;
        }
        public void setDayOff(int day){
            this.days[day] = false;
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
    public static class Commute {
        public final String id;
        public String destination;
        public int arrivalTimeHour;
        public int arrivalTimeMin;
        public String timeMode;
        public int preparationTime;
        public WeeklyInfo weekInfo;

        public Commute(String id, String destination, int arrivalTimeHour,
                       int arrivalTimeMin, int preparationTime, WeeklyInfo weekInfo) {
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
            System.out.println(weekInfo.toString());
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
