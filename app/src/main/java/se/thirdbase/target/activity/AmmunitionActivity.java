package se.thirdbase.target.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import se.thirdbase.target.R;
import se.thirdbase.target.db.TargetDBHelper;
import se.thirdbase.target.fragment.ammunition.AmmunitionAddFragment;
import se.thirdbase.target.fragment.ammunition.AmmunitionFragment;
import se.thirdbase.target.model.Ammunition;

/**
 * Created by alexp on 2/29/16.
 */
public class AmmunitionActivity extends BaseActivity implements AmmunitionStateListener {

    private static final String TAG = AmmunitionActivity.class.getSimpleName();

    private static final String BACK_STACK_TAG_AMMUNITION_OVERVIEW_FRAGMENT = "BACK_STACK_TAG_AMMUNITION_OVERVIEW_FRAGMENT";
    private static final String BACK_STACK_TAG_AMMUNITION_ADD_FRAGMENT = "BACK_STACK_TAG_AMMUNITION_ADD_FRAGMENT";

    private SQLiteDatabase mSQLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ammunition);

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
        return R.id.ammunition_layout_id;
    }

    @Override
    public void onOverview() {
        Log.d(TAG, "onOverview()");

        Fragment fragment = AmmunitionFragment.newInstance();
        displayFragment(fragment, false, BACK_STACK_TAG_AMMUNITION_OVERVIEW_FRAGMENT);
    }

    @Override
    public void onAdd() {
        Log.d(TAG, "onAdd()");

        Fragment fragment = AmmunitionAddFragment.newInstance();
        displayFragment(fragment, true, BACK_STACK_TAG_AMMUNITION_ADD_FRAGMENT);
    }

    @Override
    public void onAdded(Ammunition ammunition) {
        Log.d(TAG, "onAdded()");

        ammunition.store(mSQLiteDatabase);

        popBackStack();
    }
}
