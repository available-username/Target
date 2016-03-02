package se.thirdbase.target.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.db.TargetDBHelper;
import se.thirdbase.target.fragment.StartupFragment;
import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.precision.PrecisionRound;
import se.thirdbase.target.model.precision.PrecisionSeries;

public class MainActivity extends BaseActivity implements StateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BACK_STACK_TAG_STARTUP = "BACK_STACK_TAG_STARTUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            onStartup();
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
    public void onWeapons() {
        Log.d(TAG, "onWeapons()");

        Intent intent = new Intent(this, WeaponsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAmmunition() {
        Log.d(TAG, "onAmmunition()");

        Intent intent = new Intent(this, AmmunitionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStatistics() {
        Log.d(TAG, "onStatistics()");

        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    private void dumpDB() {
        TargetDBHelper helper = TargetDBHelper.getInstance(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        List<PrecisionRound> precisionRoundList = PrecisionRound.fetchAll(db, null);

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
