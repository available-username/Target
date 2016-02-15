package se.thirdbase.target;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import se.thirdbase.target.fragment.StartupFragment;
import se.thirdbase.target.fragment.TargetFragment;
import se.thirdbase.target.view.TargetView;

public class MainActivity extends AppCompatActivity implements StateListener {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();

        Fragment fragment = TargetFragment.newInstance();
        //Fragment fragment = StartupFragment.newInstance();
        displayFragment(fragment, false);
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

    @Override
    public void onPrecision() {
        Fragment fragment = TargetFragment.newInstance();
        displayFragment(fragment, true);
    }

    @Override
    public void onStatistics() {

    }
}
