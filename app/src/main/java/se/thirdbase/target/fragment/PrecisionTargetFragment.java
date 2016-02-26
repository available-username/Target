package se.thirdbase.target.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.thirdbase.target.R;
import se.thirdbase.target.model.BulletCaliber;
import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.PrecisionSeries;
import se.thirdbase.target.model.PrecisionTarget;
import se.thirdbase.target.view.TargetView;

/**
 * Created by alexp on 2/12/16.
 */
public class PrecisionTargetFragment extends PrecisionBaseFragment {

    private static final String TAG = PrecisionTargetFragment.class.getSimpleName();

    private enum State {
        OVERVIEW,
        ADD_BULLET,
        EDIT_BULLET,
        SELECT_BULLET,
        RELOCATE_BULLET
    }

    private static final int NBR_BUTTONS = 6;
    private static final int BUTTON_EDIT_IDX = 0;
    private static final int BUTTON_CANCEL_IDX = 0;
    private static final int BUTTON_ADD_IDX = 1;
    private static final int BUTTON_COMMIT_IDX = 2;
    private static final int BUTTON_DELETE_IDX = 1;
    private static final int BUTTON_RELOCATE_IDX = 2;
    private static final int BUTTON_EDIT_BULLET0 = 1;
    private static final int BUTTON_EDIT_BULLET1= 2;
    private static final int BUTTON_EDIT_BULLET2 = 3;
    private static final int BUTTON_EDIT_BULLET3 = 4;
    private static final int BUTTON_EDIT_BULLET4 = 5;

    private static final String BUNDLE_TAG_STATE = "BUNDLE_TAG_STATE";
    private static final String BUNDLE_TAG_PRECISION_SERIES = "BUNDLE_TAG_PRECISION_SERIES";

    private TextView mCountText;
    private TextView mScoreText;
    private Button mSaveButton;

    private ImageButton[] mButtons = new ImageButton[NBR_BUTTONS];
    private ImageButton mEditButton;
    private ImageButton mAddButton;
    private ImageButton mDeleteButton;
    private ImageButton mCommitButton;
    private ImageButton mCancelButton;
    private ImageButton mRelocateButton;
    private ImageButton mSelectBullet0;
    private ImageButton mSelectBullet1;
    private ImageButton mSelectBullet2;
    private ImageButton mSelectBullet3;
    private ImageButton mSelectBullet4;

    private TargetView mTargetView;
    private State mState = State.OVERVIEW;
    private PrecisionSeries mPrecisionSeries;


    public static PrecisionTargetFragment newInstance() {
        return newInstance(null);
    }

    public static PrecisionTargetFragment newInstance(PrecisionSeries precisionSeries) {

        PrecisionTargetFragment fragment = new PrecisionTargetFragment();

        if (precisionSeries != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(BUNDLE_TAG_PRECISION_SERIES, precisionSeries);

            Log.d(TAG, "New instance with arguments");
            fragment.setArguments(arguments);
        } else {
            Log.d(TAG, "Regular instance");
        }

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle arguments = getArguments();

        if (arguments != null) {
            mPrecisionSeries = arguments.getParcelable(BUNDLE_TAG_PRECISION_SERIES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.precision_target_layout, container, false);

        mCountText = (TextView)view.findViewById(R.id.target_layout_nbr_bullets);
        mScoreText = (TextView)view.findViewById(R.id.target_layout_score);

        mTargetView = (TargetView)view.findViewById(R.id.target_layout_target_view);
        mTargetView.setActionListener(mActionListener);

        if(mPrecisionSeries != null) {
            List<BulletHole> bulletHoles = mPrecisionSeries.getBulletHoles();
            mTargetView.setBulletHoles(bulletHoles);
        }

        mSaveButton = (Button)view.findViewById(R.id.target_layout_save_button);
        mSaveButton.setOnClickListener(mOnSaveClickedListener);

        mButtons[0] = (ImageButton)view.findViewById(R.id.target_layout_button0);
        mButtons[1] = (ImageButton)view.findViewById(R.id.target_layout_button1);
        mButtons[2] = (ImageButton)view.findViewById(R.id.target_layout_button2);
        mButtons[3] = (ImageButton)view.findViewById(R.id.target_layout_button3);
        mButtons[4] = (ImageButton)view.findViewById(R.id.target_layout_button4);
        mButtons[5] = (ImageButton)view.findViewById(R.id.target_layout_button5);

        for (int i = 0; i < mButtons.length; i++) {
            mButtons[i].setOnClickListener(mOnClickListener);
        }

        mEditButton = mButtons[BUTTON_EDIT_IDX];
        mAddButton = mButtons[BUTTON_ADD_IDX];
        mCommitButton = mButtons[BUTTON_COMMIT_IDX];
        mCancelButton = mButtons[BUTTON_CANCEL_IDX];
        mDeleteButton = mButtons[BUTTON_DELETE_IDX];
        mRelocateButton = mButtons[BUTTON_RELOCATE_IDX];

        mSelectBullet0 = mButtons[BUTTON_EDIT_BULLET0];
        mSelectBullet1 = mButtons[BUTTON_EDIT_BULLET1];
        mSelectBullet2 = mButtons[BUTTON_EDIT_BULLET2];
        mSelectBullet3 = mButtons[BUTTON_EDIT_BULLET3];
        mSelectBullet4 = mButtons[BUTTON_EDIT_BULLET4];

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        onEnterOverview();

        populateWithTestData();

        updateTextFields();

        return view;
    }

    private void updateTextFields() {
        String countText = getResources().getString(R.string.number_of_bullets, mTargetView.getNbrOfBulletsHoles());
        String scoreText = getResources().getString(R.string.points, mTargetView.getTotalScore());
        mCountText.setText(countText);
        mScoreText.setText(scoreText);

    }

    private void populateWithTestData() {
        List<BulletHole> holes = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < PrecisionTarget.MAX_NBR_BULLETS; i++) {
            BulletHole hole = new BulletHole(BulletCaliber.CAL_22, random.nextFloat() * 10f, (float)(random.nextFloat() * Math.PI * 2));
            holes.add(hole);
        }
        mTargetView.setBulletHoles(holes);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private static final String BUNDLE_TAG_BULLET_HOLES = "BUNDLE_TAG_BULLET_HOLES";
    private static final String BUNDLE_TAG_ACTIVE_BULLET_HOLE = "BUNDLE_TAG_ACTIVE_BULLET_HOLE";
    private static final String BUNDLE_TAG_ACTIVE_BULLET_HOLE_IDX = "BUNDLE_TAG_ACTIVE_BULLET_HOLE_IDX";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(BUNDLE_TAG_STATE, mState);
    }

