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

    public  interface BulletHoleEntry extends BaseColumns {
        String COLUMN_NAME_CALIBER = "caliber";
        String COLUMN_NAME_RADIUS = "radius";
        String COLUMN_NAME_ANGLE = "angle";
    }

    public static final String SQL_CREATE_BULLET_HOLE =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, %s INTEGER, %s REAL, %s REAL);",
                    TABLE_NAME,
                    BulletHoleEntry._ID,
                    BulletHoleEntry.COLUMN_NAME_CALIBER,
                    BulletHoleEntry.COLUMN_NAME_ANGLE,
                    BulletHoleEntry.COLUMN_NAME_RADIUS);

    public static final String SQL_DROP_BULLET_HOLE = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);

    public static BulletHole retrieveBulletHole(SQLiteDatabase db, long id) {
        String[] columns = {
                BulletHoleEntry.COLUMN_NAME_CALIBER,
                BulletHoleEntry.COLUMN_NAME_ANGLE,
                BulletHoleEntry.COLUMN_NAME_RADIUS
        };

        Cursor cursor = db.query(BulletHoleContract.TABLE_NAME,
                columns,
                BulletHoleEntry._ID + "=?",
                new String[] {"" + id},
                null,
                null,
                null,
                null);

        BulletHole hole = null;

        if (cursor != null && cursor.moveToFirst()) {
            try {
                BulletCaliber caliber = BulletCaliber.values()[cursor.getInt(cursor.getColumnIndex(BulletHoleEntry.COLUMN_NAME_CALIBER))];
                float angle = cursor.getFloat(cursor.getColumnIndex(BulletHoleEntry.COLUMN_NAME_ANGLE));
                float radius = cursor.getFloat(cursor.getColumnIndex(BulletHoleEntry.COLUMN_NAME_RADIUS));

                hole = new BulletHole(caliber, radius, angle);
            } finally {
                cursor.close();
            }
        }

        return hole;
    }
}
