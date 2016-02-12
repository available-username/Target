package se.thirdbase.target.model;

import android.graphics.PointF;

public class BulletHole {
    float cmX;
    float cmY;

    public BulletHole(float cmX, float cmY) {
        this.cmX = cmX;
        this.cmY = cmY;
    }

    public BulletHole(int pixelX, int pixelY, int viewWidth, int viewHeight, float targetWidth, float targetHeight) {
        cmX = targetWidth * pixelX / viewWidth;
        cmY = targetHeight * pixelY / viewHeight;
    }

    public PointF toPixelLocation(float pixelsPerCm) {
        return new PointF(cmX * pixelsPerCm, cmY * pixelsPerCm);
    }

}
