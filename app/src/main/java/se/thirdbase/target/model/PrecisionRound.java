package se.thirdbase.target.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.thirdbase.target.db.PrecisionRoundContract;

/**
 * Created by alexp on 2/18/16.
 */
public class PrecisionRound implements Parcelable {

    public static final int MAX_NBR_SERIES = 7; //real number is 7

    private List<PrecisionSeries> mPrecisionSeries;
    private int mScore;
    private String mNotes;
    private long mTimestamp;
    private long mDBHandle = Long.MIN_VALUE;

    public PrecisionRound() {
        mPrecisionSeries = new ArrayList<>();
    }

    public PrecisionRound(List<PrecisionSeries> precisionSeries, String notes) {
        mPrecisionSeries = precisionSeries;
        mScore = calculateScore(precisionSeries);
        mNotes = notes;
    }

    public PrecisionRound(List<PrecisionSeries> precisionSeries, String notes, long timestamp) {
        this(precisionSeries, notes);
        mTimestamp = timestamp;
    }

    protected PrecisionRound(Parcel in) {
        mPrecisionSeries = in.createTypedArrayList(PrecisionSeries.CREATOR);
        mScore = in.readInt();
        mNotes = in.readString();
        mTimestamp = in.readLong();
    }

    public static final Creator<PrecisionRound> CREATOR = new Creator<PrecisionRound>() {
        @Override
        public PrecisionRound createFromParcel(Parcel in) {
            return new PrecisionRound(in);
        }

        @Override
        public PrecisionRound[] newArray(int size) {
            return new PrecisionRound[size];
        }
    };

    public void addPrecisionSeries(PrecisionSeries precisionSeries) {
        mPrecisionSeries.add(precisionSeries);
        mScore = calculateScore(mPrecisionSeries);
    }

    public void setPrecisionSeries(List<PrecisionSeries> precisionSeries) {
        mPrecisionSeries = precisionSeries;
        mScore = calculateScore(precisionSeries);
    }

    public List<PrecisionSeries> getPrecisionSeries() {
        return Collections.unmodifiableList(mPrecisionSeries);
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public int getScore() {
        return mScore;
    }

    public int getNbrSeries() {
        return mPrecisionSeries.size();
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    private int calculateScore(List<PrecisionSeries> precisionSeries) {
        int score = 0;

        for (PrecisionSeries series : precisionSeries) {
            score += series.getScore();
        }

        return score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mPrecisionSeries);
        dest.writeInt(mScore);
        dest.writeString(mNotes);
        dest.writeLong(mTimestamp);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (PrecisionSeries series : mPrecisionSeries) {
            builder.append(String.format(" %d", series.getScore()));
        }
        return String.format("PrecisionRound(timestamp=%d, score=%d,%s)", mTimestamp, mScore, builder.toString());
    }

    /*** Database handling ***/

    public long getDBHandle() {
        return mDBHandle;
    }

    public long store(SQLiteDatabase db) {
        if (mDBHandle == Long.MIN_VALUE) {
            List<Long> ids = new ArrayList<>();

            for (PrecisionSeries series : mPrecisionSeries) {
                long id = series.getDBHandle();
                ids.add(id);
            }

            ContentValues values = new ContentValues();
            String[] columns = {
                    PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_SERIES_1,
                    PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_SERIES_2,
                    PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_SERIES_3,
                    PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_SERIES_4,
                    PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_SERIES_5,
                    PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_SERIES_6,
                    PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_SERIES_7,
                    PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_DATE_TIME,
            };

            int size = mPrecisionSeries.size();
            for (int i = 0; i < size; i++) {
                values.put(columns[i], ids.get(i));
            }

            values.put(PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_SCORE, getScore());
            values.put(PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_NOTES, getNotes());
            values.put(PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_DATE_TIME, System.currentTimeMillis());

            mDBHandle = db.insert(PrecisionRoundContract.TABLE_NAME, null, values);
        } else {
            update(db);
        }

        return mDBHandle;
    }

    public void update(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        String[] columns = {
                PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_SCORE,
                PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_NOTES,
                PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_DATE_TIME,
        };

        values.put(PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_SCORE, getScore());
        values.put(PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_NOTES, getNotes());
        values.put(PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_DATE_TIME, System.currentTimeMillis());

        db.update(PrecisionRoundContract.TABLE_NAME, values, PrecisionRoundContract.PrecisionRoundEntry._ID, new String[]{"" + mDBHandle  });
    }

}
