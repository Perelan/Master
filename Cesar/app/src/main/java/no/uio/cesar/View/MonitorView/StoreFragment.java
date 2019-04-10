package no.uio.cesar.View.MonitorView;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.R;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.cesar.ViewModel.SampleViewModel;
import no.uio.cesar.ViewModel.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment {

    private RecordViewModel recordViewModel;
    private SampleViewModel sampleViewModel;
    private UserViewModel userViewModel;

    private RatingBar rb;
    private EditText etTitle, etDescription;

    private TextView tvDiscard, tvStoreTime, tvStoreSample;

    private int primaryKey;

    public static final String TAG = "StoreFragment";

    public StoreFragment() {
        // Required empty public constructor
    }

    static StoreFragment newInstance(long primaryKey, long monitorTime) {
        
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
        userViewModel = new UserViewModel(getContext());

        primaryKey = (int) getArguments().getLong("key");

        etTitle = v.findViewById(R.id.store_title);
        etDescription = v.findViewById(R.id.store_desc);
        rb = v.findViewById(R.id.store_rating);
        tvDiscard = v.findViewById(R.id.store_discard);
        tvStoreTime = v.findViewById(R.id.store_time);
        tvStoreSample = v.findViewById(R.id.store_samples);

        tvDiscard.setOnClickListener(l -> discardSession(primaryKey));

        etTitle.setHint("Record #" + primaryKey);

        CardView btn = v.findViewById(R.id.store_btn);
        btn.setOnClickListener(view -> getNumberSamples(primaryKey));

        return v;
    }

    private void discardSession(int primaryKey) {

        Record record = new Record();
        record.setId(primaryKey);

        new AlertDialog.Builder(getContext())
                .setTitle("Delete current session?")
                .setMessage("Do you want to delete this session?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton("Yes", (dialog, which) -> {
                    recordViewModel.delete(record);
                    Toast.makeText(getContext(), "Monitored session deleted!", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();

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
        r.setUser(userViewModel.getUser());

        Toast.makeText(getContext(), "Monitored session stored!", Toast.LENGTH_LONG).show();

        recordViewModel.update(r);

        getActivity().finish();

    }

    // Todo: rewrite this logic
    private void getNumberSamples(long primaryKey) {

        sampleViewModel.getSamplesForRecord(primaryKey).observe(this, samples -> {
            Log.d(TAG, "getNumberSamples: " + samples);
            storeMonitorSession(samples.size());
        });
    }
}
