package se.thirdbase.target.db;

import android.provider.BaseColumns;

/**
 * Created by alexp on 2/18/16.
 */
public final class PrecisionSeriesContract {

    private static final String TAG = PrecisionSeriesContract.class.getSimpleName();

    public static final String TABLE_NAME = "precision_series";

    public interface PrecisionSeriesEntry extends  BaseColumns {
        String COLUMN_NAME_DATE_TIME  = "date_time";
        String COLUMN_NAME_BULLET_1   = "bullet1";
        String COLUMN_NAME_BULLET_2   = "bullet2";
        String COLUMN_NAME_BULLET_3   = "bullet3";
        String COLUMN_NAME_BULLET_4   = "bullet4";
        String COLUMN_NAME_BULLET_5   = "bullet5";
        String COLUMN_NAME_WEAPON     = "weapon";
        String COLUMN_NAME_AMMUNITION = "ammunition";
        String COLUMN_NAME_SCORE      = "score";
        String COLUMN_NAME_NOTES      = "notes";
    }

    public static final String SQL_CREATE_SERIES =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "%s INTEGER," + // DATE_TIME
                            "%s INTEGER," + // BULLET_1
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," +
                            "%s INTEGER," + // BULLET_5
                            "%s INTEGER," + // WEAPON
                            "%s INTEGER," + // AMMUNITION
                            "%s INTEGER," + // SCORE
                            "%s TExT,"    + // NOTES
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," + // BULLET_1
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," +
                            "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE," + // BULLET_5
                            "FOREIGN KEY(%s) REFERENCES %s(%s)," +                   // WEAPON
                            "FOREIGN KEY(%s) REFERENCES %s(%s));",                   // AMMUNITION
                    TABLE_NAME,
                    PrecisionSeriesEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_1,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_2,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_3,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_4,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_5,
                    PrecisionSeriesEntry.COLUMN_NAME_WEAPON,
                    PrecisionSeriesEntry.COLUMN_NAME_AMMUNITION,
                    PrecisionSeriesEntry.COLUMN_NAME_SCORE,
                    PrecisionSeriesEntry.COLUMN_NAME_NOTES,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_1, BulletHoleContract.TABLE_NAME, BulletHoleContract.BulletHoleEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_2, BulletHoleContract.TABLE_NAME, BulletHoleContract.BulletHoleEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_3, BulletHoleContract.TABLE_NAME, BulletHoleContract.BulletHoleEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_4, BulletHoleContract.TABLE_NAME, BulletHoleContract.BulletHoleEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_BULLET_5, BulletHoleContract.TABLE_NAME, BulletHoleContract.BulletHoleEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_WEAPON, WeaponContract.TABLE_NAME, WeaponContract.WeaponEntry._ID,
                    PrecisionSeriesEntry.COLUMN_NAME_AMMUNITION, AmmunitionContract.TABLE_NAME, AmmunitionContract.AmmunitionEntry._ID);

    public static final String SQL_DROP_SERIES = String.format("DROP TABLE IF EXISTS %s;", PrecisionSeriesContract.TABLE_NAME);
}
