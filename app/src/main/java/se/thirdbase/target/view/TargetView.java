package se.thirdbase.target.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by alexp on 2/8/16.
 */
public class TargetView extends View {

    private static final String TAG = TargetView.class.getSimpleName();

    enum State {
        INIT("INIT"),
        OVERVIEW("OVERVIEW"),
        ZOOM("ZOOM");

        private String mName;

        State(String name) {
            mName = name;
        }

        public String toString() {
            return mName;
        }
    }

    class Point {
        float x;
        float y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final float MIN_ZOOM_FACTOR = 1.0f;
    private static final float MAX_ZOOM_FACTOR = 3.0f;

    private int mWidth;
    private int mHeight;
    private float mScaleFactor = 1.0f;
    private Rect mSrcRect;
    private Rect mDstRect;
    private Rect mScaledRect;
    private Bitmap mOverViewBitmap;
    private Bitmap mBigBitmap;
    private Paint mBitmapPaint;
    private State mState = State.INIT;
    private ScaleGestureDetector mScaleGestureDetector;


    public TargetView(Context context) {
        super(context);

        mScaleGestureDetector = new ScaleGestureDetector(context, mOnScaleGestureListener);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        //setOnClickListener(mOnClickListener);
        setOnTouchListener(mOnTouchListener);
    }

    @Override
    public void onDraw(Canvas canvas) {
        switch (mState) {
            case INIT:
            case OVERVIEW:
            case ZOOM:
                drawZoomview(canvas);
                break;
        }
    }

    private void transition(State nextState) {

        //Log.d(TAG, String.format("transition() %s -> %s", mState, nextState));

        switch (mState) {
            case INIT:
            case OVERVIEW:
                mState = nextState;
                break;
            case ZOOM:
                mState = nextState;
                break;
        }
    }

    private void drawOverview(Canvas canvas) {
        if (mOverViewBitmap != null) {
            //Log.d(TAG, "drawOverview()");
            canvas.drawBitmap(mOverViewBitmap, 0, 0, mBitmapPaint);
        }
    }

    private void drawZoomview(Canvas canvas) {
        if (mBigBitmap != null) {
            if (mBigBitmap != null) {
                Log.d(TAG, "SCL: " + mScaledRect.toShortString());
                Log.d(TAG, "DST: " + mDstRect.toShortString());
                canvas.drawBitmap(mBigBitmap, mScaledRect, mDstRect, null);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //Log.d(TAG, String.format("onSizeChanged(%d, %d, %d, %d)", w, h, oldw, oldh));

        mWidth = w;
        mHeight = h;

        if (mBigBitmap == null) {
            int zoomWidth = (int) (w * MAX_ZOOM_FACTOR);
            int zoomHeight = (int) (h * MAX_ZOOM_FACTOR);

            mSrcRect = new Rect(0, 0, zoomWidth, zoomHeight);
            mScaledRect = new Rect(0, 0, zoomWidth, zoomHeight);
            mDstRect = new Rect(0, 0, w, h);

            mBigBitmap = Bitmap.createBitmap(zoomWidth, zoomHeight, Bitmap.Config.ARGB_8888);
        }

        new CreateTarget().execute(mBigBitmap);
    }

    class CreateTarget extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... params) {
            int minDim = (int) MAX_ZOOM_FACTOR * Math.min(mWidth, mHeight);

            float pixelsPerCm = minDim / 60;
            float maxRadius = 30f; // cm
            float radiusIncrement = 2.5f; // cm
            //float pixelsPerCm = minDim / (2 * maxRadius);
            float textSize = pixelsPerCm * radiusIncrement / 2;
            float textHeightOffset = textSize / 2;
            float textWidthOffset = (pixelsPerCm * radiusIncrement) / 2;
            float cx = minDim / 2;
            float cy = minDim / 2;

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);

            Canvas canvas = new Canvas(params[0]);

            int ring;
            for (ring = 10; ring > 4; ring--) {
                canvas.drawCircle(cx, cy, ring * radiusIncrement * pixelsPerCm, paint);
            }

            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(cx, cy, ring * radiusIncrement * pixelsPerCm, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);

            for (; ring > 0; ring--) {
                canvas.drawCircle(cx, cy, ring * radiusIncrement * pixelsPerCm, paint);
            }

            // finally, the inner ring
            canvas.drawCircle(cx, cy, 1.25f * pixelsPerCm, paint);


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
                    float x = cx + (float) (r * Math.cos(angle));
                    float y = cy + (float) (r * Math.sin(angle));

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

            mOverViewBitmap = Bitmap.createScaledBitmap(mBigBitmap, mWidth, mHeight, true);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            invalidate();
        }
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //Log.d(TAG, "onTouch()");

            int action = event.getAction();
            final float x = event.getX();
            final float y = event.getY();

            switch (mState) {
                case INIT:
                case OVERVIEW:
                case ZOOM:
                    mScaleGestureDetector.onTouchEvent(event);
                    break;
            }

            invalidate();
            return true;
        }
    };

    private ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            mScaleFactor = Math.max(MIN_ZOOM_FACTOR, Math.min(mScaleFactor, MAX_ZOOM_FACTOR));
            Log.d(TAG, "mScaleFactor: " + mScaleFactor);


            int width = (int)((mSrcRect.right - mSrcRect.left) / mScaleFactor);
            int height = (int)((mSrcRect.bottom - mSrcRect.top) / mScaleFactor);
            mScaledRect.left = (mSrcRect.right - width) / 2;
            mScaledRect.top = (mSrcRect.bottom - height) / 2;
            mScaledRect.right = mScaledRect.left + width;
            mScaledRect.bottom = mScaledRect.top + height;

            invalidate();
            return true;
        }
    };
}
