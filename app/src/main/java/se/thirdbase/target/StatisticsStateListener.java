package se.thirdbase.target;

import se.thirdbase.target.model.PrecisionRound;

/**
 * Created by alexp on 2/24/16.
 */
public interface StatisticsStateListener {

    void onOverview();

    void onPrecision();

    void onPrecisionProgress();

    void onPrecisionRoundSummary(PrecisionRound precisionRound);
}
