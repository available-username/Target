package se.thirdbase.target.activity;

import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

/**
 * Created by alexp on 2/19/16.
 */
public interface PrecisionStateListener {

    void onUpdatePrecisionSeries(PrecisionSeries precisionSeries);

    void onPrecisionSeriesUpdated(PrecisionSeries precisionSeries);

    void onPrecisionSeriesComplete(PrecisionSeries precisionSeries);

    void onPrecisionRoundComplete(PrecisionRound precisionRound);
}