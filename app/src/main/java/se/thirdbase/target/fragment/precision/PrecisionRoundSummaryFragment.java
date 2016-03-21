package se.thirdbase.target.fragment.precision;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.precision.PrecisionRound;
import se.thirdbase.target.model.precision.PrecisionSeries;
import se.thirdbase.target.model.precision.PrecisionTarget;
import se.thirdbase.target.util.PrecisionMath;

/**
 * Created by alexp on 2/19/16.
 */
public class PrecisionRoundSummaryFragment extends PrecisionBaseFragment {

    private static final String TAG = PrecisionRoundSummaryFragment.class.getSimpleName();

    private static final String BUNDLE_TAG_PRECISION_ROUND = "BUNDLE_TAG_PRECISION_ROUND";

    private TextView mScoreText;
    private TextView mMaxSpreadText;
    private TextView mAvgSpreadText;
    private TextView mStdSpreadText;
    private TextView mOffsetText;
    private Button mPointsDistributionButton;
    private Button mHitsDistributionButton;

    private PrecisionRound mPrecisionRound;
    private int mScore = 0;
    private double mMaxSpread;
    private double mAvgSpread;
    private double mHitStd;
    //private PointF mHitStd;
    private PointF mHitMean;
    private List<Pair<Float, Float>> mScoreDistribution = new ArrayList<>();

    public static PrecisionRoundSummaryFragment newInstance(PrecisionRound precisionRound) {
        Bundle arguments = new Bundle();

        arguments.putParcelable(BUNDLE_TAG_PRECISION_ROUND, precisionRound);

        PrecisionRoundSummaryFragment fragment = new PrecisionRoundSummaryFragment();

        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle arguments = getArguments();

        mPrecisionRound = arguments.getParcelable(BUNDLE_TAG_PRECISION_ROUND);

        PrecisionMath math = new PrecisionMath(mPrecisionRound);

        mMaxSpread = math.getMaxSpread();
        mAvgSpread = math.getAverageSpread();
        mHitMean = math.getHitMean();
        mHitStd = math.getHitStd();
        mScore = math.getScore();
        mScoreDistribution = math.getScoreDistribution();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.precision_round_summary_layout, container, false);

        if (savedInstanceState != null)  {
            onRestoreInstanceState(savedInstanceState);
        }

        mScoreText = (TextView) view.findViewById(R.id.precision_round_summary_layout_score);
        mMaxSpreadText = (TextView) view.findViewById(R.id.precision_round_summary_layout_max_spread);
        mAvgSpreadText = (TextView) view.findViewById(R.id.precision_round_summary_layout_avg_spread);
        mStdSpreadText = (TextView) view.findViewById(R.id.precision_round_summary_layout_std_spread);
        mOffsetText = (TextView) view.findViewById(R.id.precision_round_summary_layout_offset);

        mPointsDistributionButton = (Button) view.findViewById(R.id.precision_round_summary_layout_points_distribution_button);

        mPointsDistributionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = PrecisionScoreDistributionFragment.newInstance(mPrecisionRound);
                display(fragment);
            }
        });

        mHitsDistributionButton = (Button) view.findViewById(R.id.precision_round_summary_layout_hits_distribution_button);

        mHitsDistributionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = PrecisionHitDistributionFragment.newInstance(mPrecisionRound);
                display(fragment);
            }
        });

        updateTextFields();

        Fragment fragment = PrecisionHitDistributionFragment.newInstance(mPrecisionRound);
        display(fragment);

        return view;
    }

    private void updateTextFields() {
        mScoreText.setText(getResources().getString(R.string.points, mScore));
        mMaxSpreadText.setText(getResources().getString(R.string.max_spread, mMaxSpread));
        mAvgSpreadText.setText(getResources().getString(R.string.mean_spread, mAvgSpread));

        String formatString;
        if (mHitMean.x < 0 && mHitMean.y < 0) {
            formatString = getResources().getString(R.string.up_left_offset);
        } else if (mHitMean.x < 0 && mHitMean.y > 0) {
            formatString = getResources().getString(R.string.down_left_offset);
        } else if (mHitMean.x > 0 && mHitMean.y > 0) {
            formatString = getResources().getString(R.string.down_right_offset);
        } else /* if (mHitMean.x > 0 && mHitMean.y < 0) */ {
            formatString = getResources().getString(R.string.up_right_offset);
        }

        float xOffset = Math.abs(mHitMean.x);
        float yOffset = Math.abs(mHitMean.y);
        mOffsetText.setText(String.format(formatString, yOffset, xOffset));

        mStdSpreadText.setText(getResources().getString(R.string.standard_deviation, mHitStd));
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void onRestoreInstanceState(Bundle bundle) {

    }

    private void display(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.precision_round_summary_layout_graph_container, fragment, null);
        transaction.commit();
    }
}
