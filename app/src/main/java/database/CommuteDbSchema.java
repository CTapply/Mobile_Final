package database;

/**
 * Created by Jeffrey on 2/19/2017.
 */

public class CommuteDbSchema {
    public static final class CommuteTable {
        public static final String NAME = "commutes";

        public static final class Cols {
            public static final String ID = "id";
            public static final String ARR_HOUR = "arr_hour";
            public static final String ARR_MIN = "arr_min";
            public static final String PREP_MINS = "prep_mins";
            public static final String DESTINATION = "destination";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String REPEAT = "repeat";
            public static final String SUNDAY = "sunday";
            public static final String MONDAY = "monday";
            public static final String TUESDAY = "tuesday";
            public static final String WEDNESDAY = "wednesday";
            public static final String THURSDAY = "thursday";
            public static final String FRIDAY = "friday";
            public static final String SATURDAY = "saturday";
            public static final String ACTIVE = "active";
        }
    }
}
