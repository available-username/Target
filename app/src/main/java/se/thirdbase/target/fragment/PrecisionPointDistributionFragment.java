package se.thirdbase.target.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;
import se.thirdbase.target.model.PrecisionTarget;
import se.thirdbase.target.view.GraphView;

/**
 * Created by alexp on 2/25/16.
 */
public class PrecisionPointDistributionFragment extends Fragment {

    private static final String BUNDLE_TAG_PRECISION_SERIES = "BUNDLE_TAG_PRECISION_SERIES";

    public static PrecisionPointDistributionFragment newInstance(PrecisionRound precisionRound) {
        return newInstance(precisionRound.getPrecisionSeries());
    }

    public static PrecisionPointDistributionFragment newInstance(List<PrecisionSeries> precisionSeriesList) {
        int size = precisionSeriesList.size();
        PrecisionSeries[] precisionSeriesArray = new PrecisionSeries[size];

        precisionSeriesList.toArray(precisionSeriesArray);

        Bundle arguments = new Bundle();
        arguments.putParcelableArray(BUNDLE_TAG_PRECISION_SERIES, precisionSeriesArray);

        PrecisionPointDistributionFragment fragment = new PrecisionPointDistributionFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    private List<Pair<Float, Float>> mScoreDistribution = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle arguments = getArguments();
        PrecisionSeries[] precisionSeries = (PrecisionSeries[]) arguments.getParcelableArray(BUNDLE_TAG_PRECISION_SERIES);

        mScoreDistribution = calculatePointsDistribution(precisionSeries);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.precision_point_distribution_layout, container, false);

        GraphView mGraph = (GraphView) view.findViewById(R.id.precision_point_distribution_graph);
        mGraph.addDataPoints(mScoreDistribution);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private List<Pair<Float, Float>> calculatePointsDistribution(PrecisionSeries[] precisionSeries) {
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
}
