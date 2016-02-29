package se.thirdbase.target.fragment.precision;

import android.content.Context;
import android.support.v4.app.Fragment;

import se.thirdbase.target.activity.PrecisionStateListener;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

/**
 * Created by alexp on 2/19/16.
 */
public class PrecisionBaseFragment extends Fragment {

    private PrecisionStateListener mPrecisionStateListener;
    private PrecisionRoundSummaryListener mPrecisionRoundSummaryListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PrecisionStateListener) {
            mPrecisionStateListener = (PrecisionStateListener) context;
        }

        if (context instanceof PrecisionRoundSummaryListener) {
            mPrecisionRoundSummaryListener = (PrecisionRoundSummaryListener) context;
        }
    }

    protected void onUpdatePrecisionSeries(PrecisionSeries precisionSeries) {
        if (mPrecisionStateListener != null) {
            mPrecisionStateListener.onUpdatePrecisionSeries(precisionSeries);
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