    private void onRestoreInstanceState(Bundle bundle) {
        mState = (State)bundle.getSerializable(BUNDLE_TAG_STATE);
    }

    private void testTransition(State nextState) {
        boolean legal = true;

        switch (mState) {
            case OVERVIEW:
                legal = nextState == State.OVERVIEW || nextState == State.SELECT_BULLET || nextState == State.ADD_BULLET;
                break;
            case ADD_BULLET:
                legal = nextState == State.OVERVIEW;
                break;

            case SELECT_BULLET:
                legal = nextState == State.OVERVIEW || nextState == State.EDIT_BULLET;
                break;
            case EDIT_BULLET:
                legal = nextState == State.SELECT_BULLET || nextState == State.RELOCATE_BULLET || nextState == State.OVERVIEW;
                break;
            case RELOCATE_BULLET:
                legal = nextState == State.OVERVIEW;
                break;
        }

        if (!legal) {
            throw new IllegalStateException(String.format("Illegal transition: %s -> %s", mState, nextState));
        }

        Log.d(TAG, String.format("%s -> %s", mState, nextState));

        mState = nextState;
    }

    private void onEnterOverview() {
        testTransition(State.OVERVIEW);
        clearButtons();
        toggleButton(mEditButton, R.drawable.edit);

        int nbrBullets = mTargetView.getNbrOfBulletsHoles();

        if (nbrBullets < 5) {
            toggleButton(mAddButton, R.drawable.add);
        }

        updateTextFields();
    }

    private void onEnterAddBullet() {
        testTransition(State.ADD_BULLET);
        clearButtons();
        toggleButton(mCancelButton, R.drawable.cancel);
        toggleButton(mCommitButton, R.drawable.commit);
    }

    private void onCommitBullet() {
        testTransition(State.OVERVIEW);
    }

    private void onEnterSelectBullet() {
        testTransition(State.SELECT_BULLET);
        clearButtons();
        toggleButton(mCancelButton, R.drawable.cancel);

        switch (mTargetView.getNbrOfBulletsHoles()) {
            case 5:
                toggleButton(mSelectBullet4, R.drawable.five);
            case 4:
                toggleButton(mSelectBullet3, R.drawable.four);
            case 3:
                toggleButton(mSelectBullet2, R.drawable.three);
            case 2:
                toggleButton(mSelectBullet1, R.drawable.two);
            case 1:
                toggleButton(mSelectBullet0, R.drawable.one);
        }
    }

    private void onEnterEditBullet() {
        testTransition(State.EDIT_BULLET);
        clearButtons();
        toggleButton(mCancelButton, R.drawable.cancel);
        toggleButton(mDeleteButton, R.drawable.delete);
        toggleButton(mRelocateButton, R.drawable.commit);
    }

