package se.thirdbase.target.fragment.statistics;

import android.content.Context;
import android.support.v4.app.Fragment;

import se.thirdbase.target.activity.StatisticsStateListener;
import se.thirdbase.target.model.precision.PrecisionRound;

/**
 * Created by alexp on 2/23/16.
 */
public abstract class StatisticsBaseFragment extends Fragment {

    private StatisticsStateListener mStatisticsStateListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof StatisticsStateListener) {
            mStatisticsStateListener = (StatisticsStateListener) context;
        }
    }

    protected void onOverview() {
        if (mStatisticsStateListener != null) {
            mStatisticsStateListener.onOverview();
        }
    }

    protected void onPrecision() {
        if (mStatisticsStateListener != null) {
            mStatisticsStateListener.onPrecision();
        }
    }

    protected void onPrecisionProgress() {
        if (mStatisticsStateListener != null) {
            mStatisticsStateListener.onPrecisionProgress();
        }
    }

    protected void onPrecisionRoundSummary(PrecisionRound precisionRound) {
        if (mStatisticsStateListener != null) {
            mStatisticsStateListener.onPrecisionRoundSummary(precisionRound);
        }
    }
}
