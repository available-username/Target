package se.thirdbase.target.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.model.BulletCaliber;
import se.thirdbase.target.model.BulletHole;

/**
 * Created by alexp on 2/8/16.
 */
public class TargetView extends View {

    public interface ZoomChangeListener {
        void onZoomIn();

        void onZoomOut();
    }

    public interface ActionListener {
        void onIdle();

        void onAdd();

        void onRelocate();
    }

    private static final String TAG = TargetView.class.getSimpleName();

    private static final float CM_PER_INCH = 2.54f;

    private static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    enum ActionState {
        IDLE,
        ADD,
        RELOCATE
    }

    enum ViewState {
        OVERVIEW,
        ZOOM
    }

    private static final float MIN_ZOOM_FACTOR = 1.0f;
    private static final float MAX_ZOOM_FACTOR = 5.0f;
    private static final int MAX_NBR_BULLETS = 5;
    private static final float VIRTUAL_WIDTH = 60.0f; //cm
    private static float VIRTUAL_HEIGHT;

    private float mPixelsPerCm;
    private float mZoomLevel = MIN_ZOOM_FACTOR;
    private Rect mSrcRect;
    private Rect mDstRect;
    private Rect mScaledRect;

    private ActionState mActionState = ActionState.IDLE;
    private ViewState mViewState = ViewState.OVERVIEW;
    private GestureDetector mGestureDetector;

    private List<BulletHole> mBulletHoles = new ArrayList<>();
    private BulletHole mActiveBulletHole;
    private int mActiveBulletIdx = Integer.MIN_VALUE;

    private ZoomChangeListener mZoomChangeListener;
    private ActionListener mActionListener;

    private int mMaxNbrBullets = MAX_NBR_BULLETS;

