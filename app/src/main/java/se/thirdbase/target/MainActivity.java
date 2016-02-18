package se.thirdbase.target;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import se.thirdbase.target.fragment.PrecisionRoundFragment;
import se.thirdbase.target.fragment.StartupFragment;
import se.thirdbase.target.fragment.TargetFragment;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

public class MainActivity extends AppCompatActivity implements StateListener {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            Fragment fragment = StartupFragment.newInstance();
            displayFragment(fragment, false);
        }
    }

    private void displayFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        transaction.replace(R.id.main_layout_id, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public void onStartup() {
        Fragment fragment = StartupFragment.newInstance();
        displayFragment(fragment, true);
    }

    private PrecisionRound mPrecisionRound;

    @Override
    public void onPrecision() {
        Fragment fragment = TargetFragment.newInstance();

        displayFragment(fragment, true);
    }

    @Override
    public void onPrecisionSeriesComplete(PrecisionSeries precisionSeries) {
        if (mPrecisionRound == null) {
            mPrecisionRound = new PrecisionRound();
        }

        mPrecisionRound.addPrecisionSeries(precisionSeries);

        Fragment fragment = PrecisionRoundFragment.newInstance(mPrecisionRound);

        displayFragment(fragment, true);
    }

    @Override
    public void onPrecisionRoundComplete(PrecisionRound precisionRound) {
        mPrecisionRound = null;
    }

    @Override
    public void onStatistics() {

    }
}
