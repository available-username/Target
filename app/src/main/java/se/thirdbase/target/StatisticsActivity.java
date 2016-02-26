package se.thirdbase.target;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import se.thirdbase.target.fragment.StatisticsPrecisionFragment;
import se.thirdbase.target.fragment.StatisticsPrecisionProgressFragment;

/**
 * Created by alexp on 2/24/16.
 */
public class StatisticsActivity extends BaseActivity implements StatisticsStateListener {

    private static final String BACK_STACK_TAG_PRECISION_FRAGMENT = "BACK_STACK_TAG_PRECISION_FRAGMENT";
    private static final String BACK_STACK_TAG_PRECISION_PROGRESS_FRAGMENT = "BACK_STACK_TAG_PRECISION_PROGRESS_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Fragment fragment = StatisticsPrecisionFragment.newInstance();
        displayFragment(fragment, false, BACK_STACK_TAG_PRECISION_FRAGMENT);
    }

    @Override
    public int getLayoutContainerId() {
        return R.id.statistics_layout_id;
    }

    @Override
    public void onOverview() {

    }

    @Override
    public void onPrecision() {

    }

    @Override
    public void onPrecisionProgress() {
        Fragment fragment = new StatisticsPrecisionProgressFragment();
        displayFragment(fragment, true, BACK_STACK_TAG_PRECISION_PROGRESS_FRAGMENT);
    }
}
