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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import se.thirdbase.target.util.ViewMath;

/**
 * Created by alexp on 2/19/16.
 */
public class GraphView extends View {

    private static final String TAG = GraphView.class.getSimpleName();

    public interface Filter {
        boolean keep(Pair<Float, Float> data);
    }

    private enum ViewState {
        OVERVIEW,
        ZOOM
    }

    private static final String XMLNS = "http://kalle.arne.se/target";
    private static final String XMLSTYLE = "style";
    private static final String XMLSTYLE_LINES = "lines";
    private static final String XMLSTYLE_THICK_BARS = "thick_bars";
    private static final String XMLSTYLE_SPARSE_BARS = "sparse_bars";


    private enum Style {
        LINES(XMLSTYLE_LINES),
        THICK_BARS(XMLSTYLE_THICK_BARS),
        SPARSE_BARS(XMLSTYLE_SPARSE_BARS);

        private String mStr;

        Style(String str) {
            mStr = str;
        }

        public boolean equals(String value) {
            return mStr.equals(value);
        }
    }

    private static final float VIRTUAL_WIDTH = 110;
    private static float VIRTUAL_HEIGHT;
    private static final float MAX_ZOOM_FACTOR = 3f;

    private static float XMARGIN = 5;
    private static float YMARGIN = 5;

    private Rect mRect = new Rect();
    private Map<Integer, List<Pair<Float,Float>>> mDataMap = new HashMap<>();
    private Map<Integer, List<Pair<Float, Float>>> mNormalizedDataMap = new HashMap<>();
    private AtomicInteger mMapIndexer = new AtomicInteger();

    private ViewMath mViewMath;
    private ViewState mViewState = ViewState.OVERVIEW;

    private float mMinXValue = Float.MAX_VALUE;
    private float mMaxXValue = Float.MIN_VALUE;
    private float mMinYValue = Float.MAX_VALUE;
    private float mMaxYValue = Float.MIN_VALUE;
    private float mNormXAxis;
    private float mNormYAxis;

    private Map<Integer, Filter> mFilters = new HashMap<>();
    private Style mStyle = Style.LINES; // The default style

