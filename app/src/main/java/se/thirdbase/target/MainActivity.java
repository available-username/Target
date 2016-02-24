package se.thirdbase.target;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import se.thirdbase.target.db.PrecisionDBHelper;
import se.thirdbase.target.db.PrecisionRoundContract;
import se.thirdbase.target.fragment.StartupFragment;
import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

public class MainActivity extends BaseActivity implements StateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BACK_STACK_TAG_STARTUP = "BACK_STACK_TAG_STARTUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            onStartup();

            dumpDB();
        }
    }

    @Override
    public int getLayoutContainerId() {
        return R.id.main_layout_id;
    }

    @Override
    public void onStartup() {
        Log.d(TAG, "onStartup()");

        Fragment fragment = StartupFragment.newInstance();
        displayFragment(fragment, false, BACK_STACK_TAG_STARTUP);
    }

    @Override
    public void onPrecision() {
        Log.d(TAG, "onPrecision()");

        Intent intent = new Intent(this, PrecisionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStatistics() {
        Log.d(TAG, "onStatistics()");
    }

    private void dumpDB() {
        PrecisionDBHelper helper = PrecisionDBHelper.getInstance(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        List<PrecisionRound> precisionRoundList = PrecisionRoundContract.retrieveAllPrecisionRounds(db);

        for (PrecisionRound round : precisionRoundList) {
            System.out.println(round);
            for (PrecisionSeries series : round.getPrecisionSeries()) {
                System.out.println(series);
                for (BulletHole hole : series.getBulletHoles()) {
                    System.out.println(hole);
                }
            }
        }
    }
}
