package se.thirdbase.target.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.db.TargetDBHelper;
import se.thirdbase.target.fragment.loadout.LoadOutFragment;
import se.thirdbase.target.fragment.precision.PrecisionHitDistributionFragment;
import se.thirdbase.target.fragment.precision.PrecisionRoundFragment;
import se.thirdbase.target.fragment.precision.PrecisionRoundSummaryFragment;
import se.thirdbase.target.fragment.precision.PrecisionScoreDistributionFragment;
import se.thirdbase.target.fragment.precision.PrecisionSelectionFragment;
import se.thirdbase.target.fragment.precision.PrecisionTargetFragment;
import se.thirdbase.target.model.Ammunition;
import se.thirdbase.target.model.Principle;
import se.thirdbase.target.model.Setup;
import se.thirdbase.target.model.Weapon;
import se.thirdbase.target.model.precision.PrecisionRound;
import se.thirdbase.target.model.precision.PrecisionSeries;
import se.thirdbase.target.model.precision.UnboundPrecisionRound;

public class PrecisionActivity extends BaseActivity implements PrecisionStateListener, PrecisionRoundSummaryFragment.PrecisionRoundSummaryListener, LoadOutFragment.LoadOutListener {

    private static final String TAG = PrecisionActivity.class.getSimpleName();

    public static final String INTENT_SETUP_ID = "INTENT_SETUP_ID";

    private static final String BACK_STACK_TAG_SELECTION_FRAGMENT = "BACK_STACK_TAG_SELECTION_FRAGMENT";
    private static final String BACK_STACK_TAG_LOAD_OUT_FRAGMENT = "BACK_STACK_TAG_LOAD_OUT_FRAGMENT";
    private static final String BACK_STACK_TAG_PRECISION_ROUND = "BACK_STACK_TAG_PRECISION_ROUND";
    private static final String BACK_STACK_TAG_PRECISION_SERIES = "BACK_STACK_TAG_PRECISION_SERIES";
    private static final String BACK_STACK_TAG_PRECISION_SUMMARY = "BACK_STACK_TAG_PRECISION_SUMMARY";
    private static final String BACK_STACK_TAG_PRECISION_POINT_DISTRIBUTION = "BACK_STACK_TAG_PRECISION_POINT_DISTRIBUTION";
    private static final String BACK_STACK_TAG_PRECISION_HIT_DISTRIBUTION = "BACK_STACK_TAG_PRECISION_HIT_DISTRIBUTION";

    enum Selection {
        COMPETITION_ROUND,
        TRAINING_ROUND,
        UNBOUND_SERIES
    }

