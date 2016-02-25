package se.thirdbase.target.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import se.thirdbase.target.PrecisionStateListener;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

/**
 * Created by alexp on 2/19/16.
 */
public class PrecisionBaseFragment extends Fragment {

    private PrecisionStateListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PrecisionStateListener) {
            mListener = (PrecisionStateListener)context;
        }
    }

    protected void onUpdatePrecisionSeries(PrecisionSeries precisionSeries) {
        if (mListener != null) {
            mListener.onUpdatePrecisionSeries(precisionSeries);
        }
    }

    protected void onPrecisionSeriesUpdated() {
        if (mListener != null) {
            mListener.onPrecisionSeriesUpdated();
        }
    }

    protected void onPrecisionSeriesComplete(PrecisionSeries precisionSeries) {
        if (mListener != null) {
            mListener.onPrecisionSeriesComplete(precisionSeries);
        }
    }

    protected void onPrecisionRoundComplete(PrecisionRound precisionRound) {
        if (mListener != null) {
            mListener.onPrecisionRoundComplete(precisionRound);
        }
    }
}
