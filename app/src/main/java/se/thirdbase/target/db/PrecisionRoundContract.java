package se.thirdbase.target.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

/**
 * Created by alexp on 2/15/16.
 */
public final class PrecisionRoundContract {

    public static final String TABLE_NAME = "precision_round";

    public static final class PrecisionRoundEntry implements BaseColumns {
        public static final String COLUMN_NAME_DATE_TIME = "date_time";
        public static final String COLUMN_NAME_SERIES_1 = "series1";
        public static final String COLUMN_NAME_SERIES_2 = "series2";
        public static final String COLUMN_NAME_SERIES_3 = "series3";
        public static final String COLUMN_NAME_SERIES_4 = "series4";
        public static final String COLUMN_NAME_SERIES_5 = "series5";
        public static final String COLUMN_NAME_SERIES_6 = "series6";
        public static final String COLUMN_NAME_SERIES_7 = "series7";
        public static final String COLUMN_NAME_SCORE = "score";
        public static final String COLUMN_NAME_NOTES = "notes";
    }

    public static final String SQL_CREATE_PRECISION =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "%s DATETIME DEFAULT CURRENT_TIMESTAMP," +
                            "%s INTEGER," + // SERIES_1
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," + // SERIES_7
                            "%s INTEGER," + // SCORE
                            "%s TEXT);",
                    TABLE_NAME, PrecisionRoundEntry._ID,
                    PrecisionRoundEntry.COLUMN_NAME_DATE_TIME,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_1,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_2,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_3,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_4,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_5,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_6,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_7,
                    PrecisionRoundEntry.COLUMN_NAME_SCORE,
                    PrecisionRoundEntry.COLUMN_NAME_NOTES);

    public static final String SQL_DROP_PRECISION = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);

    public static PrecisionRound retrievePrecisionRound(SQLiteDatabase db, int id) {
        String[] columns = {
            PrecisionRoundEntry.COLUMN_NAME_SERIES_1,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_2,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_3,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_4,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_5,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_6,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_7,
            PrecisionRoundEntry.COLUMN_NAME_NOTES
        };

        Cursor cursor = db.query(
                PrecisionSeriesContract.TABLE_NAME,
                columns,
                PrecisionSeriesContract.PrecisionSeriesEntry._ID + "=?",
                new String[]{"" + id},
                null,  // groupBy
                null,  // having
                null,  // orderBy
                null); // limit

        List<PrecisionSeries> precisionSeries = new ArrayList<>();

        // -1 since the last columns is the notes
        for (int i = 0; i < columns.length; i++) {

            int seriesId = cursor.getInt(cursor.getColumnIndex(columns[i]));
            PrecisionSeries series = PrecisionSeriesContract.retrievePrecisionSeries(db, seriesId);
            precisionSeries.add(series);
        }

        String notes = cursor.getString(cursor.getColumnIndex(PrecisionRoundEntry.COLUMN_NAME_NOTES));

        return new PrecisionRound(precisionSeries, notes);
    }

    public static long storePrecisionRound(SQLiteDatabase db, PrecisionRound precisionRound) {
        List<PrecisionSeries> precisionSeries = precisionRound.getPrecisionSeries();
        List<Long> ids = new ArrayList<>();

        for (PrecisionSeries series : precisionSeries) {
            long id = PrecisionSeriesContract.storePrecisionSeries(db, series);
            ids.add(id);
        }

        ContentValues values = new ContentValues();
        String[] columns = {
                PrecisionRoundEntry.COLUMN_NAME_SERIES_1,
                PrecisionRoundEntry.COLUMN_NAME_SERIES_2,
                PrecisionRoundEntry.COLUMN_NAME_SERIES_3,
                PrecisionRoundEntry.COLUMN_NAME_SERIES_4,
                PrecisionRoundEntry.COLUMN_NAME_SERIES_5,
                PrecisionRoundEntry.COLUMN_NAME_SERIES_6,
                PrecisionRoundEntry.COLUMN_NAME_SERIES_7,
        };

        int size = precisionSeries.size();
        for (int i = 0; i < size; i++) {
            values.put(columns[i], ids.get(i));
        }

        values.put(PrecisionRoundEntry.COLUMN_NAME_SCORE, precisionRound.getScore());
        values.put(PrecisionRoundEntry.COLUMN_NAME_NOTES, precisionRound.getNotes());

        return db.insert(PrecisionRoundContract.TABLE_NAME, null, values);
    }
}
