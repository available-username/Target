package se.thirdbase.target.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

public class BulletHole implements Parcelable {
    final BulletCaliber mCaliber;
    float radius;
    float angle;

    public BulletHole(BulletCaliber caliber, float radius, float angle) {
        this.mCaliber = caliber;
        this.radius = radius;
        this.angle = angle;
    }

    protected BulletHole(Parcel in) {
        mCaliber = (BulletCaliber) in.readSerializable();
        radius = in.readFloat();
        angle = in.readFloat();
    }

    public BulletHole copy() {
        return new BulletHole(mCaliber, radius, angle);
    }

    public void move(float deltaX, float deltaY) {
        float x = (float) (radius * Math.cos(angle)) + deltaX;
        float y = (float) (radius * Math.sin(angle)) + deltaY;

        radius = (float) Math.sqrt(x * x + y * y);
        angle = (float) Math.atan2(y, x);
    }

    public PointF toCartesianCoordinates() {
        float x = (float) (radius * Math.cos(angle));
        float y = (float) (radius * Math.sin(angle));

        return new PointF(x, y);
    }


    public float getRadius() {
        return radius;
    }

    public float getAngle() {
        return angle;
    }

    public BulletCaliber getCaliber() {
        return mCaliber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mCaliber);
        dest.writeFloat(radius);
        dest.writeFloat(angle);
    }

    public static final Creator<BulletHole> CREATOR = new Creator<BulletHole>() {
        @Override
        public BulletHole createFromParcel(Parcel in) {
            return new BulletHole(in);
        }

        @Override
        public BulletHole[] newArray(int size) {
            return new BulletHole[size];
        }
    };
}
