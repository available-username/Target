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
        String COLUMN_NAME_MANUFACTURER = "manufacturer";
        String COLUMN_NAME_MODEL = "model";
        String COLUMN_NAME_CALIBER = "caliber";
    }

    public static final String SQL_CREATE_WEAPON =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                    "%s INTEGER" + //DATE_TIME
                    "%s VARCHAR(32)" + // TYPE
                    "%s VARCHAR(64)" + // MANUFACTURER
                    "%s VARCHAR(64)" + // MODEL
                    "%s VARCHAR(32);", // CALIBER
                    TABLE_NAME, WeaponEntry._ID,
                    WeaponEntry.COLUMN_NAME_DATE_TIME,
                    WeaponEntry.COLUMN_NAME_TYPE,
                    WeaponEntry.COLUMN_NAME_MANUFACTURER,
                    WeaponEntry.COLUMN_NAME_MODEL,
                    WeaponEntry.COLUMN_NAME_CALIBER);

    public static String SQL_DROP_WEAPON = String.format("DROP TABLE IF EXITS %s;", TABLE_NAME);

    public static void retrieveWeapon(SQLiteDatabase db, int id) {
        Log.d(TAG, String.format("retrieveWeapon(%d", id));
    }
}
