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
 * Created by alexp on 2/18/16.
 */
public final class PrecisionSeriesContract {

    private static final String TAG = PrecisionSeriesContract.class.getSimpleName();

    public static final String TABLE_NAME = "precision_series";

    public interface PrecisionSeriesEntry extends  BaseColumns {
        String COLUMN_NAME_DATE_TIME = "date_time";
        String COLUMN_NAME_BULLET_1 = "bullet1";
        String COLUMN_NAME_BULLET_2 = "bullet2";
        String COLUMN_NAME_BULLET_3 = "bullet3";
        String COLUMN_NAME_BULLET_4 = "bullet4";
        String COLUMN_NAME_BULLET_5 = "bullet5";
        String COLUMN_NAME_SCORE = "score";
    }

    public static final String SQL_CREATE_SERIES =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," + // BULLET_1
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);",
                    TABLE_NAME,
                    PrecisionSeriesEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_1,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_2,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_3,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_4,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_5,
                    PrecisionSeriesEntry.COLUMN_NAME_SCORE,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_1, BulletHoleContract.TABLE_NAME, BulletHoleContract.BulletHoleEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_2, BulletHoleContract.TABLE_NAME, BulletHoleContract.BulletHoleEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_3, BulletHoleContract.TABLE_NAME, BulletHoleContract.BulletHoleEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_4, BulletHoleContract.TABLE_NAME, BulletHoleContract.BulletHoleEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_5, BulletHoleContract.TABLE_NAME, BulletHoleContract.BulletHoleEntry._ID);

    public static final String SQL_DROP_SERIES = String.format("DROP TABLE IF EXISTS %s", PrecisionSeriesContract.TABLE_NAME);

    public static PrecisionSeries retrievePrecisionSeries(SQLiteDatabase db, int id) {
        String[] columns = {
                PrecisionSeriesEntry.COLUMN_NAME_BULLET_1,
                PrecisionSeriesEntry.COLUMN_NAME_BULLET_2,
                PrecisionSeriesEntry.COLUMN_NAME_BULLET_3,
                PrecisionSeriesEntry.COLUMN_NAME_BULLET_4,
                PrecisionSeriesEntry.COLUMN_NAME_BULLET_5,
                PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME,
                PrecisionSeriesEntry.COLUMN_NAME_SCORE
        };

        Cursor cursor = db.query(
                PrecisionSeriesContract.TABLE_NAME,
                columns,
                PrecisionSeriesEntry._ID + "=?",
                new String[]{"" + id},
                null,  // groupBy
                null,  // having
                null,  // orderBy
                null); // limit

        Log.d(TAG, String.format("Columns: %d, Rows: %d", cursor.getColumnCount(), cursor.getCount()));

        List<BulletHole> bulletHoles = new ArrayList<>();

        long timestamp = 0;
        if (cursor != null && cursor.moveToFirst()) {
            try {
                for (int i = 0; i < columns.length - 2; i++) {

                    int bulletHoleId = cursor.getInt(cursor.getColumnIndex(columns[i]));
                    BulletHole bulletHole = BulletHoleContract.retrieveBulletHole(db, bulletHoleId);
                    bulletHoles.add(bulletHole);
                }

                timestamp = cursor.getLong(cursor.getColumnIndex(PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME));
            } finally {
                cursor.close();
            }
        }

        return new PrecisionSeries(bulletHoles, timestamp);
    }

    public static List<PrecisionSeries> retrieveAllPrecisionSeries(SQLiteDatabase db, String orderBy) {
        String[] columns = { PrecisionSeriesEntry._ID };

        Cursor cursor = db.query(PrecisionSeriesContract.TABLE_NAME,
                columns,
                null, // selection
                null, // selectionArgs
                null, // groupBy
                null, // having
                orderBy);

        List<PrecisionSeries> precisionSeries = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {

            try {
                while (!cursor.isAfterLast()) {
                    int seriesId = cursor.getInt(cursor.getColumnIndex(PrecisionSeriesEntry._ID));
                    PrecisionSeries series = retrievePrecisionSeries(db, seriesId);

                    precisionSeries.add(series);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }

        return precisionSeries;
    }

    public static long storePrecisionSeries(SQLiteDatabase db, PrecisionSeries precisionSeries) {
        List<BulletHole> bulletHoles = precisionSeries.getBulletHoles();
        List<Long> ids = new ArrayList<>();

        for (BulletHole bulletHole : bulletHoles) {
            long id = bulletHole.store(db);
            ids.add(id);
        }

        ContentValues values = new ContentValues();
        String[] columns = {
                PrecisionSeriesEntry.COLUMN_NAME_BULLET_1,
                PrecisionSeriesEntry.COLUMN_NAME_BULLET_2,
                PrecisionSeriesEntry.COLUMN_NAME_BULLET_3,
                PrecisionSeriesEntry.COLUMN_NAME_BULLET_4,
                PrecisionSeriesEntry.COLUMN_NAME_BULLET_5
        };

        int size = bulletHoles.size();
        for (int i = 0; i < size; i++) {
            values.put(columns[i], ids.get(i));
        }

        values.put(PrecisionSeriesEntry.COLUMN_NAME_SCORE, precisionSeries.getScore());
        values.put(PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME, System.currentTimeMillis());

        return db.insert(PrecisionSeriesContract.TABLE_NAME, null, values);
    }

    public static void updatePrecisionSeries(SQLiteDatabase db, ContentValues values, long id) {
        db.update(PrecisionSeriesContract.TABLE_NAME, values, PrecisionSeriesEntry._ID, new String[]{"" + id});
    }
}
