package se.thirdbase.target.fragment.statistics;

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
import se.thirdbase.target.adapter.StatisticsPrecisionRoundListAdapter;
import se.thirdbase.target.db.TargetDBHelper;
import se.thirdbase.target.db.PrecisionRoundContract;
import se.thirdbase.target.model.precision.PrecisionRound;

/**
 * Created by alexp on 2/24/16.
 */
public class StatisticsPrecisionFragment extends StatisticsBaseFragment {

    public static final String TAG = StatisticsPrecisionFragment.class.getSimpleName();

    public static StatisticsBaseFragment newInstance() {
        return new StatisticsPrecisionFragment();
    }

    private Button mRoundsProgressButton;
    private ListView mRoundsListView;

    private PrecisionRound[] mPrecisionRounds;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    protected void onOverview() {
        super.onOverview();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_precision_layout, container, false);


        mRoundsProgressButton = (Button) view.findViewById(R.id.statistics_precision_layout_rounds_progress_button);
        mRoundsProgressButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     onPrecisionProgress();
                 }
             }
        );

        List<PrecisionRound> precisionRounds = getPrecisionRounds();

        mPrecisionRounds = new PrecisionRound[precisionRounds.size()];
        precisionRounds.toArray(mPrecisionRounds);

        mRoundsListView = (ListView) view.findViewById(R.id.statistics_precision_layout_rounds_list);

        StatisticsPrecisionRoundListAdapter adapter = new StatisticsPrecisionRoundListAdapter(getContext(), R.layout.statistics_precision_list_row, mPrecisionRounds);

        mRoundsListView.setAdapter(adapter);

        mRoundsListView.setOnItemClickListener(mPrecisionRoundSelectedListener);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private List<PrecisionRound> getPrecisionRounds() {
        TargetDBHelper helper = TargetDBHelper.getInstance(getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        String orderBy = String.format("%s DESC", PrecisionRoundContract.PrecisionRoundEntry.COLUMN_NAME_DATE_TIME);

        return PrecisionRound.fetchAll(db, orderBy);
    }

    AdapterView.OnItemClickListener mPrecisionRoundSelectedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, String.format("onItemClick: %d", position));
            onPrecisionRoundSummary(mPrecisionRounds[position]);
        }
    };
}
