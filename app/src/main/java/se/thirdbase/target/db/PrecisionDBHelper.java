package se.thirdbase.target.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import se.thirdbase.target.model.PrecisionSeries;

/**
 * Created by alexp on 2/17/16.
 */
public class PrecisionDBHelper extends SQLiteOpenHelper {

    private static final String TAG = PrecisionDBHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "Precision.db";
    public static final int DATABASE_VERSION = 1;

    private static PrecisionDBHelper mInstance;

    public static PrecisionDBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PrecisionDBHelper(context);
        }

        return mInstance;
    }

    private PrecisionDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d(TAG, "PrecisionDBHelper()");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate()");
        db.execSQL(BulletHoleContract.SQL_CREATE_BULLET_HOLE);
        db.execSQL(PrecisionSeriesContract.SQL_CREATE_SERIES);
        db.execSQL(PrecisionRoundContract.SQL_CREATE_PRECISION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.d(TAG, "onOpen()");
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade()");

        db.execSQL(PrecisionRoundContract.SQL_DROP_PRECISION);
        db.execSQL(PrecisionSeriesContract.SQL_DROP_SERIES);
        db.execSQL(BulletHoleContract.SQL_DROP_BULLET_HOLE);
    }
}
