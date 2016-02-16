package se.thirdbase.target.model;

import android.graphics.PointF;

public class BulletHole {
    final BulletCaliber caliber;
    float cmX;
    float cmY;


    public BulletHole(BulletCaliber caliber, float cmX, float cmY) {
        this.caliber = caliber;
        this.cmX = cmX;
        this.cmY = cmY;
    }

    public BulletHole copy() {
        return new BulletHole(caliber, cmX, cmY);
    }

    public void move(float deltaX, float deltaY) {
        cmX += deltaX;
        cmY += deltaY;
    }

    public PointF toPixelLocation(float pixelsPerCm) {
        return new PointF(cmX * pixelsPerCm, cmY * pixelsPerCm);
    }

    public float getCmX() {
        return cmX;
    }

    public float getCmY() {
        return cmY;
    }

    public float getRadius(float width, float height) {
        float x = (width / 2 - cmX);
        float y = (height / 2 - cmY);

        return (float) Math.sqrt(x * x + y * y);
    }

    public float getAngle(float width, float height) {
        float x = (width / 2 - cmX);
        float y = (height / 2 - cmY);

        return (float) (Math.PI - Math.atan2(y, x));
    }

    public BulletCaliber getCaliber() {
        return caliber;
    }
}
