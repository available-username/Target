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
    private float mMaxSpread = 0;
    private float mAvgSpread = 0;
    private float mStdSpread = 0;
    private PointF mBulletOffset;
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

        List<PrecisionSeries> precisionSeries = mPrecisionRound.getPrecisionSeries();

        mMaxSpread = calculateMaxSpread(precisionSeries);
        mAvgSpread = calculateAverageSpread(precisionSeries);
        mStdSpread = calculateAverageSpread(precisionSeries);
        mScore = calculateScore(precisionSeries);
        mScoreDistribution = calculateDistribution(precisionSeries);
        mBulletOffset = calculateBulletOffset(precisionSeries);
    }

    private float calculateMaxSpread(List<PrecisionSeries> precisionSeries) {
        float maxSpread = 0;

        int nbrSeries = precisionSeries.size();

        for (int i = 0; i < nbrSeries; i++) {
            PrecisionSeries series = precisionSeries.get(i);

            List<BulletHole> bulletHoles = series.getBulletHoles();
            int nbrBullets = bulletHoles.size();

            for (int j = 0; j < nbrBullets - 1; j++) {
                for (int k = j + 1; k < nbrBullets; k++) {

                    float distance = getSpread(bulletHoles.get(j), bulletHoles.get(k));

                    if (distance > maxSpread) {
                        maxSpread = distance;
                    }
                }
            }
        }

        return maxSpread;
    }

    private float calculateAverageSpread(List<PrecisionSeries> precisionSeries) {
        float avgSpread = 0;

        int nbrSeries = precisionSeries.size();
        int nbrDistances = 0;

        for (int i = 0; i < nbrSeries; i++) {
            PrecisionSeries series = precisionSeries.get(i);

            List<BulletHole> bulletHoles = series.getBulletHoles();
            int nbrBullets = bulletHoles.size();

            for (int j = 0; j < nbrBullets - 1; j++) {
                for (int k = j + 1; k < nbrBullets; k++) {

                    float distance = getSpread(bulletHoles.get(j), bulletHoles.get(k));

                    avgSpread += distance;
                    nbrDistances += 1;
                }
            }
        }

        // Let avgSpread be zero if there's only one bullet
        return nbrDistances > 0 ? avgSpread / nbrDistances : 0;
    }

    private int calculateScore(List<PrecisionSeries> precisionSeries) {
        int score = 0;
        int nbrSeries = precisionSeries.size();

        for (int i = 0; i < nbrSeries; i++) {
            PrecisionSeries series = precisionSeries.get(i);
            score += series.getScore();
        }

        return score;
    }

    private List<Pair<Float, Float>> calculateDistribution(List<PrecisionSeries> precisionSeries) {
        int[] distribution = new int[10];

        for (PrecisionSeries series : precisionSeries) {
            for (BulletHole bulletHole : series.getBulletHoles()) {
                int score = PrecisionTarget.getBulletScore(bulletHole) - 1;

                if (score >= 0) {
                    int count = distribution[score] + 1;
                    distribution[score] = count;
                }
            }
        }

        List<Pair<Float, Float>> data = new ArrayList<>();

        for (int i = 0; i < distribution.length; i++) {
            Pair<Float, Float> pair = new Pair<>((float)(i + 1), (float)(distribution[i]));
            data.add(pair);
        }

        return data;
    }

    private PointF calculateBulletOffset(List<PrecisionSeries> precisionSeries) {
        PointF point = new PointF(0, 0);
        int nBulletHoles = 0;

        for (PrecisionSeries series : precisionSeries) {
            for (BulletHole bulletHole : series.getBulletHoles()) {
                PointF currentPoint = bulletHole.toCartesianCoordinates();
                point.x += currentPoint.x;
                point.y += currentPoint.y;

                nBulletHoles += 1;
            }
        }

        point.x /= nBulletHoles;
        point.y /= nBulletHoles;

        return point;
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
        if (mBulletOffset.x < 0 && mBulletOffset.y < 0) {
            formatString = getResources().getString(R.string.down_left_offset);
        } else if (mBulletOffset.x < 0 && mBulletOffset.y > 0) {
            formatString = getResources().getString(R.string.up_left_offset);
        } else if (mBulletOffset.x > 0 && mBulletOffset.y > 0) {
            formatString = getResources().getString(R.string.up_right_offset);
        } else /* if (mBulletOffset.x > 0 && mBulletOffset.y < 0) */ {
            formatString = getResources().getString(R.string.down_right_offset);
        }

        float xOffset = Math.abs(mBulletOffset.x);
        float yOffset = Math.abs(mBulletOffset.y);
        mOffsetText.setText(String.format(formatString, xOffset, yOffset));
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void onRestoreInstanceState(Bundle bundle) {

    }

    private float getSpread(BulletHole h1, BulletHole h2) {
        float r1 = h1.getRadius();
        float r2 = h2.getRadius();
        float a1 = h1.getAngle();
        float a2 = h2.getAngle();
        float d1 = h1.getCaliber().getDiameter();
        float d2 = h2.getCaliber().getDiameter();

        float x1 = (float)(r1 * Math.cos(a1));
        float y1 = (float)(r1 * Math.sin(a1));
        float x2 = (float)(r2 * Math.cos(a2));
        float y2 = (float)(r2 * Math.sin(a2));

        float tmp = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);

        return (float)Math.sqrt(tmp) + (d1 + d2) / 2;
    }

    private void display(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.precision_round_summary_layout_graph_container, fragment, null);
        transaction.commit();
    }
}
