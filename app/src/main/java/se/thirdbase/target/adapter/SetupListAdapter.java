package se.thirdbase.target.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import se.thirdbase.target.R;
import se.thirdbase.target.model.Ammunition;
import se.thirdbase.target.model.Setup;
import se.thirdbase.target.model.Weapon;

/**
 * Created by alexp on 2/29/16.
 */
public class SetupListAdapter extends ArrayAdapter<Setup> {

    private int mResource;
    private Setup[] mData;

    public SetupListAdapter(Context context, int resource, Setup[] data) {
        super(context, resource, data);

        mResource = resource;
        mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SetupHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();

            row = inflater.inflate(mResource, parent, false);

            holder = new SetupHolder();
            holder.principle = (TextView)row.findViewById(R.id.setup_list_row_principle);
            holder.weapon = (TextView)row.findViewById(R.id.setup_list_row_weapon);
            holder.ammunition = (TextView)row.findViewById(R.id.setup_list_row_ammunition);

            row.setTag(holder);
        } else {
            holder = (SetupHolder)row.getTag();
        }

        Setup setup = mData[position];

        int principleIdx = setup.getPrinciple().ordinal();
        String principle = getContext().getResources().getStringArray(R.array.principles)[principleIdx];
        holder.principle.setText(principle);

        Weapon weapon = setup.getWeapon();
        String weaponStr = String.format("%s %s", weapon.getManufacturer(), weapon.getModel());
        holder.weapon.setText(weaponStr);

        Ammunition ammunition = setup.getAmmunition();
        String ammunitionStr = String.format("%s %s", ammunition.getManufacturer(), ammunition.getName());
        holder.ammunition.setText(ammunitionStr);

        return row;
    }

    static class SetupHolder {
        TextView principle;
        TextView weapon;
        TextView ammunition;
    }
}
