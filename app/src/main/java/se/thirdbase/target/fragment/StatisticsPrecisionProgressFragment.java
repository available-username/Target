package se.thirdbase.target.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import se.thirdbase.target.db.PrecisionDBHelper;
import se.thirdbase.target.db.PrecisionSeriesContract;
import se.thirdbase.target.model.PrecisionSeries;
import se.thirdbase.target.view.GraphView;

/**
 * Created by alexp on 2/25/16.
 */
public class StatisticsPrecisionProgressFragment extends Fragment {

    private static final String BUNDLE_TAG_PRECISION_SERIES = "BUNDLE_TAG_PRECISION_SERIES";

    private GraphView mGraphView;
    private List<Integer> mScores;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        PrecisionDBHelper helper = PrecisionDBHelper.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        mScores = getPrecisionSeriesScores(db);
    }

    private List<Pair<Float, Float>> calculateProgressiveAverages(List<Integer> scores) {
        List<Pair<Float, Float>> data = new ArrayList<>();

        int size = scores.size();
        float totalScore = 0;

        for (int i = 0; i < size; i++) {
            totalScore += scores.get(i);

            Pair<Float, Float> pair = new Pair<>((float)i, totalScore / (i + 1));
            data.add(pair);
        }

        return data;
    }

    private List<Pair<Float, Float>> getScores(List<Integer> scores) {
        List<Pair<Float, Float>> data = new ArrayList<>();

        int size = scores.size();

        for (int i = 0; i < size; i++) {
            int score = scores.get(i);

            Pair<Float, Float> pair = new Pair<>((float)i, (float)score);
            data.add(pair);
        }

        return data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_precision_progress_layout, container, false);

        List<Pair<Float, Float>> progressiveAverages = calculateProgressiveAverages(mScores);
        List<Pair<Float, Float>> scores = getScores(mScores);

        mGraphView = (GraphView) view.findViewById(R.id.statistics_precision_progress_layout_graph);

        mGraphView.addDataPoints(progressiveAverages);
        mGraphView.addDataPoints(scores);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static List<Integer> getPrecisionSeriesScores(SQLiteDatabase db) {
        String[] columns = {
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_SCORE,
                PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME
        };

        Cursor cursor = db.query(PrecisionSeriesContract.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                String.format("datetime(%s) DESC", PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_DATE_TIME), //orderBy,
                null);

        List<Integer> scores = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {

            try {
                while (!cursor.isAfterLast()) {
                    int score = cursor.getInt(cursor.getColumnIndex(PrecisionSeriesContract.PrecisionSeriesEntry.COLUMN_NAME_SCORE));
                    scores.add(score);
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }

        return scores;
    }
}
