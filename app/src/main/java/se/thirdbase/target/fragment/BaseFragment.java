package se.thirdbase.target.fragment;

import android.content.Context;
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

    public void onStartup() {
        if (mListener != null) {
            mListener.onStartup();
        }
    }

    public void onPrecision() {
        if (mListener != null) {
            mListener.onPrecision();
        }
    }

    public void onStatistics() {
        if (mListener != null) {
            mListener.onStatistics();
        }
    }
}
