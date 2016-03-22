package se.thirdbase.target.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by alexp on 2/26/16.
 */
public class WeaponContract {

    private static final String TAG = WeaponContract.class.getSimpleName();

    public static final String TABLE_NAME = "weapon";

    public interface WeaponEntry extends BaseColumns {
        String COLUMN_NAME_DATE_TIME = "date_time";
        String COLUMN_NAME_TYPE = "type"; //pistol, revolver, rifle...
        String COLUMN_NAME_MAKE_AND_MODEL = "make_and_model";
        String COLUMN_NAME_CALIBER = "caliber";
        String COLUMN_NAME_LAST_CLEANED = "last_cleaned";
        String COLUMN_NAME_BULLETS_FIRED = "bullets_fired";
        String COLUMN_NAME_REMOVED = "removed";
    }

    public static final String SQL_CREATE_WEAPON =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "%s INTEGER," + //DATE_TIME
                            "%s INTEGER," + // TYPE
                            "%s VARCHAR(128)," + // MAKE_AND_MODEL
                            "%s INTEGER," +
                            "%s INTEGER," + // LAST_CLEANED
                            "%s INTEGER," + // BULLETS_FIRED
                            "%s INTEGER);", // REMOVED
                    TABLE_NAME, WeaponEntry._ID,
                    WeaponEntry.COLUMN_NAME_DATE_TIME,
                    WeaponEntry.COLUMN_NAME_TYPE,
                    WeaponEntry.COLUMN_NAME_MAKE_AND_MODEL,
                    WeaponEntry.COLUMN_NAME_CALIBER,
                    WeaponEntry.COLUMN_NAME_LAST_CLEANED,
                    WeaponEntry.COLUMN_NAME_BULLETS_FIRED,
                    WeaponEntry.COLUMN_NAME_REMOVED);

    public static String SQL_DROP_WEAPON = String.format("DROP TABLE IF EXISTS %s;", TABLE_NAME);

    public static void retrieveWeapon(SQLiteDatabase db, int id) {
        Log.d(TAG, String.format("retrieveWeapon(%d", id));
    }
}
