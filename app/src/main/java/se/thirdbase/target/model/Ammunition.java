package se.thirdbase.target.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.db.AmmunitionContract;

/**
 * Created by alexp on 2/26/16.
 */
public class Ammunition implements Parcelable {

    private AmmunitionType mType;
    private String mMakeAndName;
    private BulletCaliber mCaliber;
    private double mGrains;
    private int mMuzzleVelocity;
    private int mRemoved;
    private long mDBHandle = Long.MIN_VALUE;

    public Ammunition(AmmunitionType type, String makeAndName, BulletCaliber caliber,
                      double grains, int muzzleVelocity) {
        this(type, makeAndName, caliber, grains, muzzleVelocity, false);
    }

    public Ammunition(AmmunitionType type, String makeAndName, BulletCaliber caliber,
                      double grains, int muzzleVelocity, boolean removed) {
        mType = type;
        mMakeAndName = makeAndName;
        mCaliber = caliber;
        mGrains = grains;
        mMuzzleVelocity = muzzleVelocity;
        mRemoved = removed ? 1 : 0;
    }

    protected Ammunition(Parcel in) {
        mMakeAndName = in.readString();
        mGrains = in.readDouble();
        mMuzzleVelocity = in.readInt();
        mRemoved = in.readInt();
        mCaliber = (BulletCaliber)in.readSerializable();
        mType = (AmmunitionType)in.readSerializable();
    }

    public static final Creator<Ammunition> CREATOR = new Creator<Ammunition>() {
        @Override
        public Ammunition createFromParcel(Parcel in) {
            return new Ammunition(in);
        }

        @Override
        public Ammunition[] newArray(int size) {
            return new Ammunition[size];
        }
    };

    public AmmunitionType getType() {
        return mType;
    }

    public void setType(AmmunitionType type) {
        mType = type;
    }

    public String getMakeAndName() {
        return mMakeAndName;
    }

    public void setMakeAndName(String makeAndName) {
        mMakeAndName = makeAndName;
    }

    public BulletCaliber getCaliber() {
        return mCaliber;
    }

    public void setCaliber(BulletCaliber caliber) {
        mCaliber = caliber;
    }

    public double getGrains() {
        return mGrains;
    }

    public void setGrains(double grains) {
        mGrains = grains;
    }

    public int getMuzzleVelocity() {
        return mMuzzleVelocity;
    }

    public void setMuzzleVelocity(int muzzleVelocity) {
        mMuzzleVelocity = muzzleVelocity;
    }

    public boolean getRemoved() {
        return mRemoved == 1? true : false;
    }

    public void setRemoved(boolean removed) {
        mRemoved = removed ? 1 : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMakeAndName);
        dest.writeDouble(mGrains);
        dest.writeInt(mMuzzleVelocity);
        dest.writeInt(mRemoved);
        dest.writeSerializable(mCaliber);
        dest.writeSerializable(mType);
    }

    /*** Database handling **/

    public long getDBHandle() {
        return mDBHandle;
    }

    public long store(SQLiteDatabase db) {
        if (mDBHandle == Long.MIN_VALUE) {
            ContentValues values = new ContentValues();

            values.put(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_CALIBER, mCaliber.ordinal());
            values.put(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_TYPE, mType.ordinal());
            values.put(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_GRAINS, mGrains);
            values.put(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_MAKE_AND_NAME, mMakeAndName);
            values.put(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_MUZZLE_VELOCITY, mMuzzleVelocity);
            values.put(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_REMOVED, mRemoved);
            values.put(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_DATE_TIME, System.currentTimeMillis());

            mDBHandle = db.insert(AmmunitionContract.TABLE_NAME, null, values);
        }

        return mDBHandle;
    }

    public static Ammunition fetch(SQLiteDatabase db, long id) {
        String selection = AmmunitionContract.AmmunitionEntry._ID + "=?";
        String[] args = { String.format("%d", id) };
        List<Ammunition> ammunition = fetchSelection(db, selection, args, null, null, null, null);

        return ammunition.size() == 1 ? ammunition.get(0) : null;
    }

    public static List<Ammunition> fetchAll(SQLiteDatabase db, String orderBy) {
        return fetchSelection(db, null, null, null, null, orderBy, null);
    }

    public static List<Ammunition> fetchSelection(SQLiteDatabase db, String selection, String[] selectionArgs,
                                                  String groupBy, String having, String orderBy, String limit) {
        String[] columns = {
                AmmunitionContract.AmmunitionEntry._ID,
                AmmunitionContract.AmmunitionEntry.COLUMN_NAME_TYPE,
                AmmunitionContract.AmmunitionEntry.COLUMN_NAME_MAKE_AND_NAME,
                AmmunitionContract.AmmunitionEntry.COLUMN_NAME_CALIBER,
                AmmunitionContract.AmmunitionEntry.COLUMN_NAME_GRAINS,
                AmmunitionContract.AmmunitionEntry.COLUMN_NAME_MUZZLE_VELOCITY,
                AmmunitionContract.AmmunitionEntry.COLUMN_NAME_REMOVED
        };

        Cursor cursor = db.query(AmmunitionContract.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy,
                limit);

        List<Ammunition> ammunitionList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {

            try {
                while (!cursor.isAfterLast()) {
                    Ammunition ammunition = fromCursor(db, cursor);
                    ammunitionList.add(ammunition);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }

        return ammunitionList;
    }

    private static Ammunition fromCursor(SQLiteDatabase db, Cursor cursor) {

        long id = cursor.getLong(cursor.getColumnIndex(AmmunitionContract.AmmunitionEntry._ID));
        BulletCaliber caliber = BulletCaliber.values()[cursor.getInt(cursor.getColumnIndex(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_CALIBER))];
        AmmunitionType type = AmmunitionType.values()[cursor.getInt(cursor.getColumnIndex(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_TYPE))];
        String makeAndName = cursor.getString(cursor.getColumnIndex(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_MAKE_AND_NAME));
        double grains = cursor.getDouble(cursor.getColumnIndex(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_GRAINS));
        int muzzleVelocity = cursor.getInt(cursor.getColumnIndex(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_MUZZLE_VELOCITY));
        int removed = cursor.getInt(cursor.getColumnIndex(AmmunitionContract.AmmunitionEntry.COLUMN_NAME_REMOVED));

        Ammunition ammunition = new Ammunition(type, makeAndName, caliber, grains, muzzleVelocity, removed == 1);
        ammunition.mDBHandle = id;

        return ammunition;
    }
}
