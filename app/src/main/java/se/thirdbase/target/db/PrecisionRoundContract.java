package se.thirdbase.target.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;
import se.thirdbase.target.util.SQLUtil;

/**
 * Created by alexp on 2/15/16.
 */
public final class PrecisionRoundContract {

    private static final String TAG = PrecisionRoundContract.class.getSimpleName();

    public static final String TABLE_NAME = "precision_round";

    public interface PrecisionRoundEntry extends BaseColumns {
        String COLUMN_NAME_DATE_TIME = "date_time";
        String COLUMN_NAME_SERIES_1 = "series1";
        String COLUMN_NAME_SERIES_2 = "series2";
        String COLUMN_NAME_SERIES_3 = "series3";
        String COLUMN_NAME_SERIES_4 = "series4";
        String COLUMN_NAME_SERIES_5 = "series5";
        String COLUMN_NAME_SERIES_6 = "series6";
        String COLUMN_NAME_SERIES_7 = "series7";
        String COLUMN_NAME_SCORE = "score";
        String COLUMN_NAME_NOTES = "notes";
    }

    public static final String SQL_CREATE_PRECISION =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "%s DATETIME DEFAULT CURRENT_TIMESTAMP," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," + // SCORE
                            "%s TEXT," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," + // SERIES_1
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);", // SERIES_7
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
                    PrecisionRoundEntry.COLUMN_NAME_NOTES,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_1, PrecisionSeriesContract.TABLE_NAME, PrecisionSeriesContract.PrecisionSeriesEntry._ID,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_2, PrecisionSeriesContract.TABLE_NAME, PrecisionSeriesContract.PrecisionSeriesEntry._ID,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_3, PrecisionSeriesContract.TABLE_NAME, PrecisionSeriesContract.PrecisionSeriesEntry._ID,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_4, PrecisionSeriesContract.TABLE_NAME, PrecisionSeriesContract.PrecisionSeriesEntry._ID,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_5, PrecisionSeriesContract.TABLE_NAME, PrecisionSeriesContract.PrecisionSeriesEntry._ID,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_6, PrecisionSeriesContract.TABLE_NAME, PrecisionSeriesContract.PrecisionSeriesEntry._ID,
                    PrecisionRoundEntry.COLUMN_NAME_SERIES_7, PrecisionSeriesContract.TABLE_NAME, PrecisionSeriesContract.PrecisionSeriesEntry._ID);

    public static final String SQL_DROP_PRECISION = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);

    public static PrecisionRound retrievePrecisionRound(SQLiteDatabase db, int id) {
        Log.d(TAG, String.format("retrievePrecisionRound(%d)", id));

        String[] columns = {
            PrecisionRoundEntry.COLUMN_NAME_SERIES_1,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_2,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_3,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_4,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_5,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_6,
            PrecisionRoundEntry.COLUMN_NAME_SERIES_7,
            PrecisionRoundEntry.COLUMN_NAME_DATE_TIME,
            PrecisionRoundEntry.COLUMN_NAME_NOTES
        };

        Cursor cursor = db.query(
                PrecisionRoundContract.TABLE_NAME,
                columns,
                PrecisionRoundContract.PrecisionRoundEntry._ID + "= ?",
                new String[]{"" + id},
                null,  // groupBy
                null,  // having
                null,  // orderBy
                null); // limit

        Log.d(TAG, String.format("Columns: %d, Rows: %d", cursor.getColumnCount(), cursor.getCount()));

        List<PrecisionSeries> precisionSeries = new ArrayList<>();

        String notes = null;
        Calendar calendar = null;

        if (cursor != null && cursor.moveToFirst()) {
            try {
                for (int i = 0; i < columns.length - 2; i++) {
                    int seriesId = cursor.getInt(cursor.getColumnIndex(columns[i]));
                    PrecisionSeries series = PrecisionSeriesContract.retrievePrecisionSeries(db, seriesId);
                    precisionSeries.add(series);
                }

                String calendarStr = cursor.getString(cursor.getColumnIndex(PrecisionRoundEntry.COLUMN_NAME_DATE_TIME));
                calendar = SQLUtil.string2Calendar(calendarStr);

                notes = cursor.getString(cursor.getColumnIndex(PrecisionRoundEntry.COLUMN_NAME_NOTES));
            } finally {
                cursor.close();
            }
        }


        return new PrecisionRound(precisionSeries, notes, calendar);
    }

    public static List<PrecisionRound> retrieveAllPrecisionRounds(SQLiteDatabase db) {
        String[] columns = { PrecisionRoundEntry._ID };

        Cursor cursor = db.query(PrecisionRoundContract.TABLE_NAME, columns, null, null, null, null, null);

        List<PrecisionRound> precisionRounds = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {

            try {
                while (!cursor.isAfterLast()) {
                    int roundId = cursor.getInt(cursor.getColumnIndex(PrecisionRoundEntry._ID));
                    PrecisionRound round = PrecisionRoundContract.retrievePrecisionRound(db, roundId);

                    precisionRounds.add(round);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }

        return precisionRounds;
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
