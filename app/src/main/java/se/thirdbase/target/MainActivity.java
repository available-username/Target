package se.thirdbase.target;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import se.thirdbase.target.fragment.PrecisionRoundFragment;
import se.thirdbase.target.fragment.StartupFragment;
import se.thirdbase.target.fragment.TargetFragment;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

public class MainActivity extends AppCompatActivity implements StateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BACK_STACK_TAG_STARTUP = "BACK_STACK_TAG_STARTUP";
    private static final String BACK_STACK_TAG_PRECISION_ROUND = "BACK_STACK_TAG_PRECISION_ROUND";
    private static final String BACK_STACK_TAG_PRECISION_SERIES = "BACK_STACK_TAG_PRECISION_SERIES";


    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(mBackStackChangedListener);

        if (savedInstanceState == null) {
            onStartup();
        }
    }

    private void displayFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        transaction.replace(R.id.main_layout_id, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    @Override
    public void onStartup() {
        Log.d(TAG, "onStartup()");

        Fragment fragment = StartupFragment.newInstance();
        displayFragment(fragment, false, BACK_STACK_TAG_STARTUP);
    }

    private PrecisionRound mPrecisionRound;

    @Override
    public void onPrecision() {
        Log.d(TAG, "onPrecision()");

        mPrecisionRound = new PrecisionRound();

        Fragment roundFragment = PrecisionRoundFragment.newInstance(mPrecisionRound);
        displayFragment(roundFragment, true, BACK_STACK_TAG_PRECISION_ROUND);

        Fragment seriesFragment = TargetFragment.newInstance();
        displayFragment(seriesFragment, true, BACK_STACK_TAG_PRECISION_SERIES);
    }

    @Override
    public void onUpdatePrecisionSeries(PrecisionSeries precisionSeries) {
        Log.d(TAG, "onUpdatePrecisionSeries()");

        Fragment fragment = TargetFragment.newInstance(precisionSeries);

        displayFragment(fragment, true, BACK_STACK_TAG_PRECISION_SERIES);
    }

    @Override
    public void onPrecisionSeriesUpdated() {
        Log.d(TAG, "onPrecisionSeriesUpdated()");

        mFragmentManager.popBackStack();
    }

    @Override
    public void onPrecisionSeriesComplete(PrecisionSeries precisionSeries) {
        Log.d(TAG, "onPrecisionSeriesComplete()");

        mPrecisionRound.addPrecisionSeries(precisionSeries);
        mFragmentManager.popBackStack();
    }

    @Override
    public void onPrecisionRoundComplete(PrecisionRound precisionRound) {
        Log.d(TAG, "onPrecisionRoundComplete()");

        mPrecisionRound = null;

        onStartup();
    }

    @Override
    public void onStatistics() {
        Log.d(TAG, "onStatistics()");
    }

    private FragmentManager.OnBackStackChangedListener mBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {

        @Override
        public void onBackStackChanged() {
            logBackStack();
        }
    };

    private void logBackStack() {
        int depth = mFragmentManager.getBackStackEntryCount();

        for (int i = 0; i < depth; i++) {
            String indent = "";

            for (int j = 0; j < i; j++) {
                indent += "    ";
            }
            Log.d(TAG, String.format("%d: %s%s", i, indent, mFragmentManager.getBackStackEntryAt(i).getName()));
        }
    }
}
