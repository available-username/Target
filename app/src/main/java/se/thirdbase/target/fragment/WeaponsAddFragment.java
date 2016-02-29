package se.thirdbase.target.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import se.thirdbase.target.R;
import se.thirdbase.target.model.BulletCaliber;
import se.thirdbase.target.model.Weapon;
import se.thirdbase.target.model.WeaponType;

/**
 * Created by alexp on 2/29/16.
 */
public class WeaponsAddFragment extends WeaponsBaseFragment {

    private static final String TAG = WeaponsAddFragment.class.getSimpleName();

    private EditText mManufacturerText;
    private EditText mModelText;
    private Spinner mCaliberSpinner;
    private Spinner mWeaponTypeSpinner;
    private Button mSaveButton;

    public static WeaponsAddFragment newInstance() {
        return new WeaponsAddFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weapons_add_layout, container, false);

        mManufacturerText = (EditText)view.findViewById(R.id.weapons_add_layout_manufacturer);
        mModelText = (EditText)view.findViewById(R.id.weapons_add_layout_model);

        mCaliberSpinner = (Spinner)view.findViewById(R.id.weapons_add_layout_caliber);

        BulletCaliber[] calibers = BulletCaliber.values();
        CharSequence[] calibersAsString = new CharSequence[calibers.length];

        for (int i = 0; i < calibers.length; i++) {
            calibersAsString[i] = calibers[i].toString();
        }

        ArrayAdapter<CharSequence> caliberAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, calibersAsString);
        caliberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCaliberSpinner.setAdapter(caliberAdapter);


        mWeaponTypeSpinner = (Spinner)view.findViewById(R.id.weapons_add_layout_type);
        ArrayAdapter<CharSequence> weaponTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.weapon_type, android.R.layout.simple_spinner_item);
        weaponTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWeaponTypeSpinner.setAdapter(weaponTypeAdapter);

        mSaveButton = (Button)view.findViewById(R.id.weapons_add_layout_save);
        mSaveButton.setOnClickListener(mSaveClickedListener);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    View.OnClickListener mSaveClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String manufacturer = mManufacturerText.getText().toString();
            String model = mModelText.getText().toString();

            if (manufacturer.length() == 0) {
                String msg = getContext().getResources().getString(R.string.weapon_manufacturer_error);
                Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            } else if (model.length() == 0) {
                String msg = getContext().getResources().getString(R.string.weapon_model_error);
                Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            } else {
                int caliberIdx = mCaliberSpinner.getSelectedItemPosition();
                int weaponTypeIdx = mWeaponTypeSpinner.getSelectedItemPosition();

                BulletCaliber caliber = BulletCaliber.values()[caliberIdx];
                WeaponType weaponType = WeaponType.values()[weaponTypeIdx];

                Weapon weapon = new Weapon(weaponType, manufacturer, model, caliber);

                onAdded(weapon);
            }
        }
    };
}
