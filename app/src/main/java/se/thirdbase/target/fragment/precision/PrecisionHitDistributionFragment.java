package se.thirdbase.target.fragment.precision;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.precision.PrecisionRound;
import se.thirdbase.target.model.precision.PrecisionSeries;
import se.thirdbase.target.view.PrecisionTargetView;

/**
 * Created by alexp on 2/25/16.
 */
public class PrecisionHitDistributionFragment extends Fragment {

    private static final String BUNDLE_TAG_PRECISION_SERIES = "BUNDLE_TAG_PRECISION_SERIES";

    public static PrecisionHitDistributionFragment newInstance(PrecisionRound precisionRound) {
        return newInstance(precisionRound.getPrecisionSeries());
    }

    public static PrecisionHitDistributionFragment newInstance(List<PrecisionSeries> precisionSeriesList) {
        int size = precisionSeriesList.size();
        PrecisionSeries[] precisionSeriesArray = new PrecisionSeries[size];

        precisionSeriesList.toArray(precisionSeriesArray);

        Bundle arguments = new Bundle();
        arguments.putParcelableArray(BUNDLE_TAG_PRECISION_SERIES, precisionSeriesArray);

        PrecisionHitDistributionFragment fragment = new PrecisionHitDistributionFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    private PrecisionSeries[] mPrecisionSeries;
    private PrecisionTargetView mPrecisionTargetView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle arguments = getArguments();

        mPrecisionSeries = (PrecisionSeries[]) arguments.getParcelableArray(BUNDLE_TAG_PRECISION_SERIES);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.precision_hit_distribution_layout, container, false);

        mPrecisionTargetView = (PrecisionTargetView) view.findViewById(R.id.precision_hit_distribution_target);

        List<BulletHole> bulletHoles = getBulletHoles(mPrecisionSeries);
        mPrecisionTargetView.setBulletHoles(bulletHoles);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private List<BulletHole> getBulletHoles(PrecisionSeries[] precisionSeries) {
        List<BulletHole> allHoles = new ArrayList<>();

        for (PrecisionSeries series : precisionSeries) {
            List<BulletHole> holes = series.getBulletHoles();
            allHoles.addAll(holes);
        }

        return allHoles;
    }
}
