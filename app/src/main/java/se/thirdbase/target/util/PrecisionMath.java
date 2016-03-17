package se.thirdbase.target.util;

import android.graphics.PointF;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.precision.PrecisionRound;
import se.thirdbase.target.model.precision.PrecisionSeries;
import se.thirdbase.target.model.precision.PrecisionTarget;

/**
 * Created by alex on 3/15/16.
 */
public final class PrecisionMath {

    private double mMaxSpread;
    private double mAvgSpread;
    private int mScore;
    List<Pair<Float, Float>> mScoreDistribution = new ArrayList<>();
    private PointF mHitMean = new PointF(0, 0);
    private PointF mHitStd = new PointF();

    public PrecisionMath(PrecisionRound precisionRound) {
        this(precisionRound.getPrecisionSeries());
    }

    public PrecisionMath(List<PrecisionSeries> precisionSeries) {
        calculateSpreads(precisionSeries);
        calculateMeansAndScore(precisionSeries);
        calculateStandardDeviation(precisionSeries);
    }

    public double getMaxSpread() {
        return mMaxSpread;
    }

    public double getAverageSpread() {
        return mAvgSpread;
    }

    public int getScore() {
        return mScore;
    }

    public List<Pair<Float, Float>> getScoreDistribution() {
        return Collections.unmodifiableList(mScoreDistribution);
    }

    public PointF getHitMean() {
        return new PointF(mHitMean.x, mHitMean.y);
    }

    public PointF getHitStd() {
        return new PointF(mHitStd.x, mHitStd.y);
    }

    private void calculateSpreads(List<PrecisionSeries> precisionSeries) {

        int nbrSeries = precisionSeries.size();
        int nbrDistances = 0;

        for (int i = 0; i < nbrSeries; i++) {
            PrecisionSeries series = precisionSeries.get(i);

            List<BulletHole> bulletHoles = series.getBulletHoles();
            int nbrBullets = bulletHoles.size();

            for (int j = 0; j < nbrBullets - 1; j++) {
                for (int k = j + 1; k < nbrBullets; k++) {

                    double distance = getSpread(bulletHoles.get(j), bulletHoles.get(k));

                    /* Max spread calculation */
                    if (distance > mMaxSpread) {
                        mMaxSpread = distance;
                    }

                    /* Average spread calculation */
                    mAvgSpread += distance;
                    nbrDistances += 1;
                }
            }
        }

        /* Average spread calculation */
        mAvgSpread = nbrDistances > 0 ? mAvgSpread / nbrDistances : 0;
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


    private void calculateMeansAndScore(List<PrecisionSeries> precisionSeries) {
        int[] distribution = new int[10];
        int nbrSeries = precisionSeries.size();
        int nbrBullets = 0;

        for (int i = 0; i < nbrSeries; i++) {
            PrecisionSeries series = precisionSeries.get(i);

            /* Score calculation */
            mScore += series.getScore();

            for (BulletHole bulletHole : series.getBulletHoles()) {
                int score = PrecisionTarget.getBulletScore(bulletHole) - 1;

                /* Distribution calculation */
                if (score >= 0) {
                    int count = distribution[score] + 1;
                    distribution[score] = count;
                }

                /* Hit mean calculation */
                PointF currentPoint = bulletHole.toCartesianCoordinates();
                mHitMean.x += currentPoint.x;
                mHitMean.y += currentPoint.y;

                nbrBullets += 1;
            }
        }

        /* Distribution calculation */
        for (int i = 0; i < distribution.length; i++) {
            Pair<Float, Float> pair = new Pair<>((float)(i + 1), (float)(distribution[i]));
            mScoreDistribution.add(pair);
        }

        /* Hit mean calculation */
        if (nbrBullets > 0) {
            mHitMean.x /= nbrBullets;
            mHitMean.y /= nbrBullets;
        } else {
            mHitMean.x = mHitMean.y = 0;
        }
    }

    /* Must be called after 'calculateMeansAndScore' */
    private void calculateStandardDeviation(List<PrecisionSeries> precisionSeries) {
        PointF point = new PointF(0, 0);
        int nbrBullets = 0;

        for (PrecisionSeries series : precisionSeries) {
            for (BulletHole bulletHole : series.getBulletHoles()) {
                PointF currentPoint = bulletHole.toCartesianCoordinates();

                float dx = currentPoint.x - mHitMean.x;
                float dy = currentPoint.y - mHitMean.y;

                point.x += dx * dx;
                point.y += dy * dy;

                nbrBullets += 1;
            }
        }

        if (nbrBullets > 0) {
            point.x /= nbrBullets;
            point.y /= nbrBullets;

            mHitStd.x = (float)Math.sqrt(point.x);
            mHitStd.y = (float)Math.sqrt(point.y);
        }
    }
}
