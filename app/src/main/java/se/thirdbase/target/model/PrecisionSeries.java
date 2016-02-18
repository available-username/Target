package se.thirdbase.target.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexp on 2/17/16.
 */
public class PrecisionSeries implements Parcelable {

    private static final float RING_RADIUS_INCREMENT = 2.5f;

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
        mScore += calculateScore(bulletHole);
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
            score += calculateScore(hole);
        }

        return score;
    }

    private int calculateScore(BulletHole hole) {
        float diameter = hole.getCaliber().getDiameter();
        float radius = Math.abs(hole.getRadius() - diameter / 2);

        return (int) Math.ceil(10 - radius / RING_RADIUS_INCREMENT);
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
}

