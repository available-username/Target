package se.thirdbase.target.fragment.ammunition;

import android.content.Context;
import android.support.v4.app.Fragment;

import se.thirdbase.target.AmmunitionStateListener;
import se.thirdbase.target.model.Ammunition;

/**
 * Created by alexp on 2/29/16.
 */
public class AmmunitionBaseFragment extends Fragment {

    private AmmunitionStateListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AmmunitionStateListener) {
            mListener = (AmmunitionStateListener)context;
        }
    }

    protected void onOverview() {
        if (mListener != null) {
            mListener.onOverview();
        }
    }

    protected void onAdd() {
        if (mListener != null) {
            mListener.onAdd();
        }
    }

    protected void onAdded(Ammunition ammunition) {
        if (mListener != null) {
            mListener.onAdded(ammunition);
        }
    }
}
