package se.thirdbase.target.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by alexp on 2/8/16.
 */
public class TargetView extends View {

    private static final String TAG = TargetView.class.getSimpleName();

    private static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

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
    private Bitmap mBigBitmap;
    private Bitmap mBulletHoleBitmap;
    private Paint mBitmapPaint;
    private State mState = State.INIT;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;


    public TargetView(Context context) {
        super(context);

        mScaleGestureDetector = new ScaleGestureDetector(context, mOnScaleGestureListener);
        mGestureDetector = new GestureDetector(context, mSimpleGestureDetector);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(false);
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

    private void drawZoomview(Canvas canvas) {
        if (mBigBitmap != null) {
            if (mBigBitmap != null) {
                //Log.d(TAG, "SCL: " + mScaledRect.toShortString());
                //Log.d(TAG, "DST: " + mDstRect.toShortString());
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
            int dim = Math.min(w, h);
            int zoomWidth = (int) (dim * MAX_ZOOM_FACTOR);
            int zoomHeight = (int) (dim * MAX_ZOOM_FACTOR);

            mSrcRect = new Rect(0, 0, zoomWidth, zoomHeight);
            mScaledRect = new Rect(0, 0, zoomWidth, zoomHeight);
            mDstRect = new Rect(0, 0, dim, dim);

            mBigBitmap = Bitmap.createBitmap(zoomWidth, zoomHeight, Bitmap.Config.RGB_565);
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

            Canvas canvas = new Canvas(params[0]);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawRect(0, 0, minDim, minDim, paint);

            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);

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
                    //mScaleGestureDetector.onTouchEvent(event);
                    mGestureDetector.onTouchEvent(event);
                    break;
            }

            invalidate();
            return true;
        }
    };

    private GestureDetector.SimpleOnGestureListener mSimpleGestureDetector= new GestureDetector.SimpleOnGestureListener() {

        private float mCenterX;
        private float mCenterY;

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(TAG, "onDoubleTap");

            switch (mState) {
                case INIT:
                case OVERVIEW:
                    zoomIn(event);
                    transition(State.ZOOM);
                    break;
                case ZOOM:
                    zoomOut(event);
                    transition(State.OVERVIEW);
                    break;
            }

            invalidate();

            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Log.d(TAG, "onLongPress");
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mState == State.ZOOM) {
                Log.d(TAG, String.format("%.2f %.2f", distanceX, distanceY));

                int width = (int) ((mSrcRect.right - mSrcRect.left) / MAX_ZOOM_FACTOR);
                int height = (int) ((mSrcRect.bottom - mSrcRect.top) / MAX_ZOOM_FACTOR);
                //mScaledRect.left = (int)Math.max(0, mScaledRect.left + distanceX);
                //mScaledRect.top = (int)Math.max(0, mScaledRect.top + distanceY);
                mScaledRect.left = (int)Math.max(0, Math.min(mSrcRect.right - width, mScaledRect.left + distanceX));
                mScaledRect.top = (int)Math.max(0, Math.min(mSrcRect.bottom - height, mScaledRect.top + distanceY));
                mScaledRect.right = mScaledRect.left + width;
                mScaledRect.bottom = mScaledRect.top + height;
            }

            return true;
        }

        private void zoomIn(MotionEvent event) {
            mCenterX = event.getX();
            mCenterY = event.getY();

            Log.d(TAG, String.format("%.2f %.2f", (mCenterX / mDstRect.right), (mCenterY / mDstRect.bottom)));

            int centerX = (int)((mCenterX / mDstRect.right) * mSrcRect.right);
            int centerY = (int)((mCenterY / mDstRect.bottom) * mSrcRect.bottom);

            int width = (int) ((mSrcRect.right - mSrcRect.left) / MAX_ZOOM_FACTOR);
            int height = (int) ((mSrcRect.bottom - mSrcRect.top) / MAX_ZOOM_FACTOR);

            mScaledRect.left = centerX - width / 2;
            mScaledRect.top = centerY - height / 2;
            mScaledRect.right = mScaledRect.left + width;
            mScaledRect.bottom = mScaledRect.top + height;
        }

        private void zoomOut(MotionEvent event) {
            mScaledRect.top = mSrcRect.top;
            mScaledRect.left = mSrcRect.left;
            mScaledRect.bottom = mSrcRect.bottom;
            mScaledRect.right = mSrcRect.right;
        }
    };


    private ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private int centerX;
        private int centerY;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            float x = detector.getFocusX();
            float y = detector.getFocusY();

            //centerX = (int)((x / mDstRect.right) * mSrcRect.right);
            //centerY = (int)((y / mDstRect.bottom) * mSrcRect.bottom);

            centerX = (int)((x / mDstRect.right) * mScaledRect.right);
            centerY = (int)((y / mDstRect.bottom) * mScaledRect.bottom);

            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            mScaleFactor = Math.max(MIN_ZOOM_FACTOR, Math.min(mScaleFactor, MAX_ZOOM_FACTOR));
            //Log.d(TAG, String.format("mScaleFactor: %.1f, (X/Y): %.1f %.1f", mScaleFactor, x, y));

            int width = (int) ((mSrcRect.right - mSrcRect.left) / mScaleFactor);
            int height = (int) ((mSrcRect.bottom - mSrcRect.top) / mScaleFactor);

            mScaledRect.left = centerX - width / 2;
            mScaledRect.top = centerY - height / 2;
            mScaledRect.right = mScaledRect.left + width;
            mScaledRect.bottom = mScaledRect.top + height;

            Log.d(TAG, String.format("Aspect: %.2f, factor: %.2f", ((float) width) / height, mScaleFactor));
            Log.d(TAG, String.format("%4d %4d %4d %4d", mScaledRect.left, mScaledRect.top, width, height));

            invalidate();

            return true;
        }
    };
}
