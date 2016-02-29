package se.thirdbase.target.fragment.precision;

import android.content.Context;
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
import se.thirdbase.target.adapter.PrecisionRoundListAdapter;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

/**
 * Created by alexp on 2/17/16.
 */
public class PrecisionRoundFragment extends PrecisionBaseFragment {

    private static final String TAG = PrecisionRoundFragment.class.getSimpleName();

    private static final String BUNDLE_TAG_PRECISION_ROUND = "BUNDLE_TAG_PRECISION_ROUND";

    private ListView mSeriesList;
    private Button mAddButton;
    private PrecisionRound mPrecisionRound;

    public static PrecisionRoundFragment newInstance() {
        return newInstance(new PrecisionRound());
    }

    public static PrecisionRoundFragment newInstance(PrecisionRound precisionRound) {
        Bundle arguments = new Bundle();

        arguments.putParcelable(BUNDLE_TAG_PRECISION_ROUND, precisionRound);

        PrecisionRoundFragment fragment = new PrecisionRoundFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle arguments = getArguments();

        mPrecisionRound = arguments.getParcelable(BUNDLE_TAG_PRECISION_ROUND);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.precision_round_layout, container, false);

        mSeriesList = (ListView)view.findViewById(R.id.precision_layout_series_list);
        mSeriesList.setOnItemClickListener(mSeriesClickedListener);

        mAddButton = (Button) view.findViewById(R.id.precision_layout_add_series);
        mAddButton.setOnClickListener(mAddButtonClicked);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        int nPrecisionSeries = mPrecisionRound.getPrecisionSeries().size();
        PrecisionSeries[] precisionSeries = new PrecisionSeries[nPrecisionSeries];
        mPrecisionRound.getPrecisionSeries().toArray(precisionSeries);

        PrecisionRoundListAdapter adapter = new PrecisionRoundListAdapter(getContext(), R.layout.precision_round_list_row, precisionSeries);
        mSeriesList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_TAG_PRECISION_ROUND, mPrecisionRound);
    }

    private void onRestoreInstanceState(Bundle bundle) {
        mPrecisionRound = bundle.getParcelable(BUNDLE_TAG_PRECISION_ROUND);
    }

    private View.OnClickListener mAddButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onUpdatePrecisionSeries(null);
        }
    };

    private AdapterView.OnItemClickListener mSeriesClickedListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, String.format("Position: %d", position));

            List<PrecisionSeries> precisionSeries = mPrecisionRound.getPrecisionSeries();
            //PrecisionSeries series = position == precisionSeries.size() ? null : precisionSeries.get(position);
            PrecisionSeries series = precisionSeries.get(position);

            onUpdatePrecisionSeries(series);
        }
    };
}
