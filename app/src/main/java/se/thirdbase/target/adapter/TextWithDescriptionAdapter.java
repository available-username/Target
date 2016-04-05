package se.thirdbase.target.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import se.thirdbase.target.R;
import se.thirdbase.target.view.TargetView;

/**
 * Created by alex on 3/30/16.
 */
public class TextWithDescriptionAdapter extends ArrayAdapter<TextWithDescriptionItem> {

    private static final String TAG = TextWithDescriptionAdapter.class.getSimpleName();

    private int mResource;
    private TextWithDescriptionItem[] mData;

    public TextWithDescriptionAdapter(Context context, int resource, TextWithDescriptionItem[] data) {
        super(context, resource, data);

        mResource = resource;
        mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();

            row = inflater.inflate(mResource, parent, false);

            holder = new ItemHolder();
            holder.text = (TextView)row.findViewById(R.id.text_with_desc_list_row_title);
            holder.description = (TextView)row.findViewById(R.id.text_with_desc_list_row_desc);

            row.setTag(holder);
        } else {
            holder = (ItemHolder)row.getTag();
        }

        Log.d(TAG, "Text: " + mData[position].getText());
        Log.d(TAG, "Desc: " + mData[position].getDescription());

        holder.text.setText(mData[position].getText());
        holder.description.setText(mData[position].getDescription());

        return row;
    }

    static class ItemHolder {
        TextView text;
        TextView description;
    }
}
