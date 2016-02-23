package se.thirdbase.target;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;

import se.thirdbase.target.db.PrecisionDBHelper;
import se.thirdbase.target.db.PrecisionRoundContract;
import se.thirdbase.target.fragment.PrecisionRoundFragment;
import se.thirdbase.target.fragment.PrecisionRoundSummaryFragment;
import se.thirdbase.target.fragment.PrecisionTargetFragment;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

public class PrecisionActivity extends BaseActivity implements PrecisionStateListener{

    private static final String TAG = PrecisionActivity.class.getSimpleName();

    private static final String BACK_STACK_TAG_PRECISION_ROUND = "BACK_STACK_TAG_PRECISION_ROUND";
    private static final String BACK_STACK_TAG_PRECISION_SERIES = "BACK_STACK_TAG_PRECISION_SERIES";
    private static final String BACK_STACK_TAG_PRECISION_SUMMARY = "BACK_STACK_TAG_PRECISION_SUMMARY";

    private PrecisionRound mPrecisionRound = new PrecisionRound();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precision);

        Fragment roundFragment = PrecisionRoundFragment.newInstance(mPrecisionRound);
        displayFragment(roundFragment, false, BACK_STACK_TAG_PRECISION_ROUND);

        Fragment seriesFragment = PrecisionTargetFragment.newInstance();
        displayFragment(seriesFragment, true, BACK_STACK_TAG_PRECISION_SERIES);
        /*
        Fragment fragment = PrecisionRoundSummaryFragment.newInstance(mPrecisionRound);
        displayFragment(fragment, false, BACK_STACK_TAG_PRECISION_SUMMARY);
        */
    }

    @Override
    public int getLayoutContainerId() {
        return R.id.precision_layout_id;
    }

    @Override
    public void onUpdatePrecisionSeries(PrecisionSeries precisionSeries) {
        Log.d(TAG, "onUpdatePrecisionSeries()");

        Fragment fragment = PrecisionTargetFragment.newInstance(precisionSeries);

        displayFragment(fragment, true, BACK_STACK_TAG_PRECISION_SERIES);
    }

    @Override
    public void onPrecisionSeriesUpdated() {
        Log.d(TAG, "onPrecisionSeriesUpdated()");

        popBackStack();
    }

    @Override
    public void onPrecisionSeriesComplete(PrecisionSeries precisionSeries) {
        Log.d(TAG, "onPrecisionSeriesComplete()");

        mPrecisionRound.addPrecisionSeries(precisionSeries);

        if (mPrecisionRound.getNbrSeries() == PrecisionRound.MAX_NBR_SERIES) {
            onPrecisionRoundComplete(mPrecisionRound);
        } else {
            popBackStack();
        }
    }

    @Override
    public void onPrecisionRoundComplete(PrecisionRound precisionRound) {
        Log.d(TAG, "onPrecisionRoundComplete()");

        PrecisionDBHelper dbHelper = PrecisionDBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        PrecisionRoundContract.storePrecisionRound(db, precisionRound);

        dump();

        popBackStack();
        Fragment fragment = PrecisionRoundSummaryFragment.newInstance(mPrecisionRound);
        displayFragment(fragment, false, BACK_STACK_TAG_PRECISION_SUMMARY);
    }

    private void dump() {
        try {
            //File sd = Environment.getExternalStorageDirectory();
            //Environment.getEnvi
            File sd = new File(Environment.DIRECTORY_DOWNLOADS);
            File data = Environment.getDataDirectory();

            Log.d(TAG,  "sd.canWrite(): " + (sd.canWrite() ? "true" : "false"));
            if (sd.canWrite()) {
                String currentDBPath = "//data//se.thirdbase.target//databases//Precision.db";
                String backupDBPath = "Precision.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                Log.d(TAG,  "currentDB.exists(): " + (currentDB.exists() ? "true" : "false"));
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
