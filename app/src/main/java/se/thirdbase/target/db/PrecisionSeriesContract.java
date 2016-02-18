package se.thirdbase.target.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.PrecisionSeries;

/**
 * Created by alexp on 2/18/16.
 */
public final class PrecisionSeriesContract {
    public static final String TABLE_NAME = "precision_series";

    public static final class PrecisionSeriesEntry implements  BaseColumns {
        public static final String COLUMN_NAME_BULLET_1 = "bullet1";
        public static final String COLUMN_NAME_BULLET_2 = "bullet2";
        public static final String COLUMN_NAME_BULLET_3 = "bullet3";
        public static final String COLUMN_NAME_BULLET_4 = "bullet4";
        public static final String COLUMN_NAME_BULLET_5 = "bullet5";
        public static final String COLUMN_NAME_SCORE = "score";
    }

    public static final String SQL_CREATE_SERIES =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "%s INTEGER," + // BULLET_1
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," + // BULLET_5
                            "%s INTEGER);",
                    TABLE_NAME,
                    PrecisionSeriesEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_1,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_2,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_3,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_4,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_5,
                    PrecisionSeriesEntry.COLUMN_NAME_SCORE);

    public static final String SQL_DROP_SERIES = String.format("DROP TABLE IF EXISTS %s", PrecisionSeriesContract.TABLE_NAME);

    public static PrecisionSeries retrievePrecisionSeries(SQLiteDatabase db, int id) {
        String[] columns = {
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_1,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_2,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_3,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_4,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_5,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_SCORE
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

        List<BulletHole> bulletHoles = new ArrayList<>();

        // -1 since the last columns is the score column
        for (int i = 0; i < columns.length - 1; i++) {

            int bulletHoleId = cursor.getInt(cursor.getColumnIndex(columns[i]));
            BulletHole bulletHole = BulletHoleContract.retrieveBulletHole(db, bulletHoleId);
            bulletHoles.add(bulletHole);
        }

        return new PrecisionSeries(bulletHoles);
    }

    public static long storePrecisionSeries(SQLiteDatabase db, PrecisionSeries precisionSeries) {
        List<BulletHole> bulletHoles = precisionSeries.getBulletHoles();
        List<Long> ids = new ArrayList<>();

        for (BulletHole bulletHole : bulletHoles) {
            long id = BulletHoleContract.storeBulletHole(db, bulletHole);
            ids.add(id);
        }

        ContentValues values = new ContentValues();
        String[] columns = {
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_1,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_2,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_3,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_4,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_5,
        };

        int size = bulletHoles.size();
        for (int i = 0; i < size; i++) {
            values.put(columns[i], ids.get(i));
        }

        values.put(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_SCORE, precisionSeries.getScore());

        return db.insert(PrecisionSeriesContract.TABLE_NAME, null, values);
    }
}
