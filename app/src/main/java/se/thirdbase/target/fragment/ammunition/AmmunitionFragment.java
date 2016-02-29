package se.thirdbase.target.fragment.ammunition;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.adapter.AmmunitionListAdapter;
import se.thirdbase.target.db.TargetDBHelper;
import se.thirdbase.target.model.Ammunition;

/**
 * Created by alexp on 2/29/16.
 */
public class AmmunitionFragment extends AmmunitionBaseFragment {

    private static final String TAG = AmmunitionFragment.class.getSimpleName();

    private ListView mAmmunitionList;
    private Button mAddButton;

    public static AmmunitionFragment newInstance() {
        return new AmmunitionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ammunition_layout, container, false);

        List<Ammunition> ammunition = getAmmunition();
        int size = ammunition.size();

        Ammunition[] ammunitionArray = new Ammunition[size];
        ammunition.toArray(ammunitionArray);

        mAmmunitionList = (ListView)view.findViewById(R.id.ammunition_layout_list);
        AmmunitionListAdapter adapter = new AmmunitionListAdapter(getContext(), R.layout.ammunition_list_row, ammunitionArray);

        mAmmunitionList.setAdapter(adapter);

        mAddButton = (Button)view.findViewById(R.id.ammunition_layout_add);
        mAddButton.setOnClickListener(mAddClickedListener);

        return view;
    }

    View.OnClickListener mAddClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onAdd();
        }
    };

    private List<Ammunition> getAmmunition() {
        TargetDBHelper helper = TargetDBHelper.getInstance(getContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        return Ammunition.fetchAll(db, null);
    }
}
