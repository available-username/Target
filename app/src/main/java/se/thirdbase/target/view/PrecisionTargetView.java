package se.thirdbase.target.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.precision.PrecisionTarget;

/**
 * Created by alexp on 2/19/16.
 */
public class PrecisionTargetView extends TargetView {

    private static final String TAG = PrecisionTargetView.class.getSimpleName();

    private float mVirtualWidth = 60f;
    private float mVirtualHeight;

    public PrecisionTargetView(Context context) {
        super(context, null);
    }

    public PrecisionTargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getMaxNbrBullets() {
        return PrecisionTarget.MAX_NBR_BULLETS;
    }

    @Override
    public int getBulletScore(int bulletIdx) {
        BulletHole hole = mBulletHoles.get(bulletIdx);
        return PrecisionTarget.getBulletScore(hole);
    }

    @Override
    public int getTotalScore() {
        int total = 0;
        int size = mBulletHoles.size();

        for (int i = 0; i < size; i++) {
            total += getBulletScore(i);
        }

        return total;
    }

    @Override
    protected float getVirtualWidth() {
        return mVirtualWidth;
    }

    @Override
    protected void setVirtualWidth(float width) {
        mVirtualWidth = width;
    }

    @Override
    protected float getVirtualHeight() {
        return mVirtualHeight;
    }

    @Override
    protected void setVirtualHeight(float height) {
        mVirtualHeight = height;
    }

    private boolean touches(float bulletRadius, float bulletDiameter, float radius) {
        float upperBound = bulletRadius + bulletDiameter / 2;
        float lowerBound = bulletRadius - bulletDiameter / 2;

        return lowerBound <= radius && radius <= upperBound;
    }

    @Override
    protected void drawTarget(Canvas canvas) {
        float pixelsPerCm = mViewMath.getPixelsPerCm();

        float radiusIncrement = 2.5f; // cm
        float textSize = pixelsPerCm * radiusIncrement * 0.75f;
        float textHeightOffset = textSize / 2;
        float textWidthOffset = (pixelsPerCm * radiusIncrement) / 2;

        PointF center = mViewMath.getCenterPixelCoordinate();

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawColor(Color.WHITE);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        float bulletRadius = Float.MAX_VALUE;
        float bulletDiameter = 0f;
        int ring;

        BulletHole bulletHole = getActiveBulletHole();
        if (bulletHole != null) {
            bulletRadius = bulletHole.getRadius();
            bulletDiameter = bulletHole.getCaliber().getDiameter();
        }

        for (ring = 10; ring > 4; ring--) {
            float radius = ring * radiusIncrement;

            if (touches(bulletRadius, bulletDiameter, radius)) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.BLACK);
            }

            canvas.drawCircle(center.x, center.y, radius * pixelsPerCm, paint);
        }

        paint.setColor(Color.BLACK);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(center.x, center.y, ring * radiusIncrement * pixelsPerCm, paint);

        paint.setStyle(Paint.Style.STROKE);

        for (; ring > 0; ring--) {
            float radius = ring * radiusIncrement;

            if (touches(bulletRadius, bulletDiameter, radius)) {
                paint.setColor(Color.RED);
            } else {
                if (ring == 4) {
                    paint.setColor(Color.BLACK);
                } else {
                    paint.setColor(Color.WHITE);
                }
            }

            canvas.drawCircle(center.x, center.y, radius * pixelsPerCm, paint);
        }

        paint.setColor(Color.WHITE);

        // finally, the inner ring
        canvas.drawCircle(center.x, center.y, 1.25f * pixelsPerCm, paint);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1);
        paint.setFakeBoldText(true);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < 4; i++) {
            double angle = i * Math.PI / 2;

            int num;
            for (num = 1; num < 10; num++) {
                int color;

                if (num < 4) {
                    color = Color.WHITE;
                } else {
                    color = Color.BLACK;
                }

                paint.setColor(color);

                float r = pixelsPerCm * radiusIncrement * num;
                float x = center.x + (float) (r * Math.cos(angle));
                float y = center.y + (float) (r * Math.sin(angle));

                switch (i) {
                    case 0:
                        paint.setTextAlign(Paint.Align.CENTER);
                        x += textWidthOffset;
                        y += textHeightOffset / 2;
                        break;
                    case 1:
                        paint.setTextAlign(Paint.Align.CENTER);
                        y += textHeightOffset * 2;
                        break;
                    case 2:
                        paint.setTextAlign(Paint.Align.CENTER);
                        x -= textWidthOffset;
                        y += textHeightOffset / 2;
                        break;
                    case 3:
                        paint.setTextAlign(Paint.Align.CENTER);
                        y -= textHeightOffset;
                        break;
                }

                canvas.drawText("" + (10 - num), x, y, paint);
            }
        }
    }
}
