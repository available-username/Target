package se.thirdbase.target.model;

/**
 * Created by alexp on 2/12/16.
 */
public enum BulletCaliber {
    CAL_22(0.22f, true, ".22 LR"),
    CAL_25(0.25f, true, ".25"),
    CAL_9MM(0.9f, false, "9mm"),
    CAL_10MM(1.0f, false, "10mm"),
    CAL_30(0.3f, true, ".30"),
    CAL_308WIN(0.308f, true, "0.308 Win"),
    CAL_357(0.357f, true, ".357"),
    CAL_380(0.380f, true, ".380"),
    CAL_40(0.4f, true, ".40"),
    CAL_44(0.44f, true, ".44"),
    CAL_45ACP(0.45f, true, ".45 ACP"),
    CAL_50(0.50f, true, ".50");

    private static final float CM_PER_INCH = 2.54f;

    private float mDiameter;
    private boolean mInches;
    private String mName;

    BulletCaliber(float diameter, boolean inches, String name) {
        mDiameter = inches ? diameter * CM_PER_INCH : diameter;
        mInches = inches;
        mName = name;
    }

    public float getDiameter() {
        return mDiameter;
    }

    public String toString() {
        //float diameter = mInches ? mDiameter / CM_PER_INCH : mDiameter / 10;
        //return String.format("%.2f%s", diameter, mInches ? "\"" : "mm");
        return mName;
    }
}
