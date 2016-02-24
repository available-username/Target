package se.thirdbase.target.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexp on 2/17/16.
 */
public class PrecisionSeries implements Parcelable {

    private List<BulletHole> mBulletHoles;
    private int mScore;

    public PrecisionSeries() {
        mBulletHoles = new ArrayList<>();
    }

    public PrecisionSeries(List<BulletHole> bulletHoles) {
        mBulletHoles = bulletHoles;
        mScore = calculateScore(bulletHoles);
    }

    protected PrecisionSeries(Parcel in) {
        mBulletHoles = in.createTypedArrayList(BulletHole.CREATOR);
        mScore = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mBulletHoles);
        dest.writeInt(mScore);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (BulletHole hole : mBulletHoles) {
            builder.append(String.format(" %d", PrecisionTarget.getBulletScore(hole)));
        }

        return String.format("PrecisionSeries(score=%d,%s)", mScore, builder.toString());
    }
}

