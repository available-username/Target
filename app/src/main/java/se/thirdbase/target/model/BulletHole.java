package se.thirdbase.target.model;

import android.graphics.PointF;

public class BulletHole {
    final BulletCaliber caliber;
    float cmX;
    float cmY;
    float distanceFromBullseye;

    public BulletHole(BulletCaliber caliber, float cmX, float cmY) {
        this.caliber = caliber;
        this.cmX = cmX;
        this.cmY = cmY;
        distanceFromBullseye = (float) Math.sqrt(cmX * cmX + cmY * cmY);
    }

    public BulletHole(BulletCaliber caliber, int pixelX, int pixelY, int viewWidth, int viewHeight, float targetWidth, float targetHeight) {
        this.caliber = caliber;
        cmX = targetWidth * pixelX / viewWidth;
        cmY = targetHeight * pixelY / viewHeight;
    }


    public void move(float deltaX, float deltaY) {
        cmX += deltaX;
        cmY += deltaY;
        distanceFromBullseye = (float) Math.sqrt(cmX * cmX + cmY * cmY);
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

    public float getDistanceFromBullseye() {
        return distanceFromBullseye;
    }

    public BulletCaliber getCaliber() {
        return caliber;
    }
}
