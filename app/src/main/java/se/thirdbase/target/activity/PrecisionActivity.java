package se.thirdbase.target.activity;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import se.thirdbase.target.R;
import se.thirdbase.target.db.TargetDBHelper;
import se.thirdbase.target.fragment.precision.PrecisionHitDistributionFragment;
import se.thirdbase.target.fragment.precision.PrecisionRoundSummaryListener;
import se.thirdbase.target.fragment.precision.PrecisionScoreDistributionFragment;
import se.thirdbase.target.fragment.precision.PrecisionRoundFragment;
import se.thirdbase.target.fragment.precision.PrecisionRoundSummaryFragment;
import se.thirdbase.target.fragment.precision.PrecisionTargetFragment;
import se.thirdbase.target.model.precision.PrecisionRound;
import se.thirdbase.target.model.precision.PrecisionSeries;

public class PrecisionActivity extends BaseActivity implements PrecisionStateListener, PrecisionRoundSummaryListener{

    private static final String TAG = PrecisionActivity.class.getSimpleName();

    private static final String BACK_STACK_TAG_PRECISION_ROUND = "BACK_STACK_TAG_PRECISION_ROUND";
    private static final String BACK_STACK_TAG_PRECISION_SERIES = "BACK_STACK_TAG_PRECISION_SERIES";
    private static final String BACK_STACK_TAG_PRECISION_SUMMARY = "BACK_STACK_TAG_PRECISION_SUMMARY";
    private static final String BACK_STACK_TAG_PRECISION_POINT_DISTRIBUTION = "BACK_STACK_TAG_PRECISION_POINT_DISTRIBUTION";
    private static final String BACK_STACK_TAG_PRECISION_HIT_DISTRIBUTION = "BACK_STACK_TAG_PRECISION_HIT_DISTRIBUTION";

    private SQLiteDatabase mSQLiteDatabase;
    private PrecisionRound mPrecisionRound = new PrecisionRound();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precision);

        /*
        Fragment roundFragment = PrecisionRoundFragment.newInstance(mPrecisionRound);
        displayFragment(roundFragment, false, BACK_STACK_TAG_PRECISION_ROUND);

        Fragment seriesFragment = PrecisionTargetFragment.newInstance();
        displayFragment(seriesFragment, true, BACK_STACK_TAG_PRECISION_SERIES);
        */
        Fragment seriesFragment = PrecisionTargetFragment.newInstance();
        displayFragment(seriesFragment, false, BACK_STACK_TAG_PRECISION_SERIES);

        TargetDBHelper dbHelper = TargetDBHelper.getInstance(this);
        mSQLiteDatabase = dbHelper.getWritableDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
            mSQLiteDatabase = null;
        }
    }

    @Override
    public int getLayoutContainerId() {
        return R.id.precision_layout_id;
    }

    @Override
    public void onUpdatePrecisionSeries(PrecisionSeries precisionSeries) {
        Log.d(TAG, "onUpdatePrecisionSeries()");

        Fragment fragment = PrecisionTargetFragment.newInstance(precisionSeries);

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
            //onPrecisionRoundComplete(mPrecisionRound);
            Fragment fragment = PrecisionRoundFragment.newInstance(mPrecisionRound);
            displayFragment(fragment, false, BACK_STACK_TAG_PRECISION_ROUND);
        } else if (mPrecisionRound.getNbrSeries() == PrecisionRound.MAX_NBR_SERIES) {
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
}
