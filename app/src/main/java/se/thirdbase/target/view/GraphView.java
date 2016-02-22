package se.thirdbase.target.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by alexp on 2/19/16.
 */
public class GraphView<F,S> extends View {

    private static final String TAG = GraphView.class.getSimpleName();

    private Rect mRect = new Rect();
    private Map<Integer, List<Pair<F,S>>> mDataMap = new HashMap<>();
    private AtomicInteger mMapIndexer = new AtomicInteger();

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphView(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //detde
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d(TAG, String.format("onSizeChanged(%d, %d, %d, %d)", w, h, oldw, oldh));
        mRect = new Rect(0, 0, w, h);
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

    public int addDataPoints(List<Pair<F,S>> dataPoints) {
        int idx = mMapIndexer.incrementAndGet();

        mDataMap.put(idx, dataPoints);

        invalidate();

        return idx;
    }
}
