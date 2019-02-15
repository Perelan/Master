package no.uio.cesar.View.MonitorView;


import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.Model.Record;
import no.uio.cesar.R;
import no.uio.cesar.ViewModel.RecordViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment {

    private EditText title, description;
    private CardView btn;

    private RecordViewModel recordViewModel;

    public static final String TAG = "StoreFragment";

    public StoreFragment() {
        // Required empty public constructor
    }

    public static StoreFragment newInstance(long primaryKey) {
        
        Bundle args = new Bundle();
        args.putLong("key", primaryKey);
        
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

        title = v.findViewById(R.id.store_title);
        description = v.findViewById(R.id.store_desc);

        btn = v.findViewById(R.id.store_btn);
        btn.setOnClickListener(view -> storeMonitorSession());

        long primaryKey = getArguments().getLong("key");

        Record r = new Record(null);
        r.setId((int)primaryKey);
        r.setName(String.format("Record %d", (int)primaryKey));

        recordViewModel.update(r);

        Log.d(TAG, "onCreateView: key " + primaryKey);

        return v;
    }

    private void storeMonitorSession() {
        System.out.println("here");
    }

}
