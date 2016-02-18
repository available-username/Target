package se.thirdbase.target.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import se.thirdbase.target.model.BulletCaliber;
import se.thirdbase.target.model.BulletHole;

/**
 * Created by alexp on 2/18/16.
 */
public class BulletHoleContract {

    public static final String TABLE_NAME = "bullet_hole";

    public static final class BulletHoleEntry implements BaseColumns {
        public static final String COLUMN_NAME_CALIBER = "caliber";
        public static final String COLUMN_NAME_RADIUS = "radius";
        public static final String COLUMN_NAME_ANGLE = "angle";
    }

    public static final String SQL_CREATE_BULLET_HOLE =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s REAL, %s REAL);",
                    TABLE_NAME,
                    BulletHoleEntry._ID,
                    BulletHoleEntry.COLUMN_NAME_CALIBER,
                    BulletHoleEntry.COLUMN_NAME_ANGLE,
                    BulletHoleEntry.COLUMN_NAME_RADIUS);

    public static final String SQL_DROP_BULLET_HOLE = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);

    public static BulletHole retrieveBulletHole(SQLiteDatabase db, long id) {
        String[] columns = {
                BulletHoleContract.BulletHoleEntry.COLUMN_NAME_CALIBER,
                BulletHoleContract.BulletHoleEntry.COLUMN_NAME_ANGLE,
                BulletHoleContract.BulletHoleEntry.COLUMN_NAME_RADIUS
        };

        Cursor c = db.query(BulletHoleContract.TABLE_NAME,
                columns,
                BulletHoleContract.BulletHoleEntry._ID + "=?",
                new String[] {"" + id},
                null,
                null,
                null,
                null);

        BulletCaliber caliber = BulletCaliber.values()[c.getInt(c.getColumnIndex(BulletHoleContract.BulletHoleEntry.COLUMN_NAME_CALIBER))];
        float angle = c.getFloat(c.getColumnIndex(BulletHoleContract.BulletHoleEntry.COLUMN_NAME_ANGLE));
        float radius = c.getFloat(c.getColumnIndex(BulletHoleContract.BulletHoleEntry.COLUMN_NAME_RADIUS));

        return new BulletHole(caliber, radius, angle);
    }

    public static long storeBulletHole(SQLiteDatabase db, BulletHole bulletHole) {
        ContentValues values = new ContentValues();

        values.put(BulletHoleContract.BulletHoleEntry.COLUMN_NAME_CALIBER, bulletHole.getCaliber().ordinal());
        values.put(BulletHoleContract.BulletHoleEntry.COLUMN_NAME_ANGLE, bulletHole.getAngle());
        values.put(BulletHoleContract.BulletHoleEntry.COLUMN_NAME_RADIUS, bulletHole.getRadius());

        return db.insert(BulletHoleContract.TABLE_NAME, null, values);
    }
}
