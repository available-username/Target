package se.thirdbase.target.activity;

import java.util.List;

import se.thirdbase.target.model.precision.PrecisionRound;
import se.thirdbase.target.model.precision.PrecisionSeries;

/**
 * Created by alexp on 2/19/16.
 */
public interface PrecisionStateListener {

    void onPrecisionStartCompetitionRound();

    void onPrecisionStartTrainingRound();

    void onPrecisionStartUnboundRound();

    void onPrecisionSeriesUpdate(PrecisionSeries precisionSeries);

    void onPrecisionSeriesUpdated(PrecisionSeries precisionSeries);

    void onPrecisionSeriesComplete(PrecisionSeries precisionSeries);

    void onPrecisionRoundComplete(PrecisionRound precisionRound);
}
