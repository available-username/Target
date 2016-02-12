package se.thirdbase.target.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexp on 2/8/16.
 */
public class TargetView extends View {

    private static final String TAG = TargetView.class.getSimpleName();

    private static final float CM_PER_INCH = 2.54f;

    private static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    enum State {
        OVERVIEW("OVERVIEW"),
        ZOOM("ZOOM"),
        PLACE_BULLET("PLACE_BULLET");

        private String mName;

        State(String name) {
            mName = name;
        }

        public String toString() {
            return mName;
        }
    }

    enum Caliber {
        CAL_22,
        CAL_9MM,
        CAL_10MM,
        CAL_45ACP,
        CAL_50
    }

    class BulletHole {
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

    private static final float MIN_ZOOM_FACTOR = 1.0f;
    private static final float MAX_ZOOM_FACTOR = 5.0f;

    private static final float VIRTUAL_WIDTH = 60.0f; //cm

    private float mPixelsPerCm;
    private float mScaleFactor = 1.0f;
    private float mZoomLevel = MIN_ZOOM_FACTOR;
    private Rect mSrcRect;
    private Rect mDstRect;
    private Rect mScaledRect;
    private State mState = State.OVERVIEW;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    private float mCurrentX;
    private float mCurrentY;

    private List<BulletHole> mBulletHoles = new ArrayList<>(5);

    public TargetView(Context context) {
        super(context);

        mScaleGestureDetector = new ScaleGestureDetector(context, mOnScaleGestureListener);
        mGestureDetector = new GestureDetector(context, mSimpleGestureDetector);
        setOnTouchListener(mOnTouchListener);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawTarget(canvas);
        drawBullets(canvas);

        switch (mState) {
            case OVERVIEW:
                break;
            case ZOOM:
                break;
            case PLACE_BULLET:
                drawBulletPlacer(canvas);
                postInvalidateDelayed(1000 / 25);
                break;
            default:
                break;
        }
    }

    private void transition(State nextState) {

        String transition = String.format("%s -> %s", mState, nextState);

        switch (mState) {
            case OVERVIEW:

                switch (nextState) {
                    case PLACE_BULLET:
                    case OVERVIEW: throw new IllegalStateException("Illegal transition: " + transition);
                    case ZOOM: onEnterZoom(); break;
                }

                break;
            case ZOOM:

                switch (nextState) {
                    case OVERVIEW: onEnterOverview(); break;
                    case ZOOM: throw new IllegalStateException("Illegal transition: " + transition);
                    case PLACE_BULLET: onPlaceBullet(); break;
                }
                break;

            case PLACE_BULLET:
                switch (nextState) {
                    case PLACE_BULLET:
                    case OVERVIEW: throw new IllegalStateException("Illegal transition: " + transition);
                    case ZOOM: onEnterZoom(); break;
                }
                break;
        }

        Log.d(TAG, "Transition: " + transition);
    }

    private void onEnterOverview() {
        mZoomLevel = MIN_ZOOM_FACTOR;
        mState = State.OVERVIEW;
    }

    private void onEnterZoom() {
        mZoomLevel = MAX_ZOOM_FACTOR;
        mState = State.ZOOM;
    }

    private void onPlaceBullet() {
        mState = State.PLACE_BULLET;
    }

    private void drawBulletPlacer(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);

        paint.setColor(Color.GRAY);

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int offset = Math.max(width, height) / 5;
        float zoomedPixelsPerCm = mZoomLevel * mPixelsPerCm;
        float bulletDiameter = CM_PER_INCH * 0.22f * zoomedPixelsPerCm;
        float radius = bulletDiameter / 2;

        Log.d(TAG, "drawBulletPlacer");
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(mCurrentX - offset, mCurrentY - offset, radius, paint);
    }

    private void drawBullets(Canvas canvas) {
        float pixelsPerCm = mZoomLevel * mPixelsPerCm;
        float bulletDiameter = CM_PER_INCH * 0.22f * pixelsPerCm;
        float radius = bulletDiameter / 2;
        int dim = (int)Math.ceil(bulletDiameter);

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        for (BulletHole hole : mBulletHoles) {
            //float x = getPixelCoordinateX(hole.x) - dim / 2;
            //float y = getPixelCoordinateY(hole.y) - dim / 2;

            PointF p = hole.toPixelLocation(pixelsPerCm);

            canvas.drawCircle(p.x - mScaledRect.left, p.y - mScaledRect.top, radius, paint);
        }
    }

    private float getPixelCoordinateX(float val) {
        float zoomedPixelsPerCm = mZoomLevel * mPixelsPerCm;
        //return (val * zoomedPixelsPerCm - mScaledRect.left) * mDstRect.width() / mScaledRect.width();
        return (val * zoomedPixelsPerCm - mScaledRect.left);
    }

    private float getPixelCoordinateY(float val) {
        float zoomedPixelsPerCm = mZoomLevel * mPixelsPerCm;
        //return (val * zoomedPixelsPerCm - mScaledRect.top) * mDstRect.height() / mScaledRect.height();
        return (val * zoomedPixelsPerCm - mScaledRect.top);
    }


