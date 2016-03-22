package se.thirdbase.target.fragment.weapon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import se.thirdbase.target.R;
import se.thirdbase.target.db.TargetDBHelper;
import se.thirdbase.target.model.Weapon;

/**
 * Created by alexp on 3/1/16.
 */
public class WeaponsDisplayFragment extends WeaponsBaseFragment {

    private static final String TAG = WeaponsDisplayFragment.class.getSimpleName();

    private static final String BUNDLE_TAG_WEAPON = "BUNDLE_TAG_WEAPON";

    private SQLiteDatabase mSQLiteDatabase;

    private Weapon mWeapon;

    public static WeaponsDisplayFragment newInstance(Weapon weapon) {
        Bundle arguments = new Bundle();

        arguments.putParcelable(BUNDLE_TAG_WEAPON, weapon);

        WeaponsDisplayFragment fragment = new WeaponsDisplayFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        TargetDBHelper helper = TargetDBHelper.getInstance(context);
        mSQLiteDatabase = helper.getReadableDatabase();

        Bundle arguments = getArguments();
        mWeapon = arguments.getParcelable(BUNDLE_TAG_WEAPON);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weapons_display_layout, container, false);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        TextView titleText = (TextView) view.findViewById(R.id.weapon_display_layout_weapon_title);
        String title = mWeapon.getMakeAndModel();
        titleText.setText(title);

        Button principleButton = (Button)view.findViewById(R.id.weapon_display_layout_principle);
        principleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = WeaponDisplayPrincipleDistributionFragment.newInstance(mWeapon);

                display(fragment);
            }
        });

        Button weaponButton = (Button)view.findViewById(R.id.weapon_display_layout_ammunition);
        weaponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = WeaponDisplayAmmunitionDistributionFragment.newInstance(mWeapon);

                display(fragment);
            }
        });

        Fragment fragment = WeaponDisplayPrincipleDistributionFragment.newInstance(mWeapon);
        display(fragment);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_TAG_WEAPON, mWeapon);
    }

    private void onRestoreInstanceState(Bundle bundle) {
        mWeapon = bundle.getParcelable(BUNDLE_TAG_WEAPON);
    }

    private void display(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.weapon_display_layout_charts, fragment);
        transaction.commit();
    }
}
