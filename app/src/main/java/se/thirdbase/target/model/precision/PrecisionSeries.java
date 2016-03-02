package se.thirdbase.target.model.precision;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.db.PrecisionSeriesContract;
import se.thirdbase.target.model.Ammunition;
import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.Weapon;

/**
 * Created by alexp on 2/17/16.
 */
public class PrecisionSeries implements Parcelable {

    private List<BulletHole> mBulletHoles;
    private int mScore;
    private long mTimestamp;
    private Weapon mWeapon;
    private Ammunition mAmmunition;
    private long mDBHandle = Long.MIN_VALUE;

    public PrecisionSeries() {
        mBulletHoles = new ArrayList<>();
    }

    public PrecisionSeries(Weapon weapon, Ammunition ammunition) {
        mWeapon = weapon;
        mAmmunition = ammunition;
        mBulletHoles = new ArrayList<>();
    }

    public PrecisionSeries(Weapon weapon, Ammunition ammunition, List<BulletHole> bulletHoles) {
        this(weapon, ammunition);
        mBulletHoles = bulletHoles;
        mScore = calculateScore(bulletHoles);
    }

    public PrecisionSeries(Weapon weapon, Ammunition ammunition, List<BulletHole> bulletHoles, long timestamp) {
        this(weapon, ammunition, bulletHoles);
        mTimestamp = timestamp;
    }

    protected PrecisionSeries(Parcel in) {
        mBulletHoles = in.createTypedArrayList(BulletHole.CREATOR);
        mScore = in.readInt();
        mTimestamp = in.readLong();
        mWeapon = in.readParcelable(Weapon.class.getClassLoader());
        mAmmunition = in.readParcelable(Ammunition.class.getClassLoader());
    }

    public static final Creator<PrecisionSeries> CREATOR = new Creator<PrecisionSeries>() {
        @Override
        public PrecisionSeries createFromParcel(Parcel in) {
            return new PrecisionSeries(in);
        }

        @Override
        public PrecisionSeries[] newArray(int size) {
            return new PrecisionSeries[size];
        }
    };

    public void addBulletHole(BulletHole bulletHole) {
        mBulletHoles.add(bulletHole);
        mScore += PrecisionTarget.getBulletScore(bulletHole);
    }

    public void setBulletHoles(List<BulletHole> bulletHoles) {
        mBulletHoles = bulletHoles;
        mScore = calculateScore(bulletHoles);
    }

    public List<BulletHole> getBulletHoles() {
        return mBulletHoles;
    }

    private int calculateScore(List<BulletHole> bulletHoles) {
        int score = 0;

        for (BulletHole hole : bulletHoles) {
            score += PrecisionTarget.getBulletScore(hole);
        }

        return score;
    }


    public int getScore() {
        return mScore;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public Weapon getWeapon() {
        return mWeapon;
    }

    public Ammunition getAmmunition() {
        return mAmmunition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mBulletHoles);
        dest.writeInt(mScore);
        dest.writeLong(mTimestamp);
        dest.writeParcelable(mWeapon, flags);
        dest.writeParcelable(mAmmunition, flags);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (BulletHole hole : mBulletHoles) {
            builder.append(String.format(" %d", PrecisionTarget.getBulletScore(hole)));
        }

        return String.format("PrecisionSeries(timestamp=%d, score=%d,%s)", mTimestamp, mScore, builder.toString());
    }

    /*** Database handling ***/

    public long getDBHandle() {
        return mDBHandle;
    }

    public long store(SQLiteDatabase db) {
        if (mDBHandle == Long.MIN_VALUE) {
            List<Long> ids = new ArrayList<>();

            for (BulletHole bulletHole : mBulletHoles) {
                long id = bulletHole.store(db);
                ids.add(id);
            }

            ContentValues values = new ContentValues();
            String[] columns = {
                    PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_1,
                    PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_2,
                    PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_3,
                    PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_4,
                    PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_5,
                    PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_WEAPON,
                    PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_AMMUNITION
            };

            int size = mBulletHoles.size();
            for (int i = 0; i < size; i++) {
                values.put(columns[i], ids.get(i));
            }

            values.put(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_SCORE, getScore());
            values.put(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME, System.currentTimeMillis());
            values.put(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_WEAPON, mWeapon.getDBHandle());
            values.put(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_AMMUNITION, mAmmunition.getDBHandle());

            mDBHandle = db.insert(PrecisionSeriesContract.TABLE_NAME, null, values);
        } else {
            update(db);
        }

        return mDBHandle;
    }

