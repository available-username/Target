package se.thirdbase.target.fragment.precision;

import android.content.Context;
import android.support.v4.app.Fragment;

import se.thirdbase.target.activity.PrecisionStateListener;
import se.thirdbase.target.model.precision.PrecisionRound;
import se.thirdbase.target.model.precision.PrecisionSeries;

/**
 * Created by alexp on 2/19/16.
 */
public class PrecisionBaseFragment extends Fragment {

    private PrecisionStateListener mPrecisionStateListener;
    private PrecisionRoundSummaryFragment.PrecisionRoundSummaryListener mPrecisionRoundSummaryListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PrecisionStateListener) {
            mPrecisionStateListener = (PrecisionStateListener) context;
        }

        if (context instanceof PrecisionRoundSummaryFragment.PrecisionRoundSummaryListener) {
            mPrecisionRoundSummaryListener = (PrecisionRoundSummaryFragment.PrecisionRoundSummaryListener) context;
        }
    }

    protected void onPrecisionStartCompetitionRound() {
        if (mPrecisionStateListener != null) {
            mPrecisionStateListener.onPrecisionStartCompetitionRound();
        }
    }

    protected void onPrecisionStartTrainingRound() {
        if (mPrecisionStateListener != null) {
            mPrecisionStateListener.onPrecisionStartTrainingRound();
        }
    }

    protected void onPrecisionStartUnboundRound() {
        if (mPrecisionStateListener != null) {
            mPrecisionStateListener.onPrecisionStartUnboundRound();
        }
    }

    protected void onPrecisionSeriesUpdate(PrecisionSeries precisionSeries) {
        if (mPrecisionStateListener != null) {
            mPrecisionStateListener.onPrecisionSeriesUpdate(precisionSeries);
        }
    }

    protected void onPrecisionSeriesUpdated(PrecisionSeries precisionSeries) {
        if (mPrecisionStateListener != null) {
            mPrecisionStateListener.onPrecisionSeriesUpdated(precisionSeries);
        }
    }

    protected void onPrecisionSeriesComplete(PrecisionSeries precisionSeries) {
        if (mPrecisionStateListener != null) {
            mPrecisionStateListener.onPrecisionSeriesComplete(precisionSeries);
        }
    }

    protected void onPrecisionRoundComplete(PrecisionRound precisionRound) {
        if (mPrecisionStateListener != null) {
            mPrecisionStateListener.onPrecisionRoundComplete(precisionRound);
        }
    }

    protected void onPrecisionRoundPointsDistribution(PrecisionRound precisionRound) {
        if (mPrecisionRoundSummaryListener != null) {
            mPrecisionRoundSummaryListener.onPrecisionRoundScoreDistribution(precisionRound);
        }
    }

    protected void onPrecisionRoundHitsDistribution(PrecisionRound precisionRound) {
        if (mPrecisionRoundSummaryListener != null) {
            mPrecisionRoundSummaryListener.onPrecisionRoundHitDistribution(precisionRound);
        }
    }
}
