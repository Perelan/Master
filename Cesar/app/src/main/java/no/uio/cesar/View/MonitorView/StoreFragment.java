package no.uio.cesar.View.MonitorView;


import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.R;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.cesar.ViewModel.SampleViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment {

    private RecordViewModel recordViewModel;
    private SampleViewModel sampleViewModel;

    private RatingBar rb;
    private EditText etTitle, etDescription;

    private int primaryKey;

    public static final String TAG = "StoreFragment";

    public StoreFragment() {
        // Required empty public constructor
    }

    public static StoreFragment newInstance(long primaryKey, long monitorTime) {
        
        Bundle args = new Bundle();
        args.putLong("key", primaryKey);
        args.putLong("monitorTime", monitorTime);
        
        StoreFragment fragment = new StoreFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_store, container, false);

        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        sampleViewModel = ViewModelProviders.of(this).get(SampleViewModel.class);

        primaryKey = (int) getArguments().getLong("key");

        etTitle = v.findViewById(R.id.store_title);
        etDescription = v.findViewById(R.id.store_desc);
        rb = v.findViewById(R.id.store_rating);

        etTitle.setHint("Record #" + primaryKey);

        CardView btn = v.findViewById(R.id.store_btn);
        btn.setOnClickListener(view -> getNumberSamples(primaryKey));

        return v;
    }

    private void storeMonitorSession(int sampleCount) {
        long monitorTime = getArguments().getLong("monitorTime");

        String name = etTitle.getText().toString().isEmpty()
                ? "Record " + primaryKey
                : etTitle.getText().toString();

        String description = etDescription.getText().toString();

        float rating = rb.getRating();

        Record r = new Record();
        r.setId(primaryKey);
        r.setName(name);
        r.setDescription(description);
        r.setRating(rating);
        r.setMonitorTime(monitorTime);
        r.setNrSamples(sampleCount);

        Toast.makeText(getContext(), "Monitored session stored!", Toast.LENGTH_LONG).show();

        recordViewModel.update(r);

        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    // Todo: rewrite this logic
    private void getNumberSamples(long primaryKey) {

        sampleViewModel.getSamplesForRecord(primaryKey).observe(this, samples -> {
            Log.d(TAG, "getNumberSamples: " + samples);
            storeMonitorSession(samples.size());
        });
    }
}
