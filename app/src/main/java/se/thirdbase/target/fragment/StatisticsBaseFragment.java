package se.thirdbase.target.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import se.thirdbase.target.StatisticsStateListener;

/**
 * Created by alexp on 2/23/16.
 */
public abstract class StatisticsBaseFragment extends Fragment {

    private StatisticsStateListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof StatisticsStateListener) {
            mListener = (StatisticsStateListener)context;
        }
    }

    protected void onOverview() {
        if (mListener != null) {
            mListener.onOverview();
        }
    }

    protected void onPrecision() {
        if (mListener != null) {
            mListener.onPrecision();
        }
    }
}
