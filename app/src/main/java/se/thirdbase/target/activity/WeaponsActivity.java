package se.thirdbase.target.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import se.thirdbase.target.R;
import se.thirdbase.target.db.TargetDBHelper;
import se.thirdbase.target.fragment.weapon.WeaponsAddFragment;
import se.thirdbase.target.fragment.weapon.WeaponsDisplayFragment;
import se.thirdbase.target.fragment.weapon.WeaponsFragment;
import se.thirdbase.target.model.Weapon;

/**
 * Created by alexp on 2/29/16.
 */
public class WeaponsActivity extends BaseActivity implements WeaponsStateListener {

    private static final String TAG = WeaponsActivity.class.getSimpleName();

    private static final String BACK_STACK_TAG_WEAPONS_FRAGMENT = "BACK_STACK_TAG_WEAPONS_FRAGMENT";
    private static final String BACK_STACK_TAG_WEAPONS_ADD_FRAGMENT = "BACK_STACK_TAG_WEAPONS_ADD_FRAGMENT";
    private static final String BACK_STACK_TAG_WEAPONS_DISPLAY_FRAGMENT = "BACK_STACK_TAG_WEAPONS_DISPLAY_FRAGMENT";

    private SQLiteDatabase mSQLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapons);

        TargetDBHelper dbHelper = TargetDBHelper.getInstance(this);
        mSQLiteDatabase = dbHelper.getWritableDatabase();

        if (savedInstanceState == null) {
            onOverview();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
            mSQLiteDatabase = null;
        }
    }

    @Override
    public int getLayoutContainerId() {
        return R.id.weapons_activity_id;
    }

    @Override
    public void onOverview() {
        Log.d(TAG, "onOverview()");
        Fragment fragment = WeaponsFragment.newInstance();
        displayFragment(fragment, false, BACK_STACK_TAG_WEAPONS_FRAGMENT);
    }

    @Override
    public void onAdd() {
        Log.d(TAG, "onAdd()");

        Fragment fragment = WeaponsAddFragment.newInstance();
        displayFragment(fragment, true, BACK_STACK_TAG_WEAPONS_ADD_FRAGMENT);
    }

    @Override
    public void onAdded(Weapon weapon) {
        Log.d(TAG, "onAdded()");

        weapon.store(mSQLiteDatabase);

        popBackStack();
    }

    @Override
    public void onDisplay(Weapon weapon) {
        Log.d(TAG, "onDisplay()");

        Fragment fragment = WeaponsDisplayFragment.newInstance(weapon);
        displayFragment(fragment, true, BACK_STACK_TAG_WEAPONS_DISPLAY_FRAGMENT);
    }
}
