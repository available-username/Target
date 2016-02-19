package se.thirdbase.target;

import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

/**
 * Created by alexp on 2/12/16.
 */
public interface StateListener {

    void onStartup();

    void onPrecision();

    void onUpdatePrecisionSeries(PrecisionSeries precisionSeries);

    void onPrecisionSeriesUpdated();

    void onPrecisionSeriesComplete(PrecisionSeries precisionSeries);

    void onPrecisionRoundComplete(PrecisionRound precisionRound);

    void onStatistics();
}