    public TargetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGestureDetector = new GestureDetector(context, mSimpleGestureDetector);
        setOnTouchListener(mOnTouchListener);
    }

    public TargetView(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, mSimpleGestureDetector);
        setOnTouchListener(mOnTouchListener);
    }

    public void setZoomChangedListener(ZoomChangeListener listener) {
        mZoomChangeListener = listener;
    }

    public void setActionListener(ActionListener listener) {
        mActionListener = listener;
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawTarget(canvas);
        drawBullets(canvas);
        drawActiveBullet(canvas);
        switch (mActionState) {
            case IDLE:
                break;
            case ADD:
            case RELOCATE:
                drawActiveBullet(canvas);
                break;
        }
    }

    private void testTransition(ActionState nextState) {
        boolean legal;

        switch (mActionState) {
            case IDLE:
                legal = nextState == ActionState.IDLE || nextState == ActionState.ADD || nextState == ActionState.RELOCATE;
                break;
            case ADD:
                legal = nextState == ActionState.IDLE;
                break;
            case RELOCATE:
            default:
                legal = nextState == ActionState.IDLE;
                break;
        }

        if (!legal) {
            throw new IllegalStateException(String.format("Illegal transition: %s -> %s", mActionState, nextState));
        }

        mActionState = nextState;
    }

    private void testTransition(ViewState nextState) {
        boolean legal;

        switch (mViewState) {
            case OVERVIEW:
                legal = nextState == ViewState.ZOOM;
                break;
            case ZOOM:
            default:
                legal = nextState == ViewState.OVERVIEW;
                break;
        }

        if (!legal) {
            throw new IllegalStateException(String.format("Illegal transition: %s -> %s", mViewState, nextState));
        }

        mViewState = nextState;
    }

    public void zoomIn() {
        zoomIn(mDstRect.width() / 2, mDstRect.height() / 2);
    }

    public void zoomIn(float x, float y) {
        testTransition(ViewState.ZOOM);

        int centerX = (int)((x / mDstRect.right) * mSrcRect.right);
        int centerY = (int)((y / mDstRect.bottom) * mSrcRect.bottom);

        int width = (int) ((mSrcRect.right - mSrcRect.left) / MAX_ZOOM_FACTOR);
        int height = (int) ((mSrcRect.bottom - mSrcRect.top) / MAX_ZOOM_FACTOR);

        mScaledRect.left = clamp(centerX - width / 2, 0, mSrcRect.right - width);
        mScaledRect.top = clamp(centerY - height / 2, 0, mSrcRect.bottom - height);
        mScaledRect.right = mScaledRect.left + mDstRect.width();
        mScaledRect.bottom = mScaledRect.top + mDstRect.height();

        mZoomLevel = MAX_ZOOM_FACTOR;

        invalidate();
        onZoomIn();
    }

    public void zoomOut() {
        testTransition(ViewState.OVERVIEW);

        mScaledRect.top = mDstRect.top;
        mScaledRect.left = mDstRect.left;
        mScaledRect.bottom = mDstRect.bottom;
        mScaledRect.right = mDstRect.right;

        mZoomLevel = MIN_ZOOM_FACTOR;

        invalidate();
        onZoomOut();
    }

    private void onZoomIn() {
        if (mZoomChangeListener != null) {
            mZoomChangeListener.onZoomIn();
        }
    }

    private void onZoomOut() {
        if (mZoomChangeListener != null) {
            mZoomChangeListener.onZoomOut();
        }
    }

    public void addBulletHole() {
        addBulletHole(mScaledRect.width() / 2, mScaledRect.height() / 2);
    }

    public void addBulletHole(float x, float y) {
        testTransition(ActionState.ADD);

        if (mBulletHoles.size() == mMaxNbrBullets) {
            throw new IllegalStateException("The maximum number of bullets has already been added");
        }

        float zoomedPixelsPerCm = mZoomLevel * mPixelsPerCm;

        // Find out where we are in the source rectangle
        x = mScaledRect.left + mScaledRect.width() * x / mDstRect.width();
        y = mScaledRect.top + mScaledRect.height() * y / mDstRect.height();

        mActiveBulletHole =  new BulletHole(BulletCaliber.CAL_22, x / zoomedPixelsPerCm, y / zoomedPixelsPerCm);

        invalidate();
        onAdd();
    }

    public void relocateBullet(int bullet) {
        testTransition(ActionState.RELOCATE);

        if (bullet < 0 || bullet >= mBulletHoles.size()) {
            throw new IllegalStateException("Illegal bullet index");
        }

        mActiveBulletIdx = bullet;
        mActiveBulletHole = mBulletHoles.get(bullet).copy();
        invalidate();
        onRelocate();
    }

    public void cancelMove() {
        testTransition(ActionState.IDLE);

        mActiveBulletHole = null;
        mActiveBulletIdx = Integer.MIN_VALUE;

        invalidate();
        onIdle();
    }

    public void removeBullet() {
        testTransition(ActionState.IDLE);

        if (mActiveBulletHole != null) {
            mBulletHoles.remove(mActiveBulletIdx);

            mActiveBulletIdx = Integer.MIN_VALUE;
            mActiveBulletHole = null;
        }

        invalidate();
        onIdle();
    }

    public void commitBullet() {

        if (mActionState == ActionState.ADD) {
            if (mActiveBulletHole != null && mBulletHoles.size() < mMaxNbrBullets) {
                mBulletHoles.add(mActiveBulletHole);
            }
        } else if (mActionState == ActionState.RELOCATE) {
            mBulletHoles.set(mActiveBulletIdx, mActiveBulletHole);
            mActiveBulletIdx = Integer.MIN_VALUE;
        }

        mActiveBulletHole = null;

        testTransition(ActionState.IDLE);
        invalidate();

        onIdle();
    }

    public int getNbrOfBullets() {
        return mBulletHoles.size();
    }

    private void onIdle() {
        if (mActionListener != null) {
            mActionListener.onIdle();
        }
    }

    private void onAdd() {
        if (mActionListener != null) {
            mActionListener.onAdd();
        }
    }

    private void onRelocate() {
        if (mActionListener != null) {
            mActionListener.onRelocate();
        }
    }

    private void drawActiveBullet(Canvas canvas) {
        if (mActiveBulletHole != null) {
            float pixelsPerCm = mZoomLevel * mPixelsPerCm;
            float bulletDiameter = CM_PER_INCH * 0.22f * pixelsPerCm;
            float radius = bulletDiameter / 2;

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            PointF p = mActiveBulletHole.toPixelLocation(pixelsPerCm);

            canvas.drawCircle(p.x - mScaledRect.left, p.y - mScaledRect.top, radius, paint);
        }
    }

    private void drawBullets(Canvas canvas) {
        float pixelsPerCm = mZoomLevel * mPixelsPerCm;
        float bulletDiameter = CM_PER_INCH * 0.22f * pixelsPerCm;
        float radius = bulletDiameter / 2;

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        int size = mBulletHoles.size();
        for (int i = 0; i < size; i++) {
            if (i != mActiveBulletIdx) {
                PointF p = mBulletHoles.get(i).toPixelLocation(pixelsPerCm);
                canvas.drawCircle(p.x - mScaledRect.left, p.y - mScaledRect.top, radius, paint);
            }
        }
    }

    private boolean touches(float bulletRadius, float bulletDiameter, float radius) {
        float upperBound = bulletRadius + bulletDiameter / 2;
        float lowerBound = bulletRadius - bulletDiameter / 2;

        return lowerBound <= radius && radius <= upperBound;
    }

    private void drawTarget(Canvas canvas) {
        float pixelsPerCm = mZoomLevel * mPixelsPerCm;

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

        float bulletRadius = Float.MAX_VALUE;
        float bulletDiameter = 0f;
        int ring;

        if (mActiveBulletHole != null) {
            bulletRadius = mActiveBulletHole.getRadius(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
            bulletDiameter = mActiveBulletHole.getCaliber().getDiameter();
        }

        for (ring = 10; ring > 4; ring--) {
            float radius = ring * radiusIncrement;

            if (touches(bulletRadius, bulletDiameter, radius)) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.BLACK);
            }

            canvas.drawCircle(cx, cy, radius * pixelsPerCm, paint);
        }

        paint.setColor(Color.BLACK);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(cx, cy, ring * radiusIncrement * pixelsPerCm, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);


        for (; ring > 0; ring--) {
            float radius = ring * radiusIncrement;

            if (touches(bulletRadius, bulletDiameter, radius)) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.WHITE);
            }

            canvas.drawCircle(cx, cy, radius * pixelsPerCm, paint);
        }

        paint.setColor(Color.WHITE);

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

        Log.d(TAG, String.format("onSizeChanged(%d, %d, %d, %d)", w, h, oldw, oldh));

        int zoomWidth = (int) (w * MAX_ZOOM_FACTOR);
        int zoomHeight = (int) (h * MAX_ZOOM_FACTOR);

        mSrcRect = new Rect(0, 0, zoomWidth, zoomHeight);
        mScaledRect = new Rect(0, 0, w, h);
        mDstRect = new Rect(0, 0, w, h);

        mPixelsPerCm = w / VIRTUAL_WIDTH;
        VIRTUAL_HEIGHT = h / mPixelsPerCm;

        Log.d(TAG, String.format("W/H %.2f %.2f", VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
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

    private OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
            return true;
        }
    };

    private GestureDetector.SimpleOnGestureListener mSimpleGestureDetector= new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            switch (mViewState) {
                case OVERVIEW:
                    zoomIn(event.getX(), event.getY());
                    break;
                case ZOOM:
                    zoomOut();
                    break;
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Log.d(TAG, "onLongPress");

            switch (mActionState) {
                case IDLE:
                    addBulletHole(event.getX(), event.getY());
                    break;
                case ADD:
                    break;
                case RELOCATE:
                    break;
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            Log.d(TAG, "onSingleTapConfirmed");

            commitBullet();

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            updateTarget(e2.getX(), e2.getY(), distanceX, distanceY);

            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling");
            cancelMove();

            return true;
        }

        private void updateTarget(float pointerX, float pointerY, float distanceX, float distanceY) {

            if (mActiveBulletHole != null) {
                float pixelsPercm = mZoomLevel * mPixelsPerCm;
                mActiveBulletHole.move(-distanceX / pixelsPercm, -distanceY / pixelsPercm);
            } else if (mViewState == ViewState.ZOOM){
                int width = mDstRect.width();
                int height = mDstRect.height();
                mScaledRect.left = (int) clamp(mScaledRect.left + distanceX, 0, mSrcRect.right - width);
                mScaledRect.top = (int) clamp(mScaledRect.top + distanceY, 0, mSrcRect.bottom - height);
                mScaledRect.right = mScaledRect.left + width;
                mScaledRect.bottom = mScaledRect.top + height;
            }
        }
    };
}
