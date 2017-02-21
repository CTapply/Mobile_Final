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
        }
    }
}
