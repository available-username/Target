package se.thirdbase.target.fragment;

import se.thirdbase.target.model.PrecisionRound;

/**
 * Created by alexp on 2/26/16.
 */
public interface  PrecisionRoundSummaryListener {

    void onPrecisionRoundScoreDistribution(PrecisionRound precisionRound);

    void onPrecisionRoundHitDistribution(PrecisionRound precisionRound);
}
