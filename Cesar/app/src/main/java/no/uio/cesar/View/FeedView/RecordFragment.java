package no.uio.cesar.View.FeedView;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.Locale;

import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.Model.Record;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Export;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.ViewModel.RecordViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends DialogFragment implements Toolbar.OnMenuItemClickListener {
    public static final String TAG = "Record Fragment";

    private TextView tvTitle, tvDate, tvDescription, tvSummary;
    private RatingBar rbRating;
    private CardView btnAnalytics;
    private ImageView ivClose;

    private RecordViewModel recordViewModel;

    private Record currentRecord;

    public RecordFragment() {
        // Required empty public constructor
    }

    public static RecordFragment newInstance(Record selectedRecord) {
        
        Bundle args = new Bundle();

        args.putString("record", new Gson().toJson(selectedRecord));

        RecordFragment fragment = new RecordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_record_item, container, false);
        Toolbar toolbar = view.findViewById(R.id.record_item_toolbar);

        toolbar.inflateMenu(R.menu.record);
        toolbar.setOnMenuItemClickListener(this);

        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        tvTitle = view.findViewById(R.id.record_item_title);
        tvDate = view.findViewById(R.id.record_item_date);
        tvDescription = view.findViewById(R.id.record_item_description);
        tvSummary = view.findViewById(R.id.record_item_summary);
        rbRating = view.findViewById(R.id.record_item_rating);
        btnAnalytics = view.findViewById(R.id.record_item_analytics);
        ivClose = view.findViewById(R.id.record_item_close);

        ivClose.setOnClickListener(v -> { this.dismiss(); });

        btnAnalytics.setOnClickListener(v -> {
            Uti.commitFragmentTransaction(getActivity(), new AnalyticsFragment());
        });

        String recordString = getArguments().getString("record");

        currentRecord = new Gson().fromJson(recordString, Record.class);

        Log.d(TAG, "onCreateView: " + currentRecord);

        populateElements(currentRecord);

        return view;
    }

    public void populateElements(Record currentRecord) {
        tvTitle.setText(currentRecord.getName());
        tvDate.setText(DateFormat.getDateInstance().format(currentRecord.getCreatedAt()));
        tvDescription.setText(currentRecord.getDescription().isEmpty() ? "No description" : currentRecord.getDescription());

        long[] timeConverted = Uti.splitSecondsToHMS(currentRecord.getMonitorTime());

        tvSummary.setText(String.format(Locale.getDefault(),
                "Session lasted for %d hours and %d minutes and\n %d samples were gathered",
                timeConverted[0], timeConverted[1], currentRecord.getNrSamples()));
        rbRating.setRating(currentRecord.getRating());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.record_export:
                Export.export(this, getContext(), currentRecord);
                return true;
            case R.id.record_delete:
                return true;
        }
        return false;
    }
}
