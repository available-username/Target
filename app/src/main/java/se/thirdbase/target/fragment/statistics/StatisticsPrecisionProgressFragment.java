package se.thirdbase.target.fragment.statistics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.db.PrecisionSeriesContract;
import se.thirdbase.target.db.TargetDBHelper;
import se.thirdbase.target.model.precision.PrecisionSeries;

/**
 * Created by alexp on 3/1/16.
 */
public class StatisticsPrecisionProgressFragment extends Fragment {


    private static final String TAG = StatisticsPrecisionProgressFragment.class.getSimpleName();

    private static final String BUNDLE_TAG_PRECISION_SERIES = "BUNDLE_TAG_PRECISION_SERIES";

    private LineChart mLineChart;
    private List<PrecisionSeries> mPrecisionSeries;

    public static StatisticsPrecisionProgressFragment newInstance() {
        return new StatisticsPrecisionProgressFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        TargetDBHelper helper = TargetDBHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        mPrecisionSeries = getAllPrecisionSeries(db);
    }

    private LineDataSet calculateProgressiveAverages(List<PrecisionSeries> precisionSeries) {

        List<Entry> entries = new ArrayList<>();
        int size = precisionSeries.size();

        float totalScore = 0;

        for (int i = 0; i < size; i++) {
            PrecisionSeries series = precisionSeries.get(i);

            totalScore += series.getScore();

            Entry entry = new Entry(totalScore / (i + 1), i);
            entries.add(entry);
        }

        return new LineDataSet(entries, "Medel");
    }

    private LineDataSet getScores(List<PrecisionSeries> precisionSeries) {

        List<Entry> entries = new ArrayList<>();
        int size = precisionSeries.size();

        for (int i = 0; i < size; i++) {
            PrecisionSeries series = precisionSeries.get(i);

            float score = series.getScore();

            Entry entry = new Entry(score, i);
            entries.add(entry);
        }

        return new LineDataSet(entries, "Poäng");
    }

    private List<String> getXAxisLabels(List<PrecisionSeries> precisionSeries) {
        List<String> dates = new ArrayList<>();

        for (PrecisionSeries series : precisionSeries) {
            Date date = new Date(series.getTimestamp());

            dates.add(date.toString());
        }

        return dates;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_precision_progress_layout, container, false);

        mLineChart = (LineChart) view.findViewById(R.id.statistics_precision_progress_layout_graph);

        LineDataSet scoreSet = getScores(mPrecisionSeries);
        scoreSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        scoreSet.setColor(Color.RED);


        LineDataSet avgSet = calculateProgressiveAverages(mPrecisionSeries);
        avgSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        avgSet.setDrawCubic(true);
        avgSet.setDrawCircles(false);
        avgSet.setColor(Color.BLUE);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(scoreSet);
        dataSets.add(avgSet);

        List<String> axisLabels = getXAxisLabels(mPrecisionSeries);

        LineData data = new LineData(axisLabels, dataSets);
        mLineChart.setData(data);
        mLineChart.invalidate();

        Log.d(TAG, "onCreateView exiting");

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static List<PrecisionSeries> getAllPrecisionSeries(SQLiteDatabase db) {
        String orderBy = String.format("datetime(%s) DESC", PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME);

        List<PrecisionSeries> precisionSeries = PrecisionSeries.fetchAll(db, orderBy);

        Log.d(TAG, "Length: " + precisionSeries.size());

        return precisionSeries;
    }
}
