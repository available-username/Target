package se.thirdbase.target.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexp on 2/18/16.
 */
public class PrecisionRound {

    private List<PrecisionSeries> mPrecisionSeries;
    private int mScore;

    public PrecisionRound() {
        mPrecisionSeries = new ArrayList<>();
    }

    public PrecisionRound(List<PrecisionSeries> precisionSeries) {
        mPrecisionSeries = precisionSeries;
        mScore = calculateScore(precisionSeries);
    }

    public void addPrecisionSeries(PrecisionSeries precisionSeries) {
        mPrecisionSeries.add(precisionSeries);
        mScore = calculateScore(mPrecisionSeries);
    }

    public void setPrecisionSeries(List<PrecisionSeries> precisionSeries) {
        mPrecisionSeries = precisionSeries;
        mScore = calculateScore(precisionSeries);
    }

    public List<PrecisionSeries> getPrecisionSeries() {
        return Collections.unmodifiableList(mPrecisionSeries);
    }

    public int getScore() {
        return mScore;
    }

    private int calculateScore(List<PrecisionSeries> precisionSeries) {
        int score = 0;

        for (PrecisionSeries series : precisionSeries) {
            score = series.getScore();
        }

        return score;
    }
}
