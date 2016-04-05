package se.thirdbase.target.fragment.precision;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import se.thirdbase.target.R;
import se.thirdbase.target.adapter.TextWithDescriptionAdapter;
import se.thirdbase.target.adapter.TextWithDescriptionItem;

/**
 * Created by alex on 3/23/16.
 */
public class PrecisionSelectionFragment extends PrecisionBaseFragment {

    private static final String TAG = PrecisionSelectionFragment.class.getSimpleName();

    public static PrecisionSelectionFragment newInstance() {
        PrecisionSelectionFragment fragment = new PrecisionSelectionFragment();

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.precision_selection_layout, container, false);

        Context context = getContext();

        ListView listView = (ListView) view.findViewById(R.id.precision_selection_list);

        String[] texts = context.getResources().getStringArray(R.array.precision_selection);
        String[] descriptions = context.getResources().getStringArray(R.array.precision_selection_desc);

        TextWithDescriptionItem[] data = TextWithDescriptionItem.fromArrays(texts, descriptions);
        TextWithDescriptionAdapter adapter = new TextWithDescriptionAdapter(context, R.layout.text_with_desc_list_row, data);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("position: %d, id: %d", position, id));
                switch (position) {
                    case 0:
                        onPrecisionStartCompetitionRound();
                        break;
                    case 1:
                        onPrecisionStartTrainingRound();
                        break;
                    case 2:
                    default:
                        onPrecisionStartUnboundRound();
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
