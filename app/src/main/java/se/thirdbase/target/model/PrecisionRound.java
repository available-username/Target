package se.thirdbase.target.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexp on 2/18/16.
 */
public class PrecisionRound implements Parcelable {

    private List<PrecisionSeries> mPrecisionSeries;
    private int mScore;
    private String mNotes;

    public PrecisionRound() {
        mPrecisionSeries = new ArrayList<>();
    }

    public PrecisionRound(List<PrecisionSeries> precisionSeries, String notes) {
        mPrecisionSeries = precisionSeries;
        mScore = calculateScore(precisionSeries);
        mNotes = notes;
    }

    protected PrecisionRound(Parcel in) {
        mPrecisionSeries = in.createTypedArrayList(PrecisionSeries.CREATOR);
        mScore = in.readInt();
        mNotes = in.readString();
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

    private int calculateScore(List<PrecisionSeries> precisionSeries) {
        int score = 0;

        for (PrecisionSeries series : precisionSeries) {
            score = series.getScore();
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
    }
}
