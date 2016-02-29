package se.thirdbase.target.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import se.thirdbase.target.R;
import se.thirdbase.target.model.Weapon;
import se.thirdbase.target.view.TargetView;

/**
 * Created by alexp on 2/29/16.
 */
public class WeaponsListAdapter extends ArrayAdapter<Weapon> {

    private int mResource;
    private Weapon[] mData;

    public WeaponsListAdapter(Context context, int resource, Weapon[] data) {
        super(context, resource, data);

        mResource = resource;
        mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WeaponHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();

            row = inflater.inflate(mResource, parent, false);

            holder = new WeaponHolder();
            holder.manufacturer = (TextView)row.findViewById(R.id.weapon_list_row_manufacturer);
            holder.model = (TextView)row.findViewById(R.id.weapon_list_row_model);
            holder.caliber = (TextView)row.findViewById(R.id.weapon_list_row_caliber);
            holder.type = (TextView)row.findViewById(R.id.weapons_list_row_type);

            row.setTag(holder);
        } else {
            holder = (WeaponHolder)row.getTag();
        }

        Weapon weapon = mData[position];

        holder.manufacturer.setText(weapon.getManufacturer());
        holder.model.setText(weapon.getModel());
        holder.caliber.setText(weapon.getCaliber().toString());

        String[] weaponTypeArray = getContext().getResources().getStringArray(R.array.weapon_type);
        String weaponType = weaponTypeArray[weapon.getWeaponType().ordinal()];
        holder.type.setText(weaponType);

        return row;
    }

    static class WeaponHolder {
        TextView manufacturer;
        TextView model;
        TextView caliber;
        TextView type;
    }
}
