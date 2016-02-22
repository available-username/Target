package se.thirdbase.target.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import se.thirdbase.target.util.ViewMath;

/**
 * Created by alexp on 2/19/16.
 */
public class GraphView extends View {

    private static final String TAG = GraphView.class.getSimpleName();

    private static final float RATIO = 1.618034f; //Golden ratio
    private static final int VIRTUAL_WIDTH = 110;
    private static int VIRTUAL_HEIGHT;
    private static final float MAX_ZOOM_FACTOR = 5f;

    private Rect mRect = new Rect();
    private Map<Integer, List<Pair<Float,Float>>> mDataMap = new HashMap<>();
    private AtomicInteger mMapIndexer = new AtomicInteger();

    private ViewMath mViewMath;

    private float mMinXValue = Float.MAX_VALUE;
    private float mMaxXValue = Float.MIN_VALUE;
    private float mMinYValue = Float.MAX_VALUE;
    private float mMaxYValue = Float.MIN_VALUE;


    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);
    }

    public GraphView(Context context) {
        super(context);
        setSaveEnabled(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "onDraw()");

        PointF origo = translate(0, 0);
        PointF xAxisEnd = translate(VIRTUAL_WIDTH, 0);
        PointF yAxisEnd = translate(0, VIRTUAL_HEIGHT);

        Paint paint = new Paint(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);

        Log.d(TAG, String.format("Origo / xAxis: %s  %s", origo, xAxisEnd));
        Log.d(TAG, String.format("Origo / yAxis: %s  %s", origo, yAxisEnd));
        canvas.drawLine(origo.x, origo.y, xAxisEnd.x, xAxisEnd.y, paint);
        canvas.drawLine(origo.x, origo.y, yAxisEnd.x, yAxisEnd.y, paint);
    }

    private PointF translate(float x, float y) {
        float pixelsPerCm = mViewMath.getPixelsPerCm();

        Log.d(TAG, String.format("translate(%.2f, %.2f)", x, y));

        PointF point = new PointF();
        point.x = x * pixelsPerCm;
        point.y = (VIRTUAL_HEIGHT - y) * pixelsPerCm;

        return point;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d(TAG, String.format("onSizeChanged(%d, %d, %d, %d)", w, h, oldw, oldh));
        mRect = new Rect(0, 0, w, h);

        VIRTUAL_HEIGHT = (int)(VIRTUAL_WIDTH / RATIO);

        mViewMath = new ViewMath(w, h, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, MAX_ZOOM_FACTOR);

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View parent = (View)getParent();

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentWidth = parent.getWidth();
        int width;

        switch (wMode) {
            case MeasureSpec.EXACTLY: width = wSize; break;
            case MeasureSpec.AT_MOST: width = Math.min(wSize, parentWidth); break;
            default: width = parentWidth;
        }

        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int parentHeight = parent.getHeight();
        int height;

        switch (hMode) {
            case MeasureSpec.EXACTLY: height = hSize; break;
            case MeasureSpec.AT_MOST: height = Math.min(hSize, parentHeight); break;
            default: height = parentHeight;
        }

        Log.d(TAG, String.format("setMeasuredDimension(%d, %d)", width, height));
        super.setMeasuredDimension(width, height);
    }

    public int addDataPoints(List<Pair<Float, Float>> dataPoints) {
        int idx = mMapIndexer.incrementAndGet();

        mDataMap.put(idx, dataPoints);

        for (Pair<Float, Float>  p : dataPoints) {
            if (p.first < mMinXValue) {
                mMinXValue = p.first;
            }
            if (p.first > mMaxXValue) {
                mMaxXValue = p.first;
            }

            if (p.second < mMinYValue) {
                mMinYValue = p.second;
            }
            if (p.second > mMaxYValue) {
                mMaxXValue = p.second;
            }
        }

        invalidate();

        return idx;
    }
}
