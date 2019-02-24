package no.uio.cesar.View.FeedView;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.List;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.Model.Record;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Export;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.ViewModel.RecordViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment implements FeedViewClickListener {

    private RecyclerView mRecyclerView;

    private RecordViewModel recordViewModel;

    private FeedAdapter adapter;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        setHasOptionsMenu(true);

        mRecyclerView = v.findViewById(R.id.record_list_view);

        LinearLayoutManager lym = new LinearLayoutManager(getContext());
        lym.setReverseLayout(true);
        lym.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(lym);

        mRecyclerView.setHasFixedSize(true);

        adapter = new FeedAdapter(this);

        mRecyclerView.setAdapter(adapter);

        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        recordViewModel.getAllRecords().observe(this, records -> {
            System.out.println(">>> new data " + records.size());

            adapter.insertRecord(records);
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.feed, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    System.out.println("HERE " + data.getData());
                    Uti.parseRecordFile(getContext(), data.getDataString());
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.feed_import:
                importRecords();
                return true;
            case R.id.feed_export:
                Export.exportAll(this);
                return true;
            case R.id.feed_delete:
                recordViewModel.getAllRecords().observe(this, records -> {
                    for (Record r : records) {
                        recordViewModel.delete(r);
                    }
                });
                return true;
        }

        return false;
    }

    @Override
    public void onRecordItemClick(View v, int position) {
        List<Record> allRecords = adapter.getRecords();
        Record selectedRecord = allRecords.get(position);

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        /*getActivity().getSupportFragmentManager().beginTransaction()
                //.replace(R.id.fragment_container, fragment, null)
                .addToBackStack(null)
                .commit();*/

        DialogFragment dialogFragment = RecordFragment.newInstance(selectedRecord);
        dialogFragment.show(ft, "dialog");
    }

    public void importRecords() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }
}
