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

/**
 * Created by alexp on 2/29/16.
 */
public class AmmunitionListAdapter extends ArrayAdapter<Ammunition> {

    private int mResource;
    private Ammunition[] mData;

    public AmmunitionListAdapter(Context context, int resource, Ammunition[] data) {
        super(context, resource, data);

        mResource = resource;
        mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AmmunitionHolder  holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();

            row = inflater.inflate(mResource, parent, false);

            holder = new AmmunitionHolder();
            holder.makeAndName = (TextView)row.findViewById(R.id.ammunition_list_row_make_and_name);
            holder.caliber = (TextView)row.findViewById(R.id.ammunition_list_row_caliber);
            holder.type = (TextView)row.findViewById(R.id.ammunition_list_row_type);
            holder.velocity = (TextView)row.findViewById(R.id.ammunition_list_row_velocity);
            holder.grains = (TextView)row.findViewById(R.id.ammunition_list_row_grains);

            row.setTag(holder);
        } else {
            holder = (AmmunitionHolder)row.getTag();
        }

        Ammunition ammunition = mData[position];

        holder.makeAndName.setText(ammunition.getMakeAndName());
        holder.caliber.setText(ammunition.getCaliber().toString());
        holder.type.setText(ammunition.getType().toString());
        String velocity = String.format("%dm/s", ammunition.getMuzzleVelocity());
        holder.velocity.setText(velocity);
        String grains = String.format("%.2fg", ammunition.getGrains());
        holder.grains.setText(grains);

        return row;
    }

    static class AmmunitionHolder {
        TextView makeAndName;
        TextView caliber;
        TextView type;
        TextView velocity;
        TextView grains;
    }
}
