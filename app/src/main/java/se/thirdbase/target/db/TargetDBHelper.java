package se.thirdbase.target.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import se.thirdbase.target.model.Ammunition;

/**
 * Created by alexp on 2/17/16.
 */
public class TargetDBHelper extends SQLiteOpenHelper {

    private static final String TAG = TargetDBHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "precision.db";
    public static final int DATABASE_VERSION = 1;

    private static TargetDBHelper mInstance;

    public static TargetDBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TargetDBHelper(context);
        }

        return mInstance;
    }

    private TargetDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d(TAG, "TargetDBHelper()");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate()");
        db.beginTransaction();
        try {
            db.execSQL(BulletHoleContract.SQL_CREATE_BULLET_HOLE);
            db.execSQL(WeaponContract.SQL_CREATE_WEAPON);
            db.execSQL(AmmunitionContract.SQL_CREATE_AMMUNITION);
            db.execSQL(PrecisionSeriesContract.SQL_CREATE_SERIES);
            db.execSQL(PrecisionRoundContract.SQL_CREATE_PRECISION);
            db.execSQL(SetupContract.SQL_CREATE_SETUP);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.d(TAG, "onOpen()");

        super.onOpen(db);

        db.execSQL("PRAGMA foreign_keys = ON;");
        //db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade()");

        db.execSQL(PrecisionRoundContract.SQL_DROP_PRECISION);
        db.execSQL(PrecisionSeriesContract.SQL_DROP_SERIES);
        db.execSQL(BulletHoleContract.SQL_DROP_BULLET_HOLE);
    }
}
