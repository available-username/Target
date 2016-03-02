package se.thirdbase.target.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import se.thirdbase.target.R;
import se.thirdbase.target.model.precision.PrecisionRound;

/**
 * Created by alexp on 2/24/16.
 */
public class StatisticsPrecisionRoundListAdapter extends ArrayAdapter<PrecisionRound> {

    private int mResource;
    private PrecisionRound[] mData;
    private Locale mLocale;

    public StatisticsPrecisionRoundListAdapter(Context context, int resource, PrecisionRound[] data) {
        super(context, resource, data);

        mResource = resource;
        mData = data;
        mLocale = context.getResources().getConfiguration().locale;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RoundHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();

            row = inflater.inflate(mResource, parent, false);

            holder = new RoundHolder();
            holder.score = (TextView) row.findViewById(R.id.statistics_precision_list_item_score);
            holder.date = (TextView) row.findViewById(R.id.statistics_precision_list_item_date);
            holder.since = (TextView) row.findViewById(R.id.statistics_precision_list_item_since);

            row.setTag(holder);
        } else {
            holder = (RoundHolder)row.getTag();
        }

        String scoreText = getContext().getResources().getString(R.string.points_short, mData[position].getScore());

        long timestamp = mData[position].getTimestamp();
        String sinceText = DateUtils.getRelativeTimeSpanString(timestamp).toString();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        String dateText = String.format("%s %d %s %d",
                calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, mLocale),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, mLocale),
                calendar.get(Calendar.YEAR));

        holder.score.setText(scoreText);
        holder.date.setText(dateText);
        holder.since.setText(sinceText);

        return row;
    }

    static class RoundHolder {
        TextView score;
        TextView date;
        TextView since;
    }
}
