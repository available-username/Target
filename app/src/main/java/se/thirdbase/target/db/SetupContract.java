package se.thirdbase.target.db;

import android.provider.BaseColumns;

/**
 * Created by alexp on 2/29/16.
 */
public class SetupContract {

    public static String TABLE_NAME = "setup";

    public interface SetupEntry extends BaseColumns {
        String COLUMN_NAME_DATE_TIME  = "date_time";
        String COLUMN_NAME_PRINCIPLE  = "principle";
        String COLUMN_NAME_WEAPON     = "weapon";
        String COLUMN_NAME_AMMUNITION = "ammunition";
    }

    public static final String SQL_CREATE_SETUP =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "%s INTEGER," + // DATE_TIME
            "%s INTEGER," + // PRINCIPLE
            "%s INTEGER," + // WEAPON
            "%s INTEGER," + // AMMUNITION
            "FOREIGN KEY(%s) REFERENCES %s(%s)," +
            "FOREIGN KEY(%s) REFERENCES %s(%s));",
            TABLE_NAME, SetupEntry._ID,
                    SetupEntry.COLUMN_NAME_DATE_TIME,
                    SetupEntry.COLUMN_NAME_PRINCIPLE,
                    SetupEntry.COLUMN_NAME_WEAPON,
                    SetupEntry.COLUMN_NAME_AMMUNITION,
                    SetupEntry.COLUMN_NAME_PRINCIPLE, WeaponContract.TABLE_NAME, WeaponContract.WeaponEntry._ID,
                    SetupEntry.COLUMN_NAME_AMMUNITION, AmmunitionContract.TABLE_NAME, AmmunitionContract.AmmunitionEntry._ID);

    public static final String SQL_DROP_SETUP = String.format("DROP TABLE IF EXISTS %s;", TABLE_NAME);
}
