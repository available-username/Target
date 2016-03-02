package se.thirdbase.target.fragment.weapon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.adapter.WeaponsListAdapter;
import se.thirdbase.target.db.TargetDBHelper;
import se.thirdbase.target.model.Weapon;

/**
 * Created by alexp on 2/29/16.
 */
public class WeaponsFragment extends WeaponsBaseFragment {

    private static final String TAG = WeaponsFragment.class.getSimpleName();

    private ListView mWeaponsList;
    private Button mAddButton;

    public static WeaponsFragment newInstance() {
        return new WeaponsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weapons_layout, container, false);

        List<Weapon> weapons = getWeapons();
        int size = weapons.size();

        Weapon[] weaponArray = new Weapon[size];
        weapons.toArray(weaponArray);

        mWeaponsList = (ListView)view.findViewById(R.id.weapons_fragment_layout_list);
        final WeaponsListAdapter adapter = new WeaponsListAdapter(getContext(), R.layout.weapons_list_row, weaponArray);
        mWeaponsList.setAdapter(adapter);

        mWeaponsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Weapon weapon = adapter.getItem(position);
                onDisplay(weapon);
            }
        });

        mAddButton = (Button)view.findViewById(R.id.weapons_fragment_layout_add);
        mAddButton.setOnClickListener(mAddClickedListener);

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

    private List<Weapon> getWeapons() {
        TargetDBHelper helper = TargetDBHelper.getInstance(getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        return Weapon.fetchAll(db, null);
    }

    View.OnClickListener mAddClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "mAddClickedListener.onClick()");
            onAdd();
        }
    };
}
