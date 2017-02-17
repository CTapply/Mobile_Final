package com.example.jeffrey.finalprototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static void addItem(Commute item) {
        ITEMS.add(item);
        COMMUTE_MAP.put(item.id, item);
    }

    private static Commute createDummyItem(int position) {
        return new Commute(String.valueOf(position), "Location " + position,
                ThreadLocalRandom.current().nextInt(1, 12 + 1), // Random hour
                ThreadLocalRandom.current().nextInt(0, 59 + 1), // Random minute
                ThreadLocalRandom.current().nextInt(0, 15 + 1));// Random prep time
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
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

        public Commute(String id, String destination, int arrivalTimeHour,
                       int arrivalTimeMin, int preparationTime) {
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

        public String semanticPrepTime(){
            return Integer.toString(this.preparationTime) + " min";
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
