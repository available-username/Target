package se.thirdbase.target.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import se.thirdbase.target.R;
import se.thirdbase.target.view.TargetView;

/**
 * Created by alexp on 2/12/16.
 */
public class TargetFragment extends BaseFragment {

    private static final String TAG = TargetFragment.class.getSimpleName();

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
    private static final int BUTTON_COMMIT_IDX = 1;
    private static final int BUTTON_DELETE_IDX = 1;
    private static final int BUTTON_RELOCATE_IDX = 2;
    private static final int BUTTON_EDIT_BULLET0 = 1;
    private static final int BUTTON_EDIT_BULLET1= 2;
    private static final int BUTTON_EDIT_BULLET2 = 3;
    private static final int BUTTON_EDIT_BULLET3 = 4;
    private static final int BUTTON_EDIT_BULLET4 = 5;


    private static final String BUNDLE_TAG_STATE = "BUNDLE_TAG_STATE";

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

    public static TargetFragment newInstance() {
        return new TargetFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.target_layout, container, false);

        mTargetView = (TargetView)view.findViewById(R.id.target_layout_target_view);

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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

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

        mState = nextState;
    }

    private void onEnterOverview() {
        testTransition(State.OVERVIEW);
        clearButtons();
        toggleButton(mEditButton, R.drawable.edit);
        toggleButton(mAddButton, R.drawable.add);
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
        toggleButton(mSelectBullet0, R.drawable.one);
        toggleButton(mSelectBullet1, R.drawable.two);
        toggleButton(mSelectBullet2, R.drawable.three);
        toggleButton(mSelectBullet3, R.drawable.four);
        toggleButton(mSelectBullet4, R.drawable.five);
    }

    private void onEnterEditBullet(int bulletIndex) {
        testTransition(State.EDIT_BULLET);
        clearButtons();
        toggleButton(mCancelButton, R.drawable.cancel);
        toggleButton(mDeleteButton, R.drawable.delete);
        toggleButton(mRelocateButton, R.drawable.relocate);
    }

    private void onEnterRelocate() {
        testTransition(State.RELOCATE_BULLET);
        clearButtons();
        toggleButton(mCancelButton, R.drawable.cancel);
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
                        onEnterAddBullet();
                    } else if (v == mEditButton) {
                        Log.d(TAG, "mEditButton clicked");
                        onEnterSelectBullet();
                    }
                    break;
                case ADD_BULLET:
                    if (v == mCommitButton) {
                        Log.d(TAG, "mCommitButton clicked");
                        onEnterOverview();
                    } else if (v == mCancelButton) {
                        Log.d(TAG, "mCancelButton clicked");
                        onEnterOverview();
                    }
                    break;
                case EDIT_BULLET:
                    if (v == mCancelButton) {
                        Log.d(TAG, "mCancelButton clicked");
                        onEnterSelectBullet();
                    } else if (v == mDeleteButton) {
                        Log.d(TAG, "mDeleteButton clicked");
                        onEnterOverview();
                    } else if (v == mRelocateButton) {
                        Log.d(TAG, "mRelocateButton clicked");
                        onEnterRelocate();
                    }
                    break;
                case SELECT_BULLET:
                    if (v == mCancelButton) {
                        Log.d(TAG, "mCancelButton clicked");
                        onEnterOverview();
                    } else if (v == mSelectBullet0) {
                        Log.d(TAG, "mSelectBullet0 clicked");
                        onEnterEditBullet(0);
                    } else if (v == mSelectBullet1) {
                        Log.d(TAG, "mSelectBullet1 clicked");
                        onEnterEditBullet(1);
                    } else if (v == mSelectBullet2) {
                        Log.d(TAG, "mSelectBullet2 clicked");
                        onEnterEditBullet(2);
                    } else if (v == mSelectBullet3) {
                        Log.d(TAG, "mSelectBullet3 clicked");
                        onEnterEditBullet(3);
                    } else if (v == mSelectBullet4) {
                        Log.d(TAG, "mSelectBullet4 clicked");
                        onEnterEditBullet(4);
                    }
                    break;
                case RELOCATE_BULLET:
                    if (v == mCommitButton) {
                        Log.d(TAG, "mCommitButton clicked");
                        onEnterOverview();
                    } else if (v == mCancelButton) {
                        Log.d(TAG, "mCancelButton clicked");
                        onEnterOverview();
                    }
                    break;
            }
        }
    };
}
