package se.thirdbase.target.fragment.loadout;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.adapter.AmmunitionListAdapter;
import se.thirdbase.target.adapter.WeaponsListAdapter;
import se.thirdbase.target.db.TargetDBHelper;
import se.thirdbase.target.model.Ammunition;
import se.thirdbase.target.model.Weapon;

/**
 * Created by alexp on 3/1/16.
 */
public class LoadOutFragment extends Fragment {

    private static final String TAG = LoadOutFragment.class.getSimpleName();

    private LoadOutListener mListener;

    private ListView mWeaponsListView;
    private ListView mAmmunitionListView;
    private WeaponsListAdapter mWeaponsListAdapter;
    private AmmunitionListAdapter mAmmunitionListAdapter;
    private Button mSaveButton;

    private SQLiteDatabase mSQLiteDatabase;

    List<Weapon> mWeaponsList;
    List<Ammunition> mAmmunitionList;
    private Weapon mWeapon;
    private Ammunition mAmmunition;

    public static LoadOutFragment newInstance() {
        return new LoadOutFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof LoadOutListener) {
            mListener = (LoadOutListener)context;
        }

        TargetDBHelper helper = TargetDBHelper.getInstance(context);
        mSQLiteDatabase = helper.getReadableDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loadout_fragment_layout, container, false);

        /* Weapons */
        mWeaponsListView = (ListView)view.findViewById(R.id.load_out_fragment_layout_weapon);
        mWeaponsListView.setOnItemClickListener(mWeaponClickedListener);

        mWeaponsList = Weapon.fetchAll(mSQLiteDatabase, null);
        int nbrWeapons = mWeaponsList.size();
        Weapon[] weapons = new Weapon[nbrWeapons];
        mWeaponsList.toArray(weapons);
        mWeaponsListAdapter = new WeaponsListAdapter(getContext(), R.layout.weapons_list_row, weapons);
        mWeaponsListView.setAdapter(mWeaponsListAdapter);

        /* Ammunition */
        mAmmunitionListView = (ListView)view.findViewById(R.id.load_out_fragment_layout_ammunition);
        mAmmunitionListView.setOnItemClickListener(mAmmunitionClickedListener);

        mAmmunitionList = Ammunition.fetchAll(mSQLiteDatabase, null);
        int nbrAmmunition = mAmmunitionList.size();
        Ammunition[] ammunition = new Ammunition[nbrAmmunition];
        mAmmunitionList.toArray(ammunition);
        mAmmunitionListAdapter = new AmmunitionListAdapter(getContext(), R.layout.ammunition_list_row, ammunition);
        mAmmunitionListView.setAdapter(mAmmunitionListAdapter);

        mSaveButton = (Button)view.findViewById(R.id.load_out_fragment_layout_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onLoadOut(mWeapon, mAmmunition);
                }
            }
        });

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        toggleSaveButton();

        return view;
    }

    private static final String BUNDLE_TAG_WEAPON = "BUNDLE_TAG_WEAPON";
    private static final String BUNDLE_TAG_AMMUNITION = "BUNDLE_TAG_AMMUNITION";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(BUNDLE_TAG_WEAPON, mWeapon);
        outState.putParcelable(BUNDLE_TAG_AMMUNITION, mAmmunition);
    }

    private void onRestoreInstanceState(Bundle bundle) {
        mWeapon = bundle.getParcelable(BUNDLE_TAG_WEAPON);
        mAmmunition = bundle.getParcelable(BUNDLE_TAG_AMMUNITION);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateWeaponsList() {
        Log.d(TAG, "updateWeaponsList()");

        Collection<Weapon> matching = new ArrayList<>();

        for (Weapon weapon : mWeaponsList) {
            if (weapon.getCaliber() == mAmmunition.getCaliber()) {
                matching.add(weapon);
            }
        }

        mWeaponsListAdapter.clear();
        mWeaponsListAdapter.addAll(matching);
    }

    private void updateAmmunitionList() {
        Log.d(TAG, "updateAmmunitionList()");

        Collection<Ammunition> matching = new ArrayList<>();

        for (Ammunition ammunition : mAmmunitionList) {
            if (ammunition.getCaliber() == mWeapon.getCaliber()) {
                matching.add(ammunition);
            }
        }

        mAmmunitionListAdapter.clear();
        mAmmunitionListAdapter.addAll(matching);
    }

    private void toggleSaveButton() {
        mSaveButton.setEnabled(mWeapon != null && mAmmunition != null && mWeapon.getCaliber() == mAmmunition.getCaliber());
    }

    private AdapterView.OnItemClickListener mWeaponClickedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "Weapon selected");

            mWeapon = mWeaponsListAdapter.getItem(position);

            /*
            if (mAmmunition != null) {
                if (mWeapon.getCaliber() != mAmmunition.getCaliber()) {
                    mAmmunition = null;
                    updateAmmunitionList();
                }
            }
            */

            toggleSaveButton();
        }
    };

    private AdapterView.OnItemClickListener mAmmunitionClickedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "Ammunition selected");

            mAmmunition = mAmmunitionListAdapter.getItem(position);

            /*
            if (mWeapon != null) {
                if (mAmmunition.getCaliber() != mWeapon.getCaliber()) {
                    mWeapon = null;
                    updateWeaponsList();
                }
            }
            */

            toggleSaveButton();
        }
    };

    /**
     * Created by alexp on 3/1/16.
     */
    public static interface LoadOutListener {

        void onLoadOut(Weapon weapon, Ammunition ammunition);
    }
}
