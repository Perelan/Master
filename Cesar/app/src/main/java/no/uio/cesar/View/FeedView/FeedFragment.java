package no.uio.cesar.View.FeedView;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.Utils.ExportObject;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Export;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.View.ProfileView.ProfileFragment;
import no.uio.cesar.View.SettingsActivity;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.cesar.ViewModel.SampleViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment implements FeedViewClickListener, Toolbar.OnMenuItemClickListener {

    private RecordViewModel recordViewModel;
    private SampleViewModel sampleViewModel;

    private FeedAdapter adapter;

    private TextView subtitle;

    private RecyclerView mRecyclerView;
    private View mGetStartedView;


    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        mRecyclerView = v.findViewById(R.id.record_list_view);
        mGetStartedView = v.findViewById(R.id.get_started_container);

        subtitle = v.findViewById(R.id.feed_subtitle);

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.feed);
        toolbar.setOnMenuItemClickListener(this);

        View profile = v.findViewById(R.id.user_profile);
        profile.setOnClickListener(l -> {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });

        LinearLayoutManager lym = new LinearLayoutManager(getContext());
        lym.setReverseLayout(true);
        lym.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(lym);
        mRecyclerView.setHasFixedSize(false);
        adapter = new FeedAdapter(this);
        mRecyclerView.setAdapter(adapter);

        sampleViewModel = ViewModelProviders.of(this).get(SampleViewModel.class);
        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        recordViewModel.getAllRecords().observe(this, records -> {
            subtitle.setText(String.format(Locale.getDefault(), "- %d records", records.size()));

            lym.scrollToPosition(records.size() - 1);
            adapter.insertRecord(records);
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    List<ExportObject> list = Uti.parseRecordFile(getActivity(), data.getData());
                    if (list == null) return;

                    for (ExportObject obj : list) {
                        Record r = obj.getRecord();
                        r.setId(0);

                        recordViewModel.insert(r, id -> {

                            for (int i = 0; i < obj.getSamples().size(); i++) {
                                Sample s = obj.getSamples().get(i);
                                s.setRecordId(id);
                                s.setId(0);

                                sampleViewModel.insert(s);
                            }
                        });
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRecordAnalyticsClick(Record record) {
        Uti.commitFragmentTransaction(getActivity(), AnalyticsFragment.newInstance(record), "analytics");
    }

    @Override
    public void onRecordDeleteClick(Record record) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete current record?")
                .setMessage("Do you want to delete this record with " + record.getNrSamples() + " samples?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton("Yes", (dialog, which) -> recordViewModel.delete(record))
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRecordShareClick(Record record) {
        Export.export(this, record);
    }

    private void importRecords() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.feed_import:
                importRecords();
                return true;
            case R.id.feed_export:
                Export.exportAll(this);
                return true;
            case R.id.feed_delete:
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete all record?")
                        .setMessage("Do you want to delete all record?")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            recordViewModel.getAllRecords().observe(this, records -> {
                                for (Record r : records) {
                                    recordViewModel.delete(r);
                                }
                            });
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
        }

        return false;
    }

    public void toggleGetStaredView() {
        if (mGetStartedView.getVisibility() == View.VISIBLE) {
            mGetStartedView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mGetStartedView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }
}
