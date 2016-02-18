package se.thirdbase.target.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alexp on 2/17/16.
 */
public class PrecisionDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Precision.db";

    public PrecisionDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BulletHoleContract.SQL_CREATE_BULLET_HOLE);
        db.execSQL(PrecisionSeriesContract.SQL_CREATE_SERIES);
        db.execSQL(PrecisionContract.SQL_CREATE_PRECISION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PrecisionContract.SQL_DROP_PRECISION);
        db.execSQL(PrecisionSeriesContract.SQL_DROP_SERIES);
        db.execSQL(BulletHoleContract.SQL_DROP_BULLET_HOLE);
    }
}
