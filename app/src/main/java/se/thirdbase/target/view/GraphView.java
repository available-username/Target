package se.thirdbase.target.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
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
    private static final float VIRTUAL_WIDTH = 110;
    private static float VIRTUAL_HEIGHT;
    private static final float MAX_ZOOM_FACTOR = 5f;

    private static float XMARGIN = 5;
    private static float YMARGIN = 5;

    private Rect mRect = new Rect();
    private Map<Integer, List<Pair<Float,Float>>> mDataMap = new HashMap<>();
    private Map<Integer, List<Pair<Float, Float>>> mNormalizedDataMap = new HashMap<>();
    private AtomicInteger mMapIndexer = new AtomicInteger();

    private ViewMath mViewMath;

    private float mMinXValue = Float.MAX_VALUE;
    private float mMaxXValue = Float.MIN_VALUE;
    private float mMinYValue = Float.MAX_VALUE;
    private float mMaxYValue = Float.MIN_VALUE;
    private float mNormXAxis;
    private float mNormYAxis;


    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);

        createSampleData();
    }

    public GraphView(Context context) {
        this(context, null);
    }

    private void createSampleData() {
        List data = new ArrayList();

        Log.d(TAG, "createSampleData()");

        float maxTime = (float)Math.PI * 2 * 6;

        int nSamples = 1000;
        for (int i = 0; i < nSamples; i++) {
            float t = i * maxTime / nSamples;
            Pair pair = new Pair(t, (float)Math.sin(t) + 1);
            data.add(pair);
        }

        addDataPoints(data);
    }

    private void normalizeData() {
        mNormalizedDataMap = new HashMap<>();

        Log.d(TAG, "normalizeData()");

        for (Integer key : mDataMap.keySet()) {

            List<Pair<Float, Float>> data = mDataMap.get(key);

            List<Pair<Float, Float>> norm = new ArrayList<>();

            for (Pair<Float, Float> p : data) {
                float normX = (VIRTUAL_WIDTH - 2 * XMARGIN) * p.first / mNormXAxis;
                float normY = (VIRTUAL_HEIGHT - 2 * YMARGIN) * p.second / mNormYAxis;
                Pair<Float, Float> q = new Pair<>(normX, normY);
                norm.add(q);
            }

            mNormalizedDataMap.put(key, norm);
        }

        Log.d(TAG, "Normalized data set size: " + mNormalizedDataMap.size());
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "onDraw()");

        normalizeData();

        Paint paint = new Paint(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);

        // Draw border
        drawRect(canvas, 0, VIRTUAL_HEIGHT, VIRTUAL_WIDTH, 0, paint);

        // Draw axis
        paint.setStrokeWidth(5);
        drawLine(canvas, XMARGIN, YMARGIN, VIRTUAL_WIDTH - XMARGIN, YMARGIN, paint);
        drawLine(canvas, XMARGIN, YMARGIN, XMARGIN, VIRTUAL_HEIGHT - YMARGIN, paint);

        for (Integer key : mNormalizedDataMap.keySet()) {
            List<Pair<Float, Float>> data = mNormalizedDataMap.get(key);

            drawData(canvas, data, paint);
        }
    }

    private void drawData(Canvas canvas, List<Pair<Float, Float>> data, Paint paint) {
        Path path = new Path();

        Log.d(TAG, "drawData()");

        int size = data.size();
        Pair<Float, Float> pair = data.get(0);
        PointF point = translate(pair.first + XMARGIN, pair.second + YMARGIN);
        path.moveTo(point.x, point.y);

        for (int i = 1; i < size; i++) {
            pair = data.get(i);

            point = translate(pair.first + XMARGIN, pair.second + YMARGIN);
            path.lineTo(point.x, point.y);
        }

        canvas.drawPath(path, paint);
    }

    private void drawRect(Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
        PointF leftTop = translate(left, top);
        PointF rightBottom = translate(right, bottom);

        Log.d(TAG, String.format("drawRect(%.2f, %.2f, %.2f, %.2f)", leftTop.x, leftTop.y, rightBottom.x, rightBottom.y));

        canvas.drawRect(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, paint);
    }

    private void drawLine(Canvas canvas, float startX, float startY, float endX, float endY, Paint paint) {
        PointF start = translate(startX, startY);
        PointF end = translate(endX, endY);

        Log.d(TAG, String.format("drawLine(%.2f, %.2f, %.2f, %.2f", start.x, start.y, end.x, end.y));

        canvas.drawLine(start.x, start.y, end.x, end.y, paint);
    }

    private float translateX(float x) {
        return x * mViewMath.getPixelsPerCm();
    }

    private float translateY(float y) {
        return (VIRTUAL_HEIGHT - y) * mViewMath.getPixelsPerCm();
    }

    private PointF translate(float x, float y) {
        float pixelsPerCm = mViewMath.getPixelsPerCm();

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

        //VIRTUAL_HEIGHT = VIRTUAL_WIDTH / RATIO;
        VIRTUAL_HEIGHT = (VIRTUAL_WIDTH / w) * h;

        Log.d(TAG, String.format("VIRTUAL WIDTH/HEIGHT: %.2f / %.2f", VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
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
                mMaxYValue = p.second;
            }
        }

        Log.d(TAG, String.format("Max X: %.2f Min X: %.2f", mMaxXValue, mMinXValue));
        Log.d(TAG, String.format("Max Y: %.2f Min Y: %.2f", mMaxYValue, mMinYValue));

        mNormXAxis = Math.abs(mMaxXValue - mMinXValue);
        mNormYAxis = Math.abs(mMaxYValue - mMinYValue);

        Log.d(TAG, String.format("Nomalized axis %.2f, %.2f", mNormXAxis, mNormYAxis));

        invalidate();

        return idx;
    }
}