    private void drawTarget(Canvas canvas) {
        float pixelsPerCm = mZoomLevel * mPixelsPerCm;
        Log.d(TAG, "ZoomLevel: " + mZoomLevel);

        float radiusIncrement = 2.5f; // cm
        float textSize = pixelsPerCm * radiusIncrement * 0.75f;
        float textHeightOffset = textSize / 2;
        float textWidthOffset = (pixelsPerCm * radiusIncrement) / 2;

        float cx = mScaledRect.width() * mZoomLevel / 2 - mScaledRect.left;
        float cy = mScaledRect.height() * mZoomLevel  / 2 - mScaledRect.top;

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawColor(Color.WHITE);

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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int zoomWidth = (int) (w * MAX_ZOOM_FACTOR);
        int zoomHeight = (int) (h * MAX_ZOOM_FACTOR);

        mSrcRect = new Rect(0, 0, zoomWidth, zoomHeight);
        mScaledRect = new Rect(0, 0, w, h);
        mDstRect = new Rect(0, 0, w, h);

        mPixelsPerCm = w / VIRTUAL_WIDTH;
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int action = event.getAction();
            mCurrentX = event.getX();
            mCurrentY = event.getY();

            Log.d(TAG, String.format("%.2f, %.2f", mCurrentX, mCurrentY));

            switch (mState) {
                case OVERVIEW:
                case ZOOM:
                    mGestureDetector.onTouchEvent(event);
                    break;
                case PLACE_BULLET:
                    //mScaleGestureDetector.onTouchEvent(event);
                    invalidate();
                    //mGestureDetector.onTouchEvent(event);

                    if (action == MotionEvent.ACTION_UP) {
                        // Commit bullet
                        transition(State.ZOOM);
                    }
                    break;
            }

            return true;
        }
    };

    private GestureDetector.SimpleOnGestureListener mSimpleGestureDetector= new GestureDetector.SimpleOnGestureListener() {

        private float mCenterX;
        private float mCenterY;

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            switch (mState) {
                case OVERVIEW:
                    zoomIn(event);
                    transition(State.ZOOM);
                    invalidate();
                    break;
                case ZOOM:
                    zoomOut(event);
                    transition(State.OVERVIEW);
                    invalidate();
                    break;
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Log.d(TAG, "onLongPress");


            if (mState == State.ZOOM) {
                if (mBulletHoles.size() < 5) {
                    float zoomedPixelsPerCm = mZoomLevel * mPixelsPerCm;
                    float x = event.getX();
                    float y = event.getY();

                    // Find out where we are in the source rectangle
                    x = mScaledRect.left + mScaledRect.width() * x / mDstRect.width();
                    y = mScaledRect.top + mScaledRect.height() * y / mDstRect.height();

                    BulletHole hole = new BulletHole(x / zoomedPixelsPerCm, y / zoomedPixelsPerCm);
                    //Log.d(TAG, String.format("Hole: %.2f %.2f", hole.x, hole.y));
                    mBulletHoles.add(hole);

                    invalidate();
                }
            }
            /*
            if (mState == State.ZOOM) {
                transition(State.PLACE_BULLET);
            }
            */
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            Log.d(TAG, "onSingleTapConfirmed");

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll()");
            if (mState == State.ZOOM) {

                int width = mDstRect.width();
                int height = mDstRect.height();
                mScaledRect.left = (int) clamp(mScaledRect.left + distanceX, 0, mSrcRect.right - width);
                mScaledRect.top = (int) clamp(mScaledRect.top + distanceY, 0, mSrcRect.bottom - height);
                mScaledRect.right = mScaledRect.left + width;
                mScaledRect.bottom = mScaledRect.top + height;
            } else if(mState == State.PLACE_BULLET) {

            }

            invalidate();
            return true;
        }

        private void zoomIn(MotionEvent event) {
            mCenterX = event.getX();
            mCenterY = event.getY();

            int centerX = (int)((mCenterX / mDstRect.right) * mSrcRect.right);
            int centerY = (int)((mCenterY / mDstRect.bottom) * mSrcRect.bottom);

            int width = (int) ((mSrcRect.right - mSrcRect.left) / MAX_ZOOM_FACTOR);
            int height = (int) ((mSrcRect.bottom - mSrcRect.top) / MAX_ZOOM_FACTOR);

            mScaledRect.left = clamp(centerX - width / 2, 0, mSrcRect.right - width);
            mScaledRect.top = clamp(centerY - height / 2, 0, mSrcRect.bottom - height);
            mScaledRect.right = mScaledRect.left + mDstRect.width();
            mScaledRect.bottom = mScaledRect.top + mDstRect.height();
        }

        private void zoomOut(MotionEvent event) {
            mScaledRect.top = mDstRect.top;
            mScaledRect.left = mDstRect.left;
            mScaledRect.bottom = mDstRect.bottom;
            mScaledRect.right = mDstRect.right;
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

            mScaledRect.left = clamp(centerX - width / 2, 0, mSrcRect.right - width);
            mScaledRect.top = clamp(centerY - height / 2, 0, mSrcRect.bottom - height);
            mScaledRect.right = mScaledRect.left + width;
            mScaledRect.bottom = mScaledRect.top + height;

            Log.d(TAG, String.format("Aspect: %.2f, factor: %.2f", ((float) width) / height, mScaleFactor));
            Log.d(TAG, String.format("%4d %4d %4d %4d", mScaledRect.left, mScaledRect.top, width, height));

            invalidate();

            return true;
        }
    };
}
