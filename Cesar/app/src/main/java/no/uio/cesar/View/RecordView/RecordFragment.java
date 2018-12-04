package no.uio.cesar.View.RecordView;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.R;
import no.uio.cesar.ViewModel.RecordViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private RecordViewModel recordViewModel;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_record, container, false);

        mRecyclerView = v.findViewById(R.id.record_list_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(),
                RecyclerView.VERTICAL,
                false
        ));
        mRecyclerView.setHasFixedSize(true);

        final RecordAdapter adapter = new RecordAdapter();
        mRecyclerView.setAdapter(adapter);

        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        recordViewModel.getAllRecords().observe(this, records -> {
            System.out.println(">>> new data " + records.size());

            adapter.insertRecord(records);
        });

        return v;
    }

}
