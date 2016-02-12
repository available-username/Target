package se.thirdbase.target.fragment;

import android.content.Context;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;

import se.thirdbase.target.StateListener;

/**
 * Created by alexp on 2/12/16.
 */
abstract class BaseFragment extends Fragment {

    private StateListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof StateListener) {
            mListener = (StateListener)context;
        }
    }

    public void onTarget() {
        if (mListener != null) {
            mListener.onTarget();
        }
    }

    public void onStatisticsOverview() {

    }
}