    private SQLiteDatabase mSQLiteDatabase;
    private PrecisionRound mPrecisionRound;
    private Weapon mWeapon;
    private Ammunition mAmmunition;
    private Selection selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precision);

        TargetDBHelper dbHelper = TargetDBHelper.getInstance(this);
        mSQLiteDatabase = dbHelper.getWritableDatabase();
        Log.d(TAG, "DATABASE OPENED");

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            long setupId = intent.getLongExtra(INTENT_SETUP_ID, Long.MIN_VALUE);

            if (setupId == Long.MIN_VALUE) {
                //onSelectLoadOut();
                onPrecisionSelection();
            } else {
                Setup setup = Setup.fetch(mSQLiteDatabase, setupId);

                if (setup.getPrinciple() != Principle.PRECISION) {
                    throw new IllegalArgumentException("Supplied setup does not exist or is not a precision setup");
                }

                mWeapon = setup.getWeapon();
                mAmmunition = setup.getAmmunition();

                //onPrecisionRoundBegin();
                onPrecisionSelection();
            }
        }
    }

    private static final String BUNDLE_TAG_PRECISION_ROUND = "BUNDLE_TAG_PRECISION_ROUND";
    private static final String BUNDLE_TAG_WEAPON = "BUNDLE_TAG_WEAPON";
    private static final String BUNDLE_TAG_AMMUNITION = "BUNDLE_TAG_AMMUNITION";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(BUNDLE_TAG_PRECISION_ROUND, mPrecisionRound);
        outState.putParcelable(BUNDLE_TAG_WEAPON, mWeapon);
        outState.putParcelable(BUNDLE_TAG_AMMUNITION, mAmmunition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPrecisionRound = savedInstanceState.getParcelable(BUNDLE_TAG_PRECISION_ROUND);
        mWeapon = savedInstanceState.getParcelable(BUNDLE_TAG_WEAPON);
        mAmmunition = savedInstanceState.getParcelable(BUNDLE_TAG_AMMUNITION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
            Log.d(TAG, "DATABASE CLOSED");
            mSQLiteDatabase = null;
        }
    }

    @Override
    public int getLayoutContainerId() {
        return R.id.precision_layout_id;
    }

    private void onSelectLoadOut() {
        Log.d(TAG, "onSelectLoadOut()");

        Fragment fragment = LoadOutFragment.newInstance();
        displayFragment(fragment, false, BACK_STACK_TAG_LOAD_OUT_FRAGMENT);
    }

    private void onPrecisionSelection() {
        Log.d(TAG, "onPrecisionSelection");

        Fragment fragment = PrecisionSelectionFragment.newInstance();
        displayFragment(fragment, false, BACK_STACK_TAG_SELECTION_FRAGMENT);
    }

    private void onPrecisionRoundBegin() {
        Log.d(TAG, "onPrecisionRoundBegin()");

        switch (selection) {
            case COMPETITION_ROUND:
                mPrecisionRound = new PrecisionRound(true);
                break;
            case TRAINING_ROUND:
                mPrecisionRound = new PrecisionRound(false);
                break;
            case UNBOUND_SERIES:
                mPrecisionRound = new UnboundPrecisionRound();
                break;
        }

        Fragment fragment = PrecisionTargetFragment.newInstance(mWeapon, mAmmunition);
        displayFragment(fragment, false, BACK_STACK_TAG_PRECISION_SERIES);
    }

    @Override
    public void onPrecisionStartCompetitionRound() {
        selection = Selection.COMPETITION_ROUND;
        /*
        Log.d(TAG, "onPrecisionStartCompetitionRound()");

        mPrecisionRound = new PrecisionRound(true);

        Fragment fragment = PrecisionTargetFragment.newInstance(mWeapon, mAmmunition);
        displayFragment(fragment, false, BACK_STACK_TAG_PRECISION_SERIES);
        */
        onSelectLoadOut();
    }

    @Override
    public void onPrecisionStartTrainingRound() {
        selection = Selection.TRAINING_ROUND;
        /*
        Log.d(TAG, "onPrecisionStartTrainingRound()");

        mPrecisionRound = new PrecisionRound(false);

        Fragment fragment = PrecisionTargetFragment.newInstance(mWeapon, mAmmunition);
        displayFragment(fragment, false, BACK_STACK_TAG_PRECISION_SERIES);
        */
        onSelectLoadOut();
    }

    @Override
    public void onPrecisionStartUnboundRound() {
        selection = Selection.UNBOUND_SERIES;
        onSelectLoadOut();
    }

    @Override
    public void onPrecisionSeriesUpdate(PrecisionSeries precisionSeries) {
        Log.d(TAG, "onPrecisionSeriesUpdate()");

        Fragment fragment;

        if (precisionSeries == null) {
            fragment = PrecisionTargetFragment.newInstance(mWeapon, mAmmunition);
        } else {
            fragment = PrecisionTargetFragment.newInstance(precisionSeries);
        }

        displayFragment(fragment, true, BACK_STACK_TAG_PRECISION_SERIES);
    }

    @Override
    public void onPrecisionSeriesUpdated(PrecisionSeries precisionSeries) {
        Log.d(TAG, "onPrecisionSeriesUpdated()");

        precisionSeries.store(mSQLiteDatabase);

        popBackStack();
    }

    @Override
    public void onPrecisionSeriesComplete(PrecisionSeries precisionSeries) {
        Log.d(TAG, "onPrecisionSeriesComplete()");

        precisionSeries.store(mSQLiteDatabase);

        mPrecisionRound.addPrecisionSeries(precisionSeries);

        int nbrSeries = mPrecisionRound.getNbrSeries();

        if (nbrSeries == 1) {
            Fragment fragment = PrecisionRoundFragment.newInstance(mPrecisionRound);
            displayFragment(fragment, false, BACK_STACK_TAG_PRECISION_ROUND);
        } else if (mPrecisionRound.getNbrSeries() == mPrecisionRound.getMaxNbrSeries()) {
            onPrecisionRoundComplete(mPrecisionRound);
        } else {
            popBackStack();
        }
    }

    @Override
    public void onPrecisionRoundComplete(PrecisionRound precisionRound) {
        Log.d(TAG, "onPrecisionRoundComplete()");

        precisionRound.store(mSQLiteDatabase);

        popBackStack();

        Fragment fragment = PrecisionRoundSummaryFragment.newInstance(mPrecisionRound);
        displayFragment(fragment, false, BACK_STACK_TAG_PRECISION_SUMMARY);
    }

    @Override
    public void onPrecisionRoundScoreDistribution(PrecisionRound precisionRound) {
        Log.d(TAG, "onPrecisionRoundScoreDistribution()");

        Fragment fragment = PrecisionScoreDistributionFragment.newInstance(precisionRound);
        displayFragment(fragment, true, BACK_STACK_TAG_PRECISION_POINT_DISTRIBUTION);
    }

    @Override
    public void onPrecisionRoundHitDistribution(PrecisionRound precisionRound) {
        Log.d(TAG, "onPrecisionRoundHitDistribution()");

        Fragment fragment = PrecisionHitDistributionFragment.newInstance(precisionRound);
        displayFragment(fragment, true, BACK_STACK_TAG_PRECISION_HIT_DISTRIBUTION);
    }

    @Override
    public void onLoadOut(Weapon weapon, Ammunition ammunition) {
        Log.d(TAG, "onLoadOut()");

        mWeapon = weapon;
        mAmmunition = ammunition;

        onPrecisionRoundBegin();
    }
}
