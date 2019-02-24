package no.uio.cesar.View.FeedView;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.Model.Record;
import no.uio.cesar.R;
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
    public void onRecordItemClick(View v, int position) {
        System.out.println(position);
        //recordViewModel.delete(adapter.getRecords().get(position));

        List<Record> allRecords = adapter.getRecords();
        Record selectedRecord = allRecords.get(position);

        RecordFragment fragment = RecordFragment.newInstance(selectedRecord);

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        /*getActivity().getSupportFragmentManager().beginTransaction()
                //.replace(R.id.fragment_container, fragment, null)
                .addToBackStack(null)
                .commit();*/

        DialogFragment dialogFragment = RecordFragment.newInstance(selectedRecord);
        dialogFragment.show(ft, "dialog");
    }
}
