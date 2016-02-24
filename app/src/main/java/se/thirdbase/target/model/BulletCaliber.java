package se.thirdbase.target.model;

/**
 * Created by alexp on 2/12/16.
 */
public enum BulletCaliber {
    CAL_22(0.22f, true),
    CAL_9MM(0.9f, false),
    CAL_10MM(1.0f, false),
    CAL_45ACP(0.45f, true),
    CAL_50(0.50f, true);

    private static final float CM_PER_INCH = 2.54f;

    private float mDiameter;
    private boolean mInches;

    BulletCaliber(float diameter, boolean inches) {
        mDiameter = inches ? diameter * CM_PER_INCH : diameter;
        mInches = inches;
    }

    public float getDiameter() {
        return mDiameter;
    }

    public String toString() {
        float diameter = mInches ? mDiameter / CM_PER_INCH : mDiameter / 10;
        return String.format("%.2f%s", diameter, mInches ? "\"" : "mm");
    }
}
