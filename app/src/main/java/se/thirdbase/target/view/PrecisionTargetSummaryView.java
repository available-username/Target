package se.thirdbase.target.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;

import se.thirdbase.target.R;
import se.thirdbase.target.model.BulletHole;

/**
 * Created by alex on 3/17/16.
 */
public class PrecisionTargetSummaryView extends PrecisionTargetView {

    private PointF mHitMean;
    private double mStd;
    private int mMeanColor;

    public PrecisionTargetSummaryView(Context context) {
        this(context, null);
    }

    public PrecisionTargetSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mMeanColor = context.getResources().getColor(R.color.target_view_statistics, null);
        } else {
            mMeanColor = context.getResources().getColor(R.color.target_view_statistics);
        }
    }

    public void setHitMean(PointF hitMean, double std) {
        mHitMean = hitMean;
        mStd = std;

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        PointF center = mViewMath.getCenterPixelCoordinate();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mMeanColor);
        paint.setStrokeWidth(4);

        float pixelsPerCm = getPixelsPerCm();
        float cx = center.x + mHitMean.x * pixelsPerCm;
        float cy = center.y + mHitMean.y * pixelsPerCm;
        float radius = (float)(mStd * pixelsPerCm);
        paint.setAntiAlias(true);

        canvas.drawCircle(cx, cy, radius, paint);

        float dist = 1.5f * pixelsPerCm;
        float x0 = cx - dist;
        float y0 = cy - dist;
        float x1 = cx + dist;
        float y1 = cy + dist;
        canvas.drawLine(x0, y0, x1, y1, paint);

        y0 = cy + dist;
        y1 = cy - dist;
        canvas.drawLine(x0, y0, x1, y1, paint);

        radius = (float)Math.sqrt(2 * Math.pow(dist, 2));
        canvas.drawCircle(cx, cy, radius, paint);
    }


}
