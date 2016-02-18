package se.thirdbase.target.db;

import android.provider.BaseColumns;

/**
 * Created by alexp on 2/15/16.
 */
public final class PrecisionContract {

    public static final String TABLE_NAME = "precision_round";

    public static final class PrecisionEntry implements BaseColumns {
        public static final String COLUMN_NAME_DATE_TIME = "date_time";
        public static final String COLUMN_NAME_SERIES_1 = "series1";
        public static final String COLUMN_NAME_SERIES_2 = "series2";
        public static final String COLUMN_NAME_SERIES_3 = "series3";
        public static final String COLUMN_NAME_SERIES_4 = "series4";
        public static final String COLUMN_NAME_SERIES_5 = "series5";
        public static final String COLUMN_NAME_SERIES_6 = "series6";
        public static final String COLUMN_NAME_SERIES_7 = "series7";
        public static final String COLUMN_NAME_SERIES_8 = "series8";
        public static final String COLUMN_NAME_SERIES_9 = "series9";
        public static final String COLUMN_NAME_SCORE = "score";
    }

    public static final String SQL_CREATE_PRECISION =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "%s DATETIME," +
                            "%s INTEGER," + // SERIES_1
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," + // SERIES_9
                            "%s INTEGER);", // SCORE
                    TABLE_NAME, PrecisionEntry._ID,
                    PrecisionEntry.COLUMN_NAME_DATE_TIME,
                    PrecisionEntry.COLUMN_NAME_SERIES_1,
                    PrecisionEntry.COLUMN_NAME_SERIES_2,
                    PrecisionEntry.COLUMN_NAME_SERIES_3,
                    PrecisionEntry.COLUMN_NAME_SERIES_4,
                    PrecisionEntry.COLUMN_NAME_SERIES_5,
                    PrecisionEntry.COLUMN_NAME_SERIES_6,
                    PrecisionEntry.COLUMN_NAME_SERIES_7,
                    PrecisionEntry.COLUMN_NAME_SERIES_8,
                    PrecisionEntry.COLUMN_NAME_SERIES_9,
                    PrecisionEntry.COLUMN_NAME_SCORE);

    public static final String SQL_DROP_PRECISION = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
}
