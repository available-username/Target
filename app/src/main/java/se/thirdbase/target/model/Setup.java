package se.thirdbase.target.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.db.SetupContract;

/**
 * Created by alexp on 2/29/16.
 */
public class Setup implements Parcelable {

    private Principle mPrinciple;
    private Weapon mWeapon;
    private Ammunition mAmmunition;
    private long mDBHandle = Long.MIN_VALUE;

    public Setup(Principle principle, Weapon weapon, Ammunition ammunition) {
        mPrinciple = principle;
        mWeapon = weapon;
        mAmmunition = ammunition;
    }

    protected Setup(Parcel in) {
        mWeapon = in.readParcelable(Weapon.class.getClassLoader());
        mAmmunition = in.readParcelable(Ammunition.class.getClassLoader());
        mDBHandle = in.readLong();
    }

    public static final Creator<Setup> CREATOR = new Creator<Setup>() {
        @Override
        public Setup createFromParcel(Parcel in) {
            return new Setup(in);
        }

        @Override
        public Setup[] newArray(int size) {
            return new Setup[size];
        }
    };

    public Principle getPrinciple() {
        return mPrinciple;
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
        dest.writeParcelable(mWeapon, flags);
        dest.writeParcelable(mAmmunition, flags);
        dest.writeLong(mDBHandle);
    }

    /*** Database handling ***/

    public long getDBHandle() {
        return mDBHandle;
    }

    public long store(SQLiteDatabase db) {
        if (mDBHandle == Long.MIN_VALUE) {
            ContentValues values = new ContentValues();

            values.put(SetupContract.SetupEntry.COLUMN_NAME_PRINCIPLE, mPrinciple.ordinal());
            values.put(SetupContract.SetupEntry.COLUMN_NAME_WEAPON, mWeapon.getDBHandle());
            values.put(SetupContract.SetupEntry.COLUMN_NAME_AMMUNITION, mAmmunition.getDBHandle());
            values.put(SetupContract.SetupEntry.COLUMN_NAME_DATE_TIME, System.currentTimeMillis());

            mDBHandle = db.insert(SetupContract.TABLE_NAME, null, values);
        }

        return mDBHandle;
    }

    public static Setup fetch(SQLiteDatabase db, long id) {
        String[] columns = {
                SetupContract.SetupEntry.COLUMN_NAME_PRINCIPLE,
                SetupContract.SetupEntry.COLUMN_NAME_AMMUNITION,
                SetupContract.SetupEntry.COLUMN_NAME_DATE_TIME,
                SetupContract.SetupEntry.COLUMN_NAME_WEAPON
        };

        Cursor cursor = db.query(SetupContract.TABLE_NAME,
                columns,
                SetupContract.SetupEntry._ID + "?=",
                new String[] {"" + id},
                null,
                null,
                null,
                null);

        Setup setup = null;

        if (cursor != null && cursor.moveToFirst()) {
            try {
                Principle principle = Principle.values()[cursor.getInt(cursor.getColumnIndex(SetupContract.SetupEntry.COLUMN_NAME_PRINCIPLE))];

                int weaponId = cursor.getInt(cursor.getColumnIndex(SetupContract.SetupEntry.COLUMN_NAME_WEAPON));
                Weapon weapon = Weapon.fetch(db, weaponId);

                int ammoId = cursor.getInt(cursor.getColumnIndex(SetupContract.SetupEntry.COLUMN_NAME_WEAPON));
                Ammunition ammunition = Ammunition.fetch(db, ammoId);

                setup = new Setup(principle, weapon, ammunition);
                setup.mDBHandle = id;
            } finally {
                cursor.close();
            }
        }

        return setup;
    }

    public static List<Setup> fetchAll(SQLiteDatabase db, String orderBy) {
        String[] columns = { SetupContract.SetupEntry._ID };

        Cursor cursor = db.query(SetupContract.TABLE_NAME,
                columns,
                null, // selection
                null, // selectionArgs
                null, // groupBy
                null, // having
                orderBy);

        List<Setup> setups = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            try {
                while (!cursor.isAfterLast()) {
                    int setupId = cursor.getInt(cursor.getColumnIndex(SetupContract.SetupEntry._ID));
                    Setup setup = Setup.fetch(db, setupId);

                    setups.add(setup);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }

        return setups;
    }
}
