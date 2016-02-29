package se.thirdbase.target.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.db.PrecisionSeriesContract;
import se.thirdbase.target.db.WeaponContract;

/**
 * Created by alexp on 2/26/16.
 */
public class Weapon implements Parcelable {

    private WeaponType mWeaponType;
    private String mManufacturer;
    private String mModel;
    private BulletCaliber mCaliber;
    private long mDBHandle = Long.MIN_VALUE;

    public Weapon(WeaponType type, String manufacturer, String model, BulletCaliber caliber) {
        mWeaponType = type;
        mManufacturer = manufacturer;
        mModel = model;
        mCaliber = caliber;
    }

    protected Weapon(Parcel in) {
        mDBHandle = in.readLong();
        mManufacturer = in.readString();
        mModel = in.readString();
        mCaliber = (BulletCaliber)in.readSerializable();
        mWeaponType = (WeaponType)in.readSerializable();
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

    public String getManufacturer() {
        return mManufacturer;
    }

    public void setManufacturer(String mManufacturer) {
        this.mManufacturer = mManufacturer;
    }

    public String getModel() {
        return mModel;
    }

    public void setModel(String mModel) {
        this.mModel = mModel;
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
        dest.writeString(mManufacturer);
        dest.writeString(mModel);
        dest.writeSerializable(mCaliber);
        dest.writeSerializable(mWeaponType);
    }

    /*** Database handling **/

    public long getDBHandle() {
        return mDBHandle;
    }

    public long store(SQLiteDatabase db) {
        if (mDBHandle == Long.MIN_VALUE) {
            ContentValues values = new ContentValues();

            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_CALIBER, mCaliber.ordinal());
            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_TYPE, mWeaponType.ordinal());
            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_MANUFACTURER, mManufacturer);
            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_MODEL, mModel);
            values.put(WeaponContract.WeaponEntry.COLUMN_NAME_DATE_TIME, System.currentTimeMillis());

            mDBHandle = db.insert(WeaponContract.TABLE_NAME, null, values);
        }

        return mDBHandle;
    }

    public static Weapon fetch(SQLiteDatabase db, long id) {
        String[] columns = {
                WeaponContract.WeaponEntry.COLUMN_NAME_DATE_TIME,
                WeaponContract.WeaponEntry.COLUMN_NAME_TYPE,
                WeaponContract.WeaponEntry.COLUMN_NAME_MANUFACTURER,
                WeaponContract.WeaponEntry.COLUMN_NAME_MODEL,
                WeaponContract.WeaponEntry.COLUMN_NAME_CALIBER
        };

        Cursor cursor = db.query(WeaponContract.TABLE_NAME,
                columns,
                WeaponContract.WeaponEntry._ID + "=?",
                new String[] {"" + id},
                null,
                null,
                null,
                null);

        Weapon weapon = null;

        if (cursor != null && cursor.moveToFirst()) {
            try {

                BulletCaliber caliber = BulletCaliber.values()[cursor.getInt(cursor.getColumnIndex(WeaponContract.WeaponEntry.COLUMN_NAME_CALIBER))];
                WeaponType type = WeaponType.values()[cursor.getInt(cursor.getColumnIndex(WeaponContract.WeaponEntry.COLUMN_NAME_TYPE))];
                String manufacturer = cursor.getString(cursor.getColumnIndex(WeaponContract.WeaponEntry.COLUMN_NAME_MANUFACTURER));
                String model = cursor.getString(cursor.getColumnIndex(WeaponContract.WeaponEntry.COLUMN_NAME_MODEL));

                weapon = new Weapon(type, manufacturer, model, caliber);
                weapon.mDBHandle = id;
            } finally {
                cursor.close();
            }
        }

        return weapon;
    }

    public static List<Weapon> fetchAll(SQLiteDatabase db, String orderBy) {
        String[] columns = { WeaponContract.WeaponEntry._ID };

        Cursor cursor = db.query(WeaponContract.TABLE_NAME,
                columns,
                null, // selection
                null, // selectionArgs
                null, // groupBy
                null, // having
                orderBy);

        List<Weapon> weapons = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {

            try {
                while (!cursor.isAfterLast()) {
                    int weaponId = cursor.getInt(cursor.getColumnIndex(PrecisionSeriesContract.PrecisionSeriesEntry._ID));
                    Weapon weapon = Weapon.fetch(db, weaponId);

                    weapons.add(weapon);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }

        return weapons;
    }
}