    public void update(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_SCORE, getScore());
        values.put(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME, System.currentTimeMillis());

        db.update(PrecisionSeriesContract.TABLE_NAME, values, PrecisionSeriesContract.PrecisionSeriesEntry._ID + "=?", new String[]{"" + mDBHandle});
    }

    public static PrecisionSeries fetch(SQLiteDatabase db, long id) {
        String selection = PrecisionSeriesContract.PrecisionSeriesEntry._ID + "=?";
        String[] args = new String[]{"" + id};
        List<PrecisionSeries> precisionSeries = PrecisionSeries.fetchSelection(db, selection, args, null, null, null, null);

        return precisionSeries.size() == 1 ? precisionSeries.get(0) : null;
    }

    public static List<PrecisionSeries> fetchAll(SQLiteDatabase db, String orderBy) {
        return fetchSelection(db, null, null, null, null, orderBy, null);
    }

    public static List<PrecisionSeries> fetchSelection(SQLiteDatabase db, String selection, String[] selectionArgs,
                                                       String groupBy, String having, String orderBy, String limit) {
        String[] columns = {
                PrecisionSeriesContract.PrecisionSeriesEntry._ID,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_1,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_2,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_3,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_4,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_5,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_WEAPON,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_AMMUNITION,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_SCORE
        };

        Cursor cursor = db.query(PrecisionSeriesContract.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy,
                limit);

        List<PrecisionSeries> precisionSeries = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {

            try {
                while (!cursor.isAfterLast()) {
                    PrecisionSeries series = PrecisionSeries.fromCursor(db, cursor);
                    precisionSeries.add(series);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }

        return precisionSeries;
    }

    private static PrecisionSeries fromCursor(SQLiteDatabase db, Cursor cursor) {
        List<BulletHole> bulletHoles = new ArrayList<>();

        long id = cursor.getLong(cursor.getColumnIndex(PrecisionSeriesContract.PrecisionSeriesEntry._ID));

        long bulletHoleId = cursor.getLong(cursor.getColumnIndex(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_1));
        BulletHole bulletHole = BulletHole.fetch(db, bulletHoleId);
        bulletHoles.add(bulletHole);

        bulletHoleId = cursor.getInt(cursor.getColumnIndex(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_2));
        bulletHole = BulletHole.fetch(db, bulletHoleId);
        bulletHoles.add(bulletHole);

        bulletHoleId = cursor.getInt(cursor.getColumnIndex(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_3));
        bulletHole = BulletHole.fetch(db, bulletHoleId);
        bulletHoles.add(bulletHole);

        bulletHoleId = cursor.getInt(cursor.getColumnIndex(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_4));
        bulletHole = BulletHole.fetch(db, bulletHoleId);
        bulletHoles.add(bulletHole);

        bulletHoleId = cursor.getInt(cursor.getColumnIndex(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_5));
        bulletHole = BulletHole.fetch(db, bulletHoleId);
        bulletHoles.add(bulletHole);

        long timestamp = cursor.getLong(cursor.getColumnIndex(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME));

        Long weaponId = cursor.getLong(cursor.getColumnIndex(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_WEAPON));
        Weapon weapon = Weapon.fetch(db, weaponId);

        long ammunitionId = cursor.getLong(cursor.getColumnIndex(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_AMMUNITION));
        Ammunition ammunition = Ammunition.fetch(db, ammunitionId);

        PrecisionSeries precisionSeries = new PrecisionSeries(weapon, ammunition, bulletHoles, timestamp);
        precisionSeries.mDBHandle = id;

        return precisionSeries;
    }
}

