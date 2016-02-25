package se.thirdbase.target.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import se.thirdbase.target.R;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.util.SQLUtil;

/**
 * Created by alexp on 2/24/16.
 */
public class StatisticsPrecisionRoundListAdapter extends ArrayAdapter<PrecisionRound> {

    private int mResource;
    private PrecisionRound[] mData;

    public StatisticsPrecisionRoundListAdapter(Context context, int resource, PrecisionRound[] data) {
        super(context, resource, data);

        mResource = resource;
        mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RoundHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();

            row = inflater.inflate(mResource, parent, false);

            holder = new RoundHolder();
            holder.date = (TextView) row.findViewById(R.id.statistics_precision_list_item_date);
            holder.score = (TextView) row.findViewById(R.id.statistics_precision_list_item_score);

            row.setTag(holder);
        } else {
            holder = (RoundHolder)row.getTag();
        }

        String dateText = SQLUtil.calendar2String(mData[position].getDate());
        String scoreText = getContext().getResources().getString(R.string.points_short, mData[position].getScore());

        holder.date.setText(dateText);
        holder.score.setText(scoreText);

        return row;
    }

    static class RoundHolder {
        TextView date;
        TextView score;
    }
}
