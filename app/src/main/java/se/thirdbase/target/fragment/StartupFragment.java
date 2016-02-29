package se.thirdbase.target.fragment;

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

import se.thirdbase.target.R;

/**
 * Created by alexp on 2/15/16.
 */
public class StartupFragment extends BaseFragment {

    private static final String TAG = StartupFragment.class.getSimpleName();

    public static StartupFragment newInstance() {
        return new StartupFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.startup_layout, container, false);

        ListView listView = (ListView) view.findViewById(R.id.startup_layout_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.format("position: %d, id: %d", position, id));
                switch (position) {
                    case 0:
                        onPrecision();
                        break;
                    case 1:
                        onWeapons();
                        break;
                    case 2:
                        onStatistics();
                        break;
                    case 3:
                    default:
                        onStatistics();
                        break;
                }
            }

        });

        return view;
    }
}
