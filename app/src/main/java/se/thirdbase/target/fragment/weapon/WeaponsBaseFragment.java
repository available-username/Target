package se.thirdbase.target.fragment.weapon;

import android.content.Context;
import android.support.v4.app.Fragment;

import se.thirdbase.target.activity.WeaponsStateListener;
import se.thirdbase.target.model.Weapon;

/**
 * Created by alexp on 2/29/16.
 */
public class WeaponsBaseFragment extends Fragment {

    private WeaponsStateListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof WeaponsStateListener) {
            mListener = (WeaponsStateListener)context;
        }
    }

    public void onOverview() {
        if (mListener != null) {
            mListener.onOverview();
        }
    }

    public void onAdd() {
        if (mListener != null) {
            mListener.onAdd();
        }
    }

    public void onAdded(Weapon weapon) {
        if (mListener != null) {
            mListener.onAdded(weapon);
        }
    }
}
