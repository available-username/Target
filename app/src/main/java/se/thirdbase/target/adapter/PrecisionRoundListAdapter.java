package se.thirdbase.target.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import se.thirdbase.target.R;
import se.thirdbase.target.model.precision.PrecisionSeries;

/**
 * Created by alexp on 2/24/16.
 */
public class PrecisionRoundListAdapter extends ArrayAdapter<PrecisionSeries> {

    private int mResource;
    private PrecisionSeries[] mData;

    public PrecisionRoundListAdapter(Context context, int resource, PrecisionSeries[] data) {
        super(context, resource, data);

        mResource = resource;
        mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SeriesHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();

            row = inflater.inflate(mResource, parent, false);

            holder = new SeriesHolder();
            holder.series = (TextView) row.findViewById(R.id.precision_round_list_row_series);
            holder.score = (TextView) row.findViewById(R.id.precision_round_list_row_score);

            row.setTag(holder);
        } else {
            holder = (SeriesHolder)row.getTag();
        }

        String seriesText = String.format("%d", position + 1);
        String scoreText = getContext().getResources().getString(R.string.points_short, mData[position].getScore());

        holder.series.setText(seriesText);
        holder.score.setText(scoreText);

        return row;
    }

    static class SeriesHolder {
        TextView series;
        TextView score;
    }
}