    private GestureDetector mGestureDetector;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);

        mGestureDetector = new GestureDetector(context, mSimpleGestureDetector);
        setOnTouchListener(mOnTouchListener);

        //createSampleData2();
    }

    public GraphView(Context context) {
        this(context, null);
    }

    private void createSampleData1() {
        List data = new ArrayList();

        Log.d(TAG, "createSampleData1()");

        float maxTime = (float)Math.PI * 2 * 6;

        int nSamples = 1000;
        for (int i = 0; i < nSamples; i++) {
            float t = i * maxTime / nSamples;
            Pair pair = new Pair(t, (float)Math.sin(t) + 1);
            data.add(pair);
        }

        addDataPoints(data);
    }

    private void createSampleData2() {
        List<Pair<Float, Float>> data = new ArrayList<>();

        Random rand = new Random();

        for (int i = 0; i < 10; i++) {
            Pair<Float, Float> point = new Pair((float)(i + 1), (float)rand.nextInt(10));
            //Pair<Float, Float> point = new Pair((float)(i + 1), 10f);
            data.add(point);
        }

        addDataPoints(data);
    }

    private void normalizeData() {
        mNormalizedDataMap = new HashMap<>();

        Log.d(TAG, "normalizeData()");

        StringBuilder points = new StringBuilder();

        for (Integer key : mDataMap.keySet()) {

            List<Pair<Float, Float>> data = mDataMap.get(key);

            List<Pair<Float, Float>> norm = new ArrayList<>();

            Filter filter = mFilters.get(key);

            for (Pair<Float, Float> p : data) {

                if (filter != null && !filter.keep(p)) {
                    continue;
                }

                float normX = (VIRTUAL_WIDTH - 2 * XMARGIN) * p.first / mNormXAxis;
                float normY = (VIRTUAL_HEIGHT - 2 * YMARGIN) * p.second / mNormYAxis;
                Pair<Float, Float> q = new Pair<>(normX, normY);
                norm.add(q);

                points.append(String.format("(%.2f, %.2f) ", q.first, q.second));
            }

            Log.d(TAG, points.toString());

            mNormalizedDataMap.put(key, norm);
        }

        Log.d(TAG, "Normalized data set size: " + mNormalizedDataMap.size());
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "onDraw()");

        normalizeData();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);

        Paint fill = new Paint();
        fill.setColor(0xffff9012);
        fill.setStyle(Paint.Style.FILL_AND_STROKE);
        Paint stroke = new Paint();
        stroke.setColor(Color.BLACK);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(1);

        // Draw level indicators
        drawLevelIndicators(canvas);

        for (Integer key : mNormalizedDataMap.keySet()) {
            List<Pair<Float, Float>> data = mNormalizedDataMap.get(key);

            //drawData(canvas, data, paint);
            //drawThickBars(canvas, data, fill, stroke);
            drawSparseBars(canvas, data, fill, stroke);
        }

        // Draw axis
        drawAxis(canvas);
    }

    private void drawLevelIndicators(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);


        for (int i = 1; i < NBR_BAR_LEVES; i++) {
            float y = (VIRTUAL_HEIGHT - 2 * YMARGIN) * i / NBR_BAR_LEVES;
            drawLine(canvas, XMARGIN, y + YMARGIN, VIRTUAL_WIDTH - XMARGIN, y + YMARGIN, paint);
        }
    }

    private void drawAxis(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);

        drawLine(canvas, XMARGIN, YMARGIN, VIRTUAL_WIDTH - XMARGIN, YMARGIN, paint);
        drawLine(canvas, XMARGIN, YMARGIN, XMARGIN, VIRTUAL_HEIGHT - YMARGIN, paint);
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

    private void drawThickBars(Canvas canvas, List<Pair<Float, Float>> data, Paint fill, Paint stroke) {
        Log.d(TAG, "drawThickBars()");

        int size = data.size();

        float barWidth = (VIRTUAL_WIDTH - 2 * XMARGIN) / size;

        for (int i = 0; i < size; i++) {
            Pair<Float, Float> pair = data.get(i);

            PointF topLeft = translate(i * barWidth + XMARGIN, pair.second);
            PointF rightBottom = translate(((i + 1) * barWidth + XMARGIN), YMARGIN);

            canvas.drawRect(topLeft.x, topLeft.y, rightBottom.x, rightBottom.y, fill);
            canvas.drawRect(topLeft.x, topLeft.y, rightBottom.x, rightBottom.y, stroke);
        }
    }

    private static final int NBR_BAR_LEVES = 10;

    private void drawSparseBars(Canvas canvas, List<Pair<Float, Float>> data, Paint fill, Paint stroke) {
        Log.d(TAG, "drawSparseBars()");

        int size = data.size();
        float barWidth = (VIRTUAL_WIDTH - 2 * XMARGIN) / size;
        float barSpace = barWidth / 4;

        for (int i = 0; i < size; i++) {
            Pair<Float, Float> pair = data.get(i);

            PointF topLeft = translate(i * barWidth + XMARGIN + barSpace, pair.second);
            PointF rightBottom = translate(((i + 1) * barWidth + XMARGIN - barSpace), YMARGIN);

            canvas.drawRect(topLeft.x, topLeft.y, rightBottom.x, rightBottom.y, fill);
            canvas.drawRect(topLeft.x, topLeft.y, rightBottom.x, rightBottom.y, stroke);
        }
    }

    private void drawRect(Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
        PointF leftTop = translate(left, top);
        PointF rightBottom = translate(right, bottom);

        //Log.d(TAG, String.format("drawRect(%.2f, %.2f, %.2f, %.2f)", leftTop.x, leftTop.y, rightBottom.x, rightBottom.y));

        canvas.drawRect(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, paint);
    }

    private void drawLine(Canvas canvas, float startX, float startY, float endX, float endY, Paint paint) {
        PointF start = translate(startX, startY);
        PointF end = translate(endX, endY);

        //Log.d(TAG, String.format("drawLine(%.2f, %.2f, %.2f, %.2f", start.x, start.y, end.x, end.y));

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
        Rect rect = mViewMath.getScaledRect();

        PointF point = new PointF();
        point.x = x * pixelsPerCm - rect.left;
        point.y = (VIRTUAL_HEIGHT - y) * pixelsPerCm - rect.top;

        return point;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d(TAG, String.format("onSizeChanged(%d, %d, %d, %d)", w, h, oldw, oldh));
        mRect = new Rect(0, 0, w, h);

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

        mNormXAxis = Math.abs(Math.max(0, mMaxXValue) - Math.min(0, mMinXValue));
        mNormYAxis = Math.abs(Math.max(0, mMaxYValue) - Math.min(0, mMinYValue));

        Log.d(TAG, String.format("Nomalized axis %.2f, %.2f", mNormXAxis, mNormYAxis));

        invalidate();

        return idx;
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
            return true;
        }
    };

    public void addFilter(int dataSet, Filter filter) {
        mFilters.put(dataSet, filter);
    }

    public void removeFilter(int dataSet) {
        mFilters.remove(dataSet);
    }

    private GestureDetector.SimpleOnGestureListener mSimpleGestureDetector = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(TAG, "onDoubleTap()");

            switch (mViewState) {
                case OVERVIEW:
                    mViewMath.zoomIn(event.getX(), event.getY());
                    mViewState = ViewState.ZOOM;
                    break;
                case ZOOM:
                    mViewMath.zoomOut();
                    mViewState = ViewState.OVERVIEW;
                    break;
            }

            invalidate();

            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            updateTarget(distanceX, distanceY);

            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        private void updateTarget(float distanceX, float distanceY) {
            if (mViewState == ViewState.ZOOM) {
                mViewMath.translate(distanceX, distanceY);
            }
        }
    };
}