    private void onEnterRelocate() {
        testTransition(State.RELOCATE_BULLET);
        //testTransition(State.EDIT_BULLET);
        clearButtons();
        toggleButton(mCancelButton, R.drawable.cancel);
        toggleButton(mDeleteButton, R.drawable.delete);
        toggleButton(mCommitButton, R.drawable.commit);
    }

    private void clearButtons() {
        for (ImageButton button : mButtons) {
            button.setVisibility(View.INVISIBLE);
        }
    }

    private void toggleButton(ImageButton button, int drawableId) {
        button.setImageResource(drawableId);
        button.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (mState) {
                case OVERVIEW:
                    if (v == mAddButton) {
                        Log.d(TAG, "mAddButton clicked");

                        if (mTargetView.getNbrOfBulletsHoles() < 5) {
                            mTargetView.addBullet();

                            //Transition to ADD_BULLET will be performed when the callback is
                            //received from the TargetView
                        } else {
                            Toast.makeText(getContext(), R.string.max_bullets_reached, Toast.LENGTH_SHORT).show();
                        }
                    } else if (v == mEditButton) {
                        Log.d(TAG, "mEditButton clicked");
                        onEnterSelectBullet();
                    }
                    break;
                case ADD_BULLET:
                    if (v == mCommitButton) {
                        Log.d(TAG, "mCommitButton clicked");
                        mTargetView.commitBullet();
                    } else if (v == mCancelButton) {
                        Log.d(TAG, "mCancelButton clicked");
                        mTargetView.cancelMoveBullet();
                    }
                    onEnterOverview();
                    break;
                case EDIT_BULLET:
                    if (v == mCancelButton) {
                        Log.d(TAG, "mCancelButton clicked");
                        mTargetView.cancelMoveBullet();
                        onEnterSelectBullet();
                    } else if (v == mDeleteButton) {
                        Log.d(TAG, "mDeleteButton clicked");
                        mTargetView.removeBullet();
                        onEnterOverview();
                    } else if (v == mRelocateButton) {
                        Log.d(TAG, "mRelocateButton clicked");
                        mTargetView.commitBullet();
                        onEnterOverview();
                    }
                    break;
                case SELECT_BULLET:
                    if (v == mCancelButton) {
                        Log.d(TAG, "mCancelButton clicked");
                        mTargetView.cancelMoveBullet();
                        onEnterOverview();
                    } else {
                        if (v == mSelectBullet0) {
                            Log.d(TAG, "mSelectBullet0 clicked");
                            mTargetView.relocateBullet(0);
                        } else if (v == mSelectBullet1) {
                            Log.d(TAG, "mSelectBullet1 clicked");
                            mTargetView.relocateBullet(1);
                        } else if (v == mSelectBullet2) {
                            Log.d(TAG, "mSelectBullet2 clicked");
                            mTargetView.relocateBullet(2);
                        } else if (v == mSelectBullet3) {
                            Log.d(TAG, "mSelectBullet3 clicked");
                            mTargetView.relocateBullet(3);
                        } else if (v == mSelectBullet4) {
                            Log.d(TAG, "mSelectBullet4 clicked");
                            mTargetView.relocateBullet(4);
                        }

                        //Transition to EDIT_BULLET will occur when
                        //a callback from the TargetView is received
                    }
                    break;
                case RELOCATE_BULLET:
                    if (v == mCancelButton) {
                        Log.d(TAG, "mCancelButton clicked");
                        mTargetView.cancelMoveBullet();
                    } else if (v == mDeleteButton) {
                        Log.d(TAG, "mDeleteButton clicked");
                        mTargetView.removeBullet();
                    } else if (v == mCommitButton) {
                        Log.d(TAG, "mCommitButton clicked");
                        mTargetView.commitBullet();
                    }
                    onEnterOverview();
                    break;
            }
        }
    };

    private TargetView.ActionListener mActionListener = new TargetView.ActionListener() {
        @Override
        public void onIdle() {
            onEnterOverview();
        }

        @Override
        public void onAdd() {
            onEnterAddBullet();
        }

        @Override
        public void onRelocate() {
            onEnterEditBullet();
        }
    };

    private View.OnClickListener mOnSaveClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<BulletHole> bulletHoleList = mTargetView.getBulletHoles();

            if (mPrecisionSeries == null) {
                PrecisionSeries precisionSeries = new PrecisionSeries(bulletHoleList);

                onPrecisionSeriesComplete(precisionSeries);
            } else {
                mPrecisionSeries.setBulletHoles(bulletHoleList);
                onPrecisionSeriesUpdated(mPrecisionSeries);
            }
        }
    };
}
