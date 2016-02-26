package se.thirdbase.target.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.db.PrecisionSeriesContract;

/**
 * Created by alexp on 2/17/16.
 */
public class PrecisionSeries implements Parcelable {

    private List<BulletHole> mBulletHoles;
    private int mScore;
    private long mTimestamp;
    private long mDBHandle = Long.MIN_VALUE;

    public PrecisionSeries() {
        mBulletHoles = new ArrayList<>();
    }

    public PrecisionSeries(List<BulletHole> bulletHoles) {
        mBulletHoles = bulletHoles;
        mScore = calculateScore(bulletHoles);
    }

    public PrecisionSeries(List<BulletHole> bulletHoles, long timestamp) {
        this(bulletHoles);
        mTimestamp = timestamp;
    }

    protected PrecisionSeries(Parcel in) {
        mBulletHoles = in.createTypedArrayList(BulletHole.CREATOR);
        mScore = in.readInt();
        mTimestamp = in.readLong();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mBulletHoles);
        dest.writeInt(mScore);
        dest.writeLong(mTimestamp);
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
                    PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_BULLET_5
            };

            int size = mBulletHoles.size();
            for (int i = 0; i < size; i++) {
                values.put(columns[i], ids.get(i));
            }

            values.put(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_SCORE, getScore());
            values.put(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME, System.currentTimeMillis());

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

        db.update(PrecisionSeriesContract.TABLE_NAME, values, PrecisionSeriesContract.PrecisionSeriesEntry._ID, new String[]{"" + mDBHandle});
    }
}

