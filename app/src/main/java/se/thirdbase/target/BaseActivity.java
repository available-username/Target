package se.thirdbase.target;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by alexp on 2/19/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(mBackStackChangedListener);
    }

    public abstract int getLayoutContainerId();

    protected void displayFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        transaction.replace(getLayoutContainerId(), fragment);

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    protected void popBackStack() {
        mFragmentManager.popBackStack();
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
