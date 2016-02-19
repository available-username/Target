package se.thirdbase.target;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import se.thirdbase.target.fragment.StartupFragment;

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
    public void onStatistics() {
        Log.d(TAG, "onStatistics()");
    }
}
