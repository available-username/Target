package se.thirdbase.target.fragment.precision;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.precision.PrecisionRound;
import se.thirdbase.target.model.precision.PrecisionSeries;
import se.thirdbase.target.model.precision.PrecisionTarget;

/**
 * Created by alexp on 3/1/16.
 */
public class PrecisionScoreDistributionFragment extends Fragment {

    private static final String BUNDLE_TAG_PRECISION_SERIES = "BUNDLE_TAG_PRECISION_SERIES";

    private BarGraphSeries<DataPoint> mBarGraphSeries;

    private PrecisionSeries[] mPrecisionSeries;

    public static PrecisionScoreDistributionFragment newInstance(PrecisionRound precisionRound) {
        return newInstance(precisionRound.getPrecisionSeries());
    }

    public static PrecisionScoreDistributionFragment newInstance(List<PrecisionSeries> precisionSeriesList) {
        int size = precisionSeriesList.size();
        PrecisionSeries[] precisionSeriesArray = new PrecisionSeries[size];

        precisionSeriesList.toArray(precisionSeriesArray);

        Bundle arguments = new Bundle();
        arguments.putParcelableArray(BUNDLE_TAG_PRECISION_SERIES, precisionSeriesArray);

        PrecisionScoreDistributionFragment fragment = new PrecisionScoreDistributionFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle arguments = getArguments();
        mPrecisionSeries = (PrecisionSeries[]) arguments.getParcelableArray(BUNDLE_TAG_PRECISION_SERIES);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.precision_point_distribution_layout, container, false);

        BarChart barChart = (BarChart)view.findViewById(R.id.precision_point_distribution_graph);

        BarDataSet scoreDistribution = calculatePointsDistribution(mPrecisionSeries);
        List<String> axis = getXAxisLabels();

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(scoreDistribution);

        BarData data = new BarData(axis, dataSets);
        barChart.setData(data);
        barChart.invalidate();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private List<String> getXAxisLabels() {
        List<String> axis = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            axis.add("" + i);
        }

        return axis;
    }

    private BarDataSet calculatePointsDistribution(PrecisionSeries[] precisionSeries) {
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

        List<BarEntry> entires = new ArrayList<>();

        for (int i = 0; i < distribution.length; i++) {
            BarEntry entry = new BarEntry(distribution[i], i);
            entires.add(entry);
        }

        return new BarDataSet(entires, "Poäng");
    }
}
