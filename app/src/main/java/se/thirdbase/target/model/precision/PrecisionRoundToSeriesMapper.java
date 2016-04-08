package se.thirdbase.target.model.precision;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.db.PrecisionRoundToSeriesContract;

/**
 * Created by alex on 4/5/16.
 */
public class PrecisionRoundToSeriesMapper {

    public static void store(SQLiteDatabase db, PrecisionRound precisionRound) {
        List<PrecisionSeries> precisionSeries = precisionRound.getPrecisionSeries();

        ContentValues values = new ContentValues();

        int nbrSeries = precisionSeries.size();
        for (int i = 0; i < nbrSeries; i++) {
            PrecisionSeries series = precisionSeries.get(i);

            values = new ContentValues();
            values.put(PrecisionRoundToSeriesContract.PrecisionRoundToSeriesEntry.COLUMN_NAME_ROUND, precisionRound.getDBHandle());
            values.put(PrecisionRoundToSeriesContract.PrecisionRoundToSeriesEntry.COLUMN_NAME_SERIES, series.getDBHandle());
            values.put(PrecisionRoundToSeriesContract.PrecisionRoundToSeriesEntry.COLUMN_NAME_ORDERING, i);

            db.insert(PrecisionRoundToSeriesContract.TABLE_NAME, null, values);
        }

    }

    public static List<PrecisionSeries> fetch(SQLiteDatabase db, long precisionRoundId) {
        String[] columns = {
                PrecisionRoundToSeriesContract.PrecisionRoundToSeriesEntry.COLUMN_NAME_SERIES,
        };

        String selection = PrecisionRoundToSeriesContract.PrecisionRoundToSeriesEntry.COLUMN_NAME_ROUND + "=?";
        String selectionArgs[] = { "" + precisionRoundId };

        Cursor cursor = db.query(PrecisionRoundToSeriesContract.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null, //having
                null, //groupBy
                PrecisionRoundToSeriesContract.PrecisionRoundToSeriesEntry.COLUMN_NAME_ORDERING);

        List<PrecisionSeries> precisionSeries = null;

        if (cursor != null && cursor.moveToFirst()) {
            try {
                precisionSeries = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    long precisionSeriesId = cursor.getLong(cursor.getColumnIndex(PrecisionRoundToSeriesContract.PrecisionRoundToSeriesEntry.COLUMN_NAME_SERIES));
                    PrecisionSeries series = PrecisionSeries.fetch(db, precisionSeriesId);

                    precisionSeries.add(series);

                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }

        return precisionSeries;
    }
}
