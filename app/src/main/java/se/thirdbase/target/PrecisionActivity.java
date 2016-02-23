package se.thirdbase.target;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import se.thirdbase.target.fragment.PrecisionRoundFragment;
import se.thirdbase.target.fragment.PrecisionRoundSummaryFragment;
import se.thirdbase.target.fragment.PrecisionTargetFragment;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

public class PrecisionActivity extends BaseActivity implements PrecisionStateListener{

    private static final String TAG = PrecisionActivity.class.getSimpleName();

    private static final String BACK_STACK_TAG_PRECISION_ROUND = "BACK_STACK_TAG_PRECISION_ROUND";
    private static final String BACK_STACK_TAG_PRECISION_SERIES = "BACK_STACK_TAG_PRECISION_SERIES";
    private static final String BACK_STACK_TAG_PRECISION_SUMMARY = "BACK_STACK_TAG_PRECISION_SUMMARY";

    private PrecisionRound mPrecisionRound = new PrecisionRound();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precision);

        Fragment roundFragment = PrecisionRoundFragment.newInstance(mPrecisionRound);
        displayFragment(roundFragment, false, BACK_STACK_TAG_PRECISION_ROUND);

        Fragment seriesFragment = PrecisionTargetFragment.newInstance();
        displayFragment(seriesFragment, true, BACK_STACK_TAG_PRECISION_SERIES);
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
    public void onPrecisionSeriesUpdated() {
        Log.d(TAG, "onPrecisionSeriesUpdated()");

        popBackStack();
    }

    @Override
    public void onPrecisionSeriesComplete(PrecisionSeries precisionSeries) {
        Log.d(TAG, "onPrecisionSeriesComplete()");

        mPrecisionRound.addPrecisionSeries(precisionSeries);

        if (mPrecisionRound.getNbrSeries() == PrecisionRound.MAX_NBR_SERIES) {
            onPrecisionRoundComplete(mPrecisionRound);
        } else {
            popBackStack();
        }
    }

    @Override
    public void onPrecisionRoundComplete(PrecisionRound precisionRound) {
        Log.d(TAG, "onPrecisionRoundComplete()");
        popBackStack();
        Fragment fragment = PrecisionRoundSummaryFragment.newInstance(mPrecisionRound);
        displayFragment(fragment, false, BACK_STACK_TAG_PRECISION_SUMMARY);
    }
}
