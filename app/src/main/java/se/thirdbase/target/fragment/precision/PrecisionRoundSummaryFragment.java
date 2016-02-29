package se.thirdbase.target.fragment.precision;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;
import se.thirdbase.target.model.PrecisionTarget;

/**
 * Created by alexp on 2/19/16.
 */
public class PrecisionRoundSummaryFragment extends PrecisionBaseFragment {

    private static final String TAG = PrecisionRoundSummaryFragment.class.getSimpleName();

    private static final String BUNDLE_TAG_PRECISION_ROUND = "BUNDLE_TAG_PRECISION_ROUND";

    private TextView mScoreText;
    private TextView mMaxSpreadText;
    private TextView mAvgSpreadText;
    private Button mPointsDistributionButton;
    private Button mHitsDistributionButton;

    private PrecisionRound mPrecisionRound;
    private int mScore = 0;
    private int mNbrBullets = 0;
    private float mMaxSpread = 0;
    private float mAvgSpread = 0;
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
        mScore = calculateScore(precisionSeries);
        mScoreDistribution = calculateDistribution(precisionSeries);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.precision_round_summary_layout, container, false);

        if (savedInstanceState != null)  {
            onRestoreInstanceState(savedInstanceState);
        }

        mScoreText = (TextView) view.findViewById(R.id.precision_round_summary_layout_score);
        mScoreText.setText("" + mScore);

        mMaxSpreadText = (TextView) view.findViewById(R.id.precision_round_summary_layout_max_spread);
        mMaxSpreadText.setText(String.format("%.2fcm", mMaxSpread));

        mAvgSpreadText = (TextView) view.findViewById(R.id.precision_round_summary_layout_avg_spread);
        mAvgSpreadText.setText(String.format("%.2fcm", mAvgSpread));

        /*
        mDistributionGraphView = (GraphView) view.findViewById(R.id.precision_round_summary_layout_graph_view);
        mDistributionGraphView.addDataPoints(mScoreDistribution);

        mPrecisionTargetView = (PrecisionTargetHeatMapView) view.findViewById(R.id.precision_round_summary_layout_target_view);
        */

        mPointsDistributionButton = (Button) view.findViewById(R.id.precision_round_summary_layout_points_distribution_button);

        mPointsDistributionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrecisionRoundPointsDistribution(mPrecisionRound);
            }
        });

        mHitsDistributionButton = (Button) view.findViewById(R.id.precision_round_summary_layout_hits_distribution_button);

        mHitsDistributionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrecisionRoundHitsDistribution(mPrecisionRound);
            }
        });

        updateTextFields();

        return view;
    }

    private void updateTextFields() {
        mScoreText.setText(getResources().getString(R.string.points, mScore));
        mMaxSpreadText.setText(getResources().getString(R.string.max_spread, mMaxSpread));
        mAvgSpreadText.setText(getResources().getString(R.string.mean_spread, mAvgSpread));
    }

    private void addBulletsToView() {
        List<BulletHole> allHoles = new ArrayList<>();

        for (PrecisionSeries series : mPrecisionRound.getPrecisionSeries()) {
            List<BulletHole> holes = series.getBulletHoles();
            allHoles.addAll(holes);
        }

        //mPrecisionTargetView.setBulletHoles(allHoles);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
}