package se.thirdbase.target.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import se.thirdbase.target.R;
import se.thirdbase.target.model.BulletHole;
import se.thirdbase.target.model.PrecisionRound;
import se.thirdbase.target.model.PrecisionSeries;

/**
 * Created by alexp on 2/19/16.
 */
public class PrecisionRoundSummaryFragment extends PrecisionBaseFragment {

    private static final String TAG = PrecisionRoundSummaryFragment.class.getSimpleName();

    private static final String BUNDLE_TAG_PRECISION_ROUND = "BUNDLE_TAG_PRECISION_ROUND";

    private TextView mScoreText;
    private TextView mMaxSpreadText;
    private TextView mAvgSpreadText;

    private PrecisionRound mPrecisionRound;
    private int mScore = 0;
    private int mNbrBullets = 0;
    private float mMaxSpread = 0;
    private float mAvgSpread = 0;

    public static PrecisionRoundSummaryFragment newInstance(PrecisionRound precisionRound) {
        Bundle arguments = new Bundle();

        arguments.putParcelable(BUNDLE_TAG_PRECISION_ROUND, precisionRound);

        PrecisionRoundSummaryFragment fragment = new PrecisionRoundSummaryFragment();

        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle arguments = getArguments();

        mPrecisionRound = arguments.getParcelable(BUNDLE_TAG_PRECISION_ROUND);

        List<PrecisionSeries> precisionSeries = mPrecisionRound.getPrecisionSeries();
        int nbrSeries = precisionSeries.size();
        int nbrDistances = 0;

        for (int i = 0; i < nbrSeries; i++) {
            PrecisionSeries series = precisionSeries.get(i);

            mScore += series.getScore();

            List<BulletHole> bulletHoles = series.getBulletHoles();
            int nbrBullets = bulletHoles.size();

            mNbrBullets += nbrBullets;

            for (int j = 0; j < nbrBullets - 1; j++) {
                for (int k = j + 1; k < nbrBullets; k++) {

                    float distance = getSpread(bulletHoles.get(j), bulletHoles.get(k));

                    if (distance > mMaxSpread) {
                        mMaxSpread = distance;
                    }

                    mAvgSpread += distance;
                    nbrDistances += 1;
                }
            }
        }

        // Let avgSpread be zero if there's only one bullet
        mAvgSpread = nbrDistances > 0 ? mAvgSpread / nbrDistances : 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.precision_round_summary_layout, container, false);

        if (savedInstanceState != null)  {
            onRestoreInstanceState(savedInstanceState);
        }

        mScoreText = (TextView) view.findViewById(R.id.precision_round_summary_layout_score);
        mScoreText.setText("" + mScore);

        mMaxSpreadText = (TextView) view.findViewById(R.id.precision_round_summary_layout_max_spread);
        mMaxSpreadText.setText(String.format("%.2fcm", mMaxSpread));

        mAvgSpreadText = (TextView) view.findViewById(R.id.precision_round_summary_layout_avg_spread);
        mAvgSpreadText.setText(String.format("%.2fcm", mAvgSpread));

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void onRestoreInstanceState(Bundle bundle) {

    }

    private float getSpread(BulletHole h1, BulletHole h2) {
        float r1 = h1.getRadius();
        float r2 = h2.getRadius();
        float a1 = h1.getAngle();
        float a2 = h2.getAngle();
        float d1 = h1.getCaliber().getDiameter();
        float d2 = h2.getCaliber().getDiameter();

        float x1 = (float)(r1 * Math.cos(a1));
        float y1 = (float)(r1 * Math.sin(a1));
        float x2 = (float)(r2 * Math.cos(a2));
        float y2 = (float)(r2 * Math.sin(a2));

        float tmp = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);

        return (float)Math.sqrt(tmp) + (d1 + d2) / 2;
    }
}
