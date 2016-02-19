package se.thirdbase.target.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.thirdbase.target.model.BulletCaliber;
import se.thirdbase.target.model.BulletHole;

/**
 * Created by alexp on 2/8/16.
 */
public abstract class TargetView extends View {

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
    private Rect mSrcRect = new Rect();
    private Rect mDstRect = new Rect();
    private Rect mScaledRect = new Rect();

    private ActionState mActionState = ActionState.IDLE;
    private ViewState mViewState = ViewState.OVERVIEW;
    private GestureDetector mGestureDetector;

    protected ArrayList<BulletHole> mBulletHoles = new ArrayList<>();
    private BulletHole mActiveBulletHole;
    private int mActiveBulletIdx = Integer.MIN_VALUE;

    private ZoomChangeListener mZoomChangeListener;
    private ActionListener mActionListener;

    private int mMaxNbrBullets = MAX_NBR_BULLETS;

    public TargetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGestureDetector = new GestureDetector(context, mSimpleGestureDetector);
        setOnTouchListener(mOnTouchListener);

        setSaveEnabled(true);
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

    private static final String BUNDLE_TAG_SUPER_PARCEL = "BUNDLE_TAG_SUPER_PARCEL";
    private static final String BUNDLE_TAG_PIXELS_PER_CM = "BUNDLE_TAG_PIXELS_PER_CM";
    private static final String BUNDLE_TAG_ZOOM_LEVEL = "BUNDLE_TAG_ZOOM_LEVEL";
    private static final String BUNDLE_TAG_SRC_RECT = "BUNDLE_TAG_SRC_RECT";
    private static final String BUNDLE_TAG_DST_RECT = "BUNDLE_TAG_DST_RECT";
    private static final String BUNDLE_TAG_SCALED_RECT = "BUNDLE_TAG_SCALED_RECT";
    private static final String BUNDLE_TAG_ACTION_STATE = "BUNDLE_TAG_ACTION_STATE";
    private static final String BUNDLE_TAG_VIEW_STATE = "BUNDLE_TAG_VIEW_STATE";
    private static final String BUNDLE_TAG_BULLET_HOLES = "BUNDLE_TAG_BULLET_HOLES";
    private static final String BUNDLE_TAG_ACTIVE_BULLET_HOLE = "BUNDLE_TAG_ACTIVE_BULLET_HOLE";
    private static final String BUNDLE_TAG_ACTIVE_BULLET_HOLE_IDX = "BUNDLE_TAG_ACTIVE_BULLET_HOLE_IDX";

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcel = super.onSaveInstanceState();

        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_TAG_SUPER_PARCEL, parcel);
        bundle.putFloat(BUNDLE_TAG_PIXELS_PER_CM, mPixelsPerCm);
        bundle.putFloat(BUNDLE_TAG_ZOOM_LEVEL, mZoomLevel);
        bundle.putParcelable(BUNDLE_TAG_SRC_RECT, mSrcRect);
        bundle.putParcelable(BUNDLE_TAG_DST_RECT, mDstRect);
        bundle.putParcelable(BUNDLE_TAG_SCALED_RECT, mScaledRect);
        bundle.putSerializable(BUNDLE_TAG_ACTION_STATE, mActionState);
        bundle.putSerializable(BUNDLE_TAG_VIEW_STATE, mViewState);
        bundle.putParcelableArrayList(BUNDLE_TAG_BULLET_HOLES, mBulletHoles);
        bundle.putParcelable(BUNDLE_TAG_ACTIVE_BULLET_HOLE, mActiveBulletHole);
        bundle.putInt(BUNDLE_TAG_ACTIVE_BULLET_HOLE_IDX, mActiveBulletIdx);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcel) {
        Bundle bundle = (Bundle)parcel;
        Parcelable superParcel = bundle.getBundle(BUNDLE_TAG_SUPER_PARCEL);

        super.onRestoreInstanceState(superParcel);

        mPixelsPerCm = bundle.getFloat(BUNDLE_TAG_PIXELS_PER_CM);
        mZoomLevel = bundle.getFloat(BUNDLE_TAG_ZOOM_LEVEL);
        mSrcRect = bundle.getParcelable(BUNDLE_TAG_SRC_RECT);
        mDstRect = bundle.getParcelable(BUNDLE_TAG_DST_RECT);
        mScaledRect = bundle.getParcelable(BUNDLE_TAG_SCALED_RECT);
        mActionState = (ActionState)bundle.getSerializable(BUNDLE_TAG_ACTION_STATE);
        mViewState = (ViewState) bundle.getSerializable(BUNDLE_TAG_VIEW_STATE);
        mBulletHoles = bundle.getParcelableArrayList(BUNDLE_TAG_BULLET_HOLES);
        mActiveBulletHole = bundle.getParcelable(BUNDLE_TAG_ACTIVE_BULLET_HOLE);
        mActiveBulletIdx = bundle.getInt(BUNDLE_TAG_ACTIVE_BULLET_HOLE_IDX);

        invalidate();
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

    public void addBullet() {
        addBullet(mScaledRect.width() / 2, mScaledRect.height() / 2);
    }

    public abstract int getMaxNbrBullets();

    public void addBullet(float x, float y) {
        testTransition(ActionState.ADD);

        if (mBulletHoles.size() == getMaxNbrBullets()) {
            throw new IllegalStateException("The maximum number of bullets has already been added");
        }

        float zoomedPixelsPerCm = mZoomLevel * mPixelsPerCm;

        // Find out where we are in the source rectangle
        x = mScaledRect.left + mScaledRect.width() * x / mDstRect.width();
        y = mScaledRect.top + mScaledRect.height() * y / mDstRect.height();

        float xOffset = VIRTUAL_WIDTH / 2 - x / zoomedPixelsPerCm;
        float yOffset = y / zoomedPixelsPerCm - VIRTUAL_HEIGHT / 2;
        float radius = (float)Math.sqrt(xOffset * xOffset + yOffset * yOffset);
        float angle = (float)(Math.PI - Math.atan2(yOffset, xOffset));

        Log.d(TAG, String.format("Radius: %.2f Angle: %.2f", radius, angle));

        mActiveBulletHole =  new BulletHole(BulletCaliber.CAL_22, radius, angle);

        invalidate();
        onAdd();
    }

    public int getNbrOfBulletsHoles() {
        return mBulletHoles.size();
    }

    public List<BulletHole> getBulletHoles() {
        return Collections.unmodifiableList(mBulletHoles);
    }

    protected BulletHole getActiveBulletHole() {
        return mActiveBulletHole;
    }

    public void setBulletHoles(List<BulletHole> bulletHoles) {
        mBulletHoles = new ArrayList<>();
        mBulletHoles.addAll(bulletHoles);
        invalidate();
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

    public void cancelMoveBullet() {
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

    public abstract int getBulletScore(int bulletIdx);

    public abstract int getTotalScore();

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

    protected float getZoomLevel() {
        return mZoomLevel;
    }

    protected float getPixelsPerCm() {
        return mPixelsPerCm;
    }

    protected PointF getCenterPixelCoordinate() {
        PointF center = new PointF();
        center.x = mScaledRect.width() * mZoomLevel / 2 - mScaledRect.left;
        center.y = mScaledRect.height() * mZoomLevel  / 2 - mScaledRect.top;

        return center;
    }

    protected void drawActiveBullet(Canvas canvas) {
        if (mActiveBulletHole != null) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            drawBullet(canvas, paint, mActiveBulletHole);
        }
    }

    protected void drawBullets(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        int size = mBulletHoles.size();
        for (int i = 0; i < size; i++) {
            if (i != mActiveBulletIdx) {
                drawBullet(canvas, paint, mBulletHoles.get(i));
            }
        }
    }

    protected void drawBullet(Canvas canvas, Paint paint, BulletHole bulletHole) {
        float pixelsPerCm = mZoomLevel * mPixelsPerCm;
        float bulletDiameter = bulletHole.getCaliber().getDiameter() * pixelsPerCm;
        float radius = bulletDiameter / 2;

        PointF p = bulletHole.toCartesianCoordinates();
        p.x += VIRTUAL_WIDTH / 2;
        p.y += VIRTUAL_HEIGHT / 2;
        p.x *= pixelsPerCm;
        p.y *= pixelsPerCm;

        canvas.drawCircle(p.x - mScaledRect.left, p.y - mScaledRect.top, radius, paint);
    }

    protected abstract void drawTarget(Canvas canvas);

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
                    addBullet(event.getX(), event.getY());
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
            updateTarget(distanceX, distanceY);

            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling");
            cancelMoveBullet();

            return true;
        }

        private void updateTarget(float distanceX, float distanceY) {

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
