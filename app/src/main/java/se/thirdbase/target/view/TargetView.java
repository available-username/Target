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

    enum Caliber {
        CAL_22,
        CAL_9MM,
        CAL_10MM,
        CAL_45ACP,
        CAL_50
    }

    class BulletHole {
        float x;
        float y;

        public BulletHole(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final float MIN_ZOOM_FACTOR = 1.0f;
    private static final float MAX_ZOOM_FACTOR = 1.3f;

    private static final float VIRTUAL_WIDTH = 60.0f; //cm

    private float mPixelsPerCm;
    private float mCmPerPixel;
    private float mZoomedPixelsPerCm;
    private float mZoomedCmPerPixel;
    private float mScaleFactor = 1.0f;
    private boolean mZoomedIn;
    private Rect mSrcRect;
    private Rect mDstRect;
    private Rect mScaledRect;
    private Bitmap mTargetBitmap;
    private Bitmap mBulletHoleBitmap;
    private Paint mBitmapPaint;
    private State mState = State.INIT;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    private List<BulletHole> mBulletHoles = new ArrayList<>(5);

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
        if (mTargetBitmap != null) {
            //Log.d(TAG, "SCL: " + mScaledRect.toShortString());
            //Log.d(TAG, "DST: " + mDstRect.toShortString());
            Log.d(TAG, String.format("WIDTH/HEIGHT: %d/%d", mSrcRect.width(), mSrcRect.height()));

            canvas.drawBitmap(mTargetBitmap, mScaledRect, mDstRect, null);
        }

        if (true) {
            for (BulletHole hole : mBulletHoles) {
                //float x = hole.x * mPixelsPerCm - mBulletHoleBitmap.getWidth() / 2;
                //float y = hole.y * mPixelsPerCm- mBulletHoleBitmap.getHeight() / 2;
                float x = getPixelCoordinateX(hole.x) - mBulletHoleBitmap.getWidth() / 2;
                float y = getPixelCoordinateY(hole.y) - mBulletHoleBitmap.getHeight() / 2;

                Log.d(TAG, String.format("X/Y: %.2f %.2f", hole.x, hole.y));
                Log.d(TAG, String.format("Draw at %.2f %.2f", x, y));
                canvas.drawBitmap(mBulletHoleBitmap, x, y, null);
            }
        } else{
            if (mBulletHoleBitmap != null) {
                float x = 30f * mZoomedPixelsPerCm / MAX_ZOOM_FACTOR - mBulletHoleBitmap.getWidth() / 2;
                float y = 30f * mZoomedPixelsPerCm / MAX_ZOOM_FACTOR - mBulletHoleBitmap.getHeight() / 2;

                Log.d(TAG, String.format("X/Y: %.2f %.2f",x, y));
                canvas.drawBitmap(mBulletHoleBitmap, x, y, null);

                x = 60f * mZoomedPixelsPerCm / MAX_ZOOM_FACTOR - mBulletHoleBitmap.getWidth() / 2;
                y = 30f * mZoomedPixelsPerCm / MAX_ZOOM_FACTOR - mBulletHoleBitmap.getHeight() / 2;

                Log.d(TAG, String.format("X/Y: %.2f %.2f",x, y));
                canvas.drawBitmap(mBulletHoleBitmap, x, y, null);

            } else {
                Log.d(TAG, "No bullet hole");
            }
        }
    }

    private float getPixelCoordinateX(float val) {
        return (val * mZoomedPixelsPerCm - mScaledRect.left) * mDstRect.width() / mScaledRect.width();
    }

    private float getPixelCoordinateY(float val) {
        return (val * mZoomedPixelsPerCm - mScaledRect.top) * mDstRect.height() / mScaledRect.height();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //Log.d(TAG, String.format("onSizeChanged(%d, %d, %d, %d)", w, h, oldw, oldh));

        //mWidth = w;
        //mHeight = h;

        if (mTargetBitmap == null) {
            int dim = Math.min(w, h);
            //int zoomWidth = (int) (dim * MAX_ZOOM_FACTOR);
            //int zoomHeight = (int) (dim * MAX_ZOOM_FACTOR);
            int zoomWidth = (int) (w * MAX_ZOOM_FACTOR);
            int zoomHeight = (int) (h * MAX_ZOOM_FACTOR);

            mSrcRect = new Rect(0, 0, zoomWidth, zoomHeight);
            mScaledRect = new Rect(0, 0, zoomWidth, zoomHeight);
            //mDstRect = new Rect(0, 0, dim, dim);
            mDstRect = new Rect(0, 0, w, h);

            //mPixelsPerCm = dim / VIRTUAL_WIDTH;
            mPixelsPerCm = w / VIRTUAL_WIDTH;
            mCmPerPixel = 1 / mZoomedPixelsPerCm;
            //mZoomedPixelsPerCm = Math.min(zoomWidth, zoomHeight) / VIRTUAL_WIDTH;
            mZoomedPixelsPerCm = zoomWidth / VIRTUAL_WIDTH;
            mZoomedCmPerPixel = 1 / mZoomedPixelsPerCm;

            Log.d(TAG, String.format("DIM: %d, pc/cm: %.2f & %.2f", dim, mZoomedPixelsPerCm, mPixelsPerCm));
        }

        new BitmapCreator().execute();
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

            if (mBulletHoles.size() < 5) {
                float x = event.getX();
                float y = event.getY();

                // Find out where we are in the source rectangle
                x = mScaledRect.left + mScaledRect.width() * x / mDstRect.width();
                y = mScaledRect.top + mScaledRect.height() * y / mDstRect.height();

                BulletHole hole = new BulletHole(x / mZoomedPixelsPerCm, y / mZoomedPixelsPerCm);
                Log.d(TAG, String.format("Hole: %.2f %.2f", hole.x, hole.y));
                mBulletHoles.add(hole);
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            Log.d(TAG, "onSingleTapConfirmed");
            float x = event.getX();
            float y = event.getY();

            return true;
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

            //Log.d(TAG, String.format("%.2f %.2f", (mCenterX / mDstRect.right), (mCenterY / mDstRect.bottom)));

            int centerX = (int)((mCenterX / mDstRect.right) * mSrcRect.right);
            int centerY = (int)((mCenterY / mDstRect.bottom) * mSrcRect.bottom);

            int width = (int) ((mSrcRect.right - mSrcRect.left) / MAX_ZOOM_FACTOR);
            int height = (int) ((mSrcRect.bottom - mSrcRect.top) / MAX_ZOOM_FACTOR);

            mScaledRect.left = clamp(centerX - width / 2, 0, mSrcRect.right - width);
            mScaledRect.top = clamp(centerY - height / 2, 0, mSrcRect.bottom - height);
            mScaledRect.right = mScaledRect.left + width;
            mScaledRect.bottom = mScaledRect.top + height;

            mZoomedIn = true;
        }

        private void zoomOut(MotionEvent event) {
            mScaledRect.top = mSrcRect.top;
            mScaledRect.left = mSrcRect.left;
            mScaledRect.bottom = mSrcRect.bottom;
            mScaledRect.right = mSrcRect.right;

            mZoomedIn = false;
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

    class BitmapCreator extends AsyncTask<Void, Void, Void> {

        private int dim;
        private int width;
        private int height;

        @Override
        protected Void doInBackground(Void... params) {
            dim = Math.min(mSrcRect.width(), mSrcRect.height());
            width = mSrcRect.width();
            height = mSrcRect.height();
            createTarget();
            createBulletHole();

            return null;
        }

        private void createTarget() {
            float radiusIncrement = 2.5f; // cm
            float textSize = mZoomedPixelsPerCm * radiusIncrement * 0.75f;
            float textHeightOffset = textSize / 2;
            float textWidthOffset = (mZoomedPixelsPerCm * radiusIncrement) / 2;
            //float cx = dim / 2;
            //float cy = dim / 2;
            float cx = width / 2;
            float cy = height / 2;

            //mTargetBitmap = Bitmap.createBitmap(dim, dim, Bitmap.Config.RGB_565);
            mTargetBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(mTargetBitmap);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            //canvas.drawRect(0, 0, dim, dim, paint);
            canvas.drawRect(0, 0, width, height, paint);

            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);

            int ring;
            for (ring = 10; ring > 4; ring--) {
                canvas.drawCircle(cx, cy, ring * radiusIncrement * mZoomedPixelsPerCm, paint);
            }

            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(cx, cy, ring * radiusIncrement * mZoomedPixelsPerCm, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);

            for (; ring > 0; ring--) {
                canvas.drawCircle(cx, cy, ring * radiusIncrement * mZoomedPixelsPerCm, paint);
            }

            // finally, the inner ring
            canvas.drawCircle(cx, cy, 1.25f * mZoomedPixelsPerCm, paint);


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

                    float r = mZoomedPixelsPerCm * radiusIncrement * num;
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

        private void createBulletHole() {
            float bulletDiameter = CM_PER_INCH * 0.22f * mZoomedPixelsPerCm;
            int bmDim = (int)Math.ceil(bulletDiameter);
            float cx = ((float)bmDim) / 2;
            float cy = ((float)bmDim) / 2;

            mBulletHoleBitmap = Bitmap.createBitmap(bmDim, bmDim, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(mBulletHoleBitmap);

            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            float radius = bulletDiameter / 2;
            canvas.drawCircle(cx, cy, radius, paint);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            invalidate();
        }
    }
}
