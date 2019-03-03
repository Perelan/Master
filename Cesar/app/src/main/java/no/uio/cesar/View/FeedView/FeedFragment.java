package no.uio.cesar.View.FeedView;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.gauriinfotech.commons.Commons;
import no.uio.cesar.ExportObject;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.User;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Constant;
import no.uio.cesar.Utils.Export;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.View.ProfileView.ProfileFragment;
import no.uio.cesar.ViewModel.RecordViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment implements FeedViewClickListener, Toolbar.OnMenuItemClickListener {

    private RecyclerView mRecyclerView;

    private RecordViewModel recordViewModel;

    private FeedAdapter adapter;

    private TextView subtitle;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        mRecyclerView = v.findViewById(R.id.record_list_view);

        subtitle = v.findViewById(R.id.feed_subtitle);

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.feed);
        toolbar.setOnMenuItemClickListener(this);

        LinearLayoutManager lym = new LinearLayoutManager(getContext());
        lym.setReverseLayout(true);
        lym.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(lym);

        mRecyclerView.setHasFixedSize(false);

        adapter = new FeedAdapter(this);

        mRecyclerView.setAdapter(adapter);

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

    public void importRecords() {
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


}
