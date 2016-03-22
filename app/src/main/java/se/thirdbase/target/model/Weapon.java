package se.thirdbase.target.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.db.WeaponContract;

/**
 * Created by alexp on 2/26/16.
 */
public class Weapon implements Parcelable {

    private WeaponType mWeaponType;
    private String mMakeAndModel;
    private BulletCaliber mCaliber;
    private long mLastCleaned;
    private int mBulletsFired;
    private int mRemoved;
    private long mDBHandle = Long.MIN_VALUE;

    public Weapon(WeaponType type, String makeAndModel, BulletCaliber caliber) {
        this(type, makeAndModel, caliber, 0, 0, false);
    }

    public Weapon(WeaponType type, String makeAndModel, BulletCaliber caliber, long lastCleaned, int mBulletsFired, boolean removed) {
        mWeaponType = type;
        mMakeAndModel = makeAndModel;
        mCaliber = caliber;
        mLastCleaned = lastCleaned;
        mBulletsFired = mBulletsFired;
        mRemoved = removed ? 1 : 0;
    }

    protected Weapon(Parcel in) {
        mDBHandle = in.readLong();
        mMakeAndModel = in.readString();
        mCaliber = (BulletCaliber)in.readSerializable();
        mWeaponType = (WeaponType)in.readSerializable();
        mLastCleaned = in.readLong();
        mBulletsFired = in.readInt();
        mRemoved = in.readInt();
    }

    public static final Creator<Weapon> CREATOR = new Creator<Weapon>() {
        @Override
        public Weapon createFromParcel(Parcel in) {
            return new Weapon(in);
        }

        @Override
        public Weapon[] newArray(int size) {
            return new Weapon[size];
        }
    };

    public WeaponType getWeaponType() {
        return mWeaponType;
    }

    public void setWeaponType(WeaponType mWeaponType) {
        this.mWeaponType = mWeaponType;
    }

    public String getMakeAndModel() {
        return mMakeAndModel;
    }

    public void setMakeAndModel(String makeAndModel) {
        this.mMakeAndModel = makeAndModel;
    }

    public BulletCaliber getCaliber() {
        return mCaliber;
    }

    public void setCaliber(BulletCaliber mCaliber) {
        this.mCaliber = mCaliber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mDBHandle);
        dest.writeString(mMakeAndModel);
        dest.writeSerializable(mCaliber);
        dest.writeSerializable(mWeaponType);
        dest.writeLong(mLastCleaned);
        dest.writeInt(mBulletsFired);
        dest.writeInt(mRemoved);
    }

    /*** Database handling **/

    public long getDBHandle() {
        return mDBHandle;
    }

    public long store(SQLiteDatabase db) {
        if (mDBHandle == Long.MIN_VALUE) {
            ContentValues values = new ContentValues();

            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_DATE_TIME, System.currentTimeMillis());
            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_TYPE, mWeaponType.ordinal());
            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_MAKE_AND_MODEL, mMakeAndModel);
            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_CALIBER, mCaliber.ordinal());
            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_LAST_CLEANED, mLastCleaned);
            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_BULLETS_FIRED, mBulletsFired);
            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_REMOVED, mRemoved);

            mDBHandle = db.insert(WeaponContract.TABLE_NAME, null, values);
        }

        return mDBHandle;
    }

    public static Weapon fetch(SQLiteDatabase db, long id) {
        String selection = WeaponContract.WeaponEntry._ID + "=?";
        String[] args = { String.format("%d", id) };
        List<Weapon> weapons = fetchSelection(db, selection, args, null, null, null, null);

        return weapons.size() == 1 ? weapons.get(0) : null;
    }

    public static List<Weapon> fetchAll(SQLiteDatabase db, String orderBy) {
        return fetchSelection(db, null, null, null, null, orderBy, null);
    }

    public static List<Weapon> fetchSelection(SQLiteDatabase db, String selection, String[] selectionArgs,
                                              String groupBy, String having, String orderBy, String limit) {
        String[] columns = {
                WeaponContract.WeaponEntry._ID,
                WeaponContract.WeaponEntry.COLUMN_NAME_DATE_TIME,
                WeaponContract.WeaponEntry.COLUMN_NAME_TYPE,
                WeaponContract.WeaponEntry.COLUMN_NAME_MAKE_AND_MODEL,
                WeaponContract.WeaponEntry.COLUMN_NAME_CALIBER,
                WeaponContract.WeaponEntry.COLUMN_NAME_LAST_CLEANED,
                WeaponContract.WeaponEntry.COLUMN_NAME_BULLETS_FIRED,
                WeaponContract.WeaponEntry.COLUMN_NAME_REMOVED
        };

        Cursor cursor = db.query(WeaponContract.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy,
                limit);

        List<Weapon> weapons = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {

            try {
                while (!cursor.isAfterLast()) {
                    Weapon weapon = Weapon.fromCursor(db, cursor);
                    weapons.add(weapon);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }

        return weapons;
    }

    private static Weapon fromCursor(SQLiteDatabase db, Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(WeaponContract.WeaponEntry._ID));
        BulletCaliber caliber = BulletCaliber.values()[cursor.getInt(cursor.getColumnIndex(WeaponContract.WeaponEntry.COLUMN_NAME_CALIBER))];
        WeaponType type = WeaponType.values()[cursor.getInt(cursor.getColumnIndex(WeaponContract.WeaponEntry.COLUMN_NAME_TYPE))];
        String makeAndModel = cursor.getString(cursor.getColumnIndex(WeaponContract.WeaponEntry.COLUMN_NAME_MAKE_AND_MODEL));
        long lastCleaned = cursor.getLong(cursor.getColumnIndex(WeaponContract.WeaponEntry.COLUMN_NAME_LAST_CLEANED));
        int bulletsFired = cursor.getInt(cursor.getColumnIndex(WeaponContract.WeaponEntry.COLUMN_NAME_BULLETS_FIRED));
        int removed = cursor.getInt(cursor.getColumnIndex(WeaponContract.WeaponEntry.COLUMN_NAME_REMOVED));

        Weapon weapon = new Weapon(type, makeAndModel, caliber, lastCleaned, bulletsFired, removed == 1);
        weapon.mDBHandle = id;

        return weapon;
    }
}
