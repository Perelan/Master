package no.uio.cesar.View.FeedView;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Locale;

import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.ViewModel.SampleViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnalyticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnalyticsFragment extends Fragment {
    private static final String ARG_RECORD = "recordparam";

    private Record currentRecord;


    public AnalyticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AnalyticsFragment.
     */
    public static AnalyticsFragment newInstance(Record record) {
        AnalyticsFragment fragment = new AnalyticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECORD, new Gson().toJson(record));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String recordString = getArguments().getString(ARG_RECORD);
            currentRecord = new Gson().fromJson(recordString, Record.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_analytics, container, false);


        TextView tvTitle = v.findViewById(R.id.analytics_title);
        TextView tvSubtitle = v.findViewById(R.id.analytics_subtitle);

        tvTitle.setText(String.format(Locale.getDefault(), "Analytics for %s", currentRecord.getName()));
        tvSubtitle.setText(String.format(Locale.getDefault(), "%d samples were gathered during this session", currentRecord.getNrSamples()));

        GraphView respGraph = v.findViewById(R.id.analytics_resp_graph);

        GraphView hrGraph = v.findViewById(R.id.analytics_hr_graph);

        SampleViewModel sampleViewModel = ViewModelProviders.of(this).get(SampleViewModel.class);

        sampleViewModel.getSamplesForRecord(currentRecord.getId()).observe(this, samples -> {
            DataPoint[] dpList = new DataPoint[samples.size()];

            for (int i = 0; i < dpList.length; i++) {
                Sample currSample = samples.get(i);

                int value = Uti.extractFlowData(currSample.getSample());

                dpList[i] = new DataPoint(currSample.getImplicitTS().getTime() / 1000, value);
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dpList);

            respGraph.addSeries(series);

            respGraph.getViewport().setYAxisBoundsManual(true);
            respGraph.getViewport().setMinY(1700);
            respGraph.getViewport().setMaxY(2000);

            respGraph.getViewport().setXAxisBoundsManual(true);

            respGraph.getViewport().setScalable(true);
            respGraph.getViewport().setScalableY(true);

            respGraph.getGridLabelRenderer().setHumanRounding(true);
        });


        return v;
    }

}
