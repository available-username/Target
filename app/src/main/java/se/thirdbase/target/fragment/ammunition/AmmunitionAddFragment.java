package se.thirdbase.target.fragment.ammunition;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import se.thirdbase.target.R;
import se.thirdbase.target.model.Ammunition;
import se.thirdbase.target.model.AmmunitionType;
import se.thirdbase.target.model.BulletCaliber;

/**
 * Created by alexp on 2/29/16.
 */
public class AmmunitionAddFragment extends AmmunitionBaseFragment {

    private static final String TAG = AmmunitionAddFragment.class.getSimpleName();

    private EditText mManufacturerText;
    private EditText mNameText;
    private Spinner mCaliberSpinner;
    private Spinner mTypeSpinner;
    private EditText mVelocityText;
    private EditText mGrainsText;
    private Button mSaveButton;

    public static AmmunitionAddFragment newInstance() {
        return new AmmunitionAddFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ammunition_add_layout, container, false);

        mManufacturerText = (EditText)view.findViewById(R.id.ammunition_add_layout_manufacturer);
        mNameText = (EditText)view.findViewById(R.id.ammunition_add_layout_name);

        //
        //// Caliber
        mCaliberSpinner = (Spinner)view.findViewById(R.id.ammunition_add_layout_caliber);
        BulletCaliber[] calibers = BulletCaliber.values();
        CharSequence[] calibersAsString = new CharSequence[calibers.length];

        for (int i = 0; i < calibers.length; i++) {
            calibersAsString[i] = calibers[i].toString();
        }

        ArrayAdapter<CharSequence> caliberAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, calibersAsString);
        caliberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCaliberSpinner.setAdapter(caliberAdapter);
        ////
        //

        //
        //// Bullet type
        mTypeSpinner = (Spinner)view.findViewById(R.id.ammunition_add_layout_type);
        AmmunitionType[] types = AmmunitionType.values();
        CharSequence[] typesAsString = new CharSequence[types.length];

        for (int i = 0; i < types.length; i++) {
            typesAsString[i] = types[i].toString();
        }

        ArrayAdapter<CharSequence> typesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, typesAsString);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(typesAdapter);
        ////
        //

        mVelocityText = (EditText)view.findViewById(R.id.ammunition_add_layout_velocity);
        mGrainsText = (EditText)view.findViewById(R.id.ammunition_add_layout_grains);

        mSaveButton = (Button)view.findViewById(R.id.ammunition_add_layout_save);

        mSaveButton.setOnClickListener(mSaveClickedListener);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    View.OnClickListener mSaveClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String manufacturer = mManufacturerText.getText().toString();
            String name = mNameText.getText().toString();
            String velocity = mVelocityText.getText().toString();
            String grains = mGrainsText.getText().toString();

            if (manufacturer.length() == 0) {
                String msg = getContext().getResources().getString(R.string.ammunition_manufacturer_error);
                Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
                toast.show();
            } else if (name.length() == 0) {
                String msg = getContext().getResources().getString(R.string.ammunition_name_error);
                Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
                toast.show();
            } else if (velocity.length() == 0) {
                String msg = getContext().getResources().getString(R.string.ammunition_velocity_error);
                Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
                toast.show();
            } else if (grains.length() == 0) {
                String msg = getContext().getResources().getString(R.string.ammunition_grains_error);
                Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                int typeIdx = mTypeSpinner.getSelectedItemPosition();
                AmmunitionType type = AmmunitionType.values()[typeIdx];

                int caliberIdx = mCaliberSpinner.getSelectedItemPosition();
                BulletCaliber caliber = BulletCaliber.values()[caliberIdx];

                int velocityValue = Integer.parseInt(velocity);
                double grainsValue = Double.parseDouble(grains);

                Ammunition ammunition = new Ammunition(type, manufacturer, name, caliber, grainsValue, velocityValue);

                onAdded(ammunition);
            }
        }
    };
}
