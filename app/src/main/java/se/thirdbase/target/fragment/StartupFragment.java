package se.thirdbase.target.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import se.thirdbase.target.R;

/**
 * Created by alexp on 2/15/16.
 */
public class StartupFragment extends BaseFragment {

    private Button mPrecisionButton;
    private Button mStatisticsButton;
    private Button mSettingsButton;

    public static StartupFragment newInstance() {
        return new StartupFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.startup_layout, container, false);

        mPrecisionButton = (Button) view.findViewById(R.id.startup_layout_new_precision_round);
        mStatisticsButton = (Button) view.findViewById(R.id.startup_layout_statistics);
        mSettingsButton = (Button) view.findViewById(R.id.startup_layout_settings);

        mPrecisionButton.setOnClickListener(mOnClickListener);
        mStatisticsButton.setOnClickListener(mOnClickListener);
        mSettingsButton.setOnClickListener(mOnClickListener);

        return view;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mPrecisionButton) {
                onPrecision();
            } else if (v == mStatisticsButton) {
                onStatistics();
            } else if (v == mSettingsButton) {
                onStatistics();
            }
        }
    };
}
