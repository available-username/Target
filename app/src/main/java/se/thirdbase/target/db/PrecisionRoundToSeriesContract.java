package se.thirdbase.target.db;

import android.provider.BaseColumns;

/**
 * Created by alex on 4/5/16.
 */
public class PrecisionRoundToSeriesContract {

    private static final String TAG = PrecisionRoundToSeriesContract.class.getSimpleName();

    public static final String TABLE_NAME = "precision_round_and_series";

    public interface PrecisionRoundToSeriesEntry extends BaseColumns {
        String COLUMN_NAME_ROUND = "round";
        String COLUMN_NAME_SERIES = "series";
        String COLUMN_NAME_ORDERING = "ordering";
    }

    public static final String SQL_CREATE_ROUND_TO_SERIES =
            String.format("CREATE TABLE %s (" +
                    "%s INTEGER NOT NULL," +
                    "%s INTEGER NOT NULL," +
                    "%s INTEGER NOT NULL," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s)," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s));",
                    TABLE_NAME,
                    PrecisionRoundToSeriesEntry.COLUMN_NAME_ROUND,
                    PrecisionRoundToSeriesEntry.COLUMN_NAME_SERIES,
                    PrecisionRoundToSeriesEntry.COLUMN_NAME_ORDERING,
                    PrecisionRoundToSeriesEntry.COLUMN_NAME_ROUND, PrecisionRoundContract.TABLE_NAME, PrecisionRoundContract.PrecisionRoundEntry._ID,
                    PrecisionRoundToSeriesEntry.COLUMN_NAME_SERIES, PrecisionSeriesContract.TABLE_NAME, PrecisionSeriesContract.PrecisionSeriesEntry._ID);

    public static final String SQL_DROP_ROUND_TO_SERIES = String.format("DROP TABLE IF EXISTS %s;", PrecisionRoundToSeriesContract.TABLE_NAME);
}
