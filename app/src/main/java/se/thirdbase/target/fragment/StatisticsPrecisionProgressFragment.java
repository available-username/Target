package se.thirdbase.target.fragment;

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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.db.PrecisionDBHelper;
import se.thirdbase.target.db.PrecisionSeriesContract;
import se.thirdbase.target.model.PrecisionSeries;

/**
 * Created by alexp on 2/25/16.
 */
public class StatisticsPrecisionProgressFragment extends Fragment {

    private static final String TAG = StatisticsPrecisionProgressFragment.class.getSimpleName();

    private static final String BUNDLE_TAG_PRECISION_SERIES = "BUNDLE_TAG_PRECISION_SERIES";

    private GraphView mGraphView;
    private List<PrecisionSeries> mPrecisionSeries;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        PrecisionDBHelper helper = PrecisionDBHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        mPrecisionSeries = getAllPrecisionSeries(db);
    }

    private LineGraphSeries<DataPoint> calculateProgressiveAverages(List<PrecisionSeries> precisionSeries) {

        int size = precisionSeries.size();
        DataPoint[] data = new DataPoint[size];

        float totalScore = 0;

        for (int i = 0; i < size; i++) {
            PrecisionSeries series = precisionSeries.get(i);

            totalScore += series.getScore();

            Date date = new Date(series.getTimestamp());

            //data[i] = new DataPoint(date, totalScore / (i + 1));
            data[i] = new DataPoint(i, totalScore / (i + 1));
        }

        return new LineGraphSeries<>(data);
    }

    private LineGraphSeries<DataPoint> getScores(List<PrecisionSeries> precisionSeries) {

        int size = precisionSeries.size();
        DataPoint[] data = new DataPoint[size];

        for (int i = 0; i < size; i++) {
            PrecisionSeries series = precisionSeries.get(i);

            float score = series.getScore();

            Date date = new Date(series.getTimestamp());

            //data[i] = new DataPoint(date, score);
            data[i] = new DataPoint(i, score);
        }

        return new LineGraphSeries<>(data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_precision_progress_layout, container, false);

        mGraphView = (GraphView) view.findViewById(R.id.statistics_precision_progress_layout_graph);

        LineGraphSeries<DataPoint> scores = getScores(mPrecisionSeries);
        scores.setTitle(getContext().getResources().getString(R.string.legend_score));
        scores.setColor(Color.RED);
        scores.setDrawDataPoints(true);

        mGraphView.addSeries(scores);

        LineGraphSeries<DataPoint> averages = calculateProgressiveAverages(mPrecisionSeries);
        averages.setTitle(getContext().getResources().getString(R.string.legend_average_score));
        averages.setColor(Color.BLUE);

        mGraphView.addSeries(averages);

        mGraphView.getLegendRenderer().setVisible(true);

        Log.d(TAG, "onCreateView exiting");

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static List<PrecisionSeries> getAllPrecisionSeries(SQLiteDatabase db) {
        String orderBy = String.format("datetime(%s) DESC", PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME);

        List<PrecisionSeries> precisionSeries = PrecisionSeriesContract.retrieveAllPrecisionSeries(db, orderBy);

        Log.d(TAG, "Length: " + precisionSeries.size());

        return precisionSeries;
    }
}
