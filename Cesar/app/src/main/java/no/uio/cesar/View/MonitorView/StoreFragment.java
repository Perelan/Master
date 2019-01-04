package no.uio.cesar.View.MonitorView;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import no.uio.cesar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment {

    private EditText title, description;
    private Button btn;

    public StoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_store, container, false);

        title = v.findViewById(R.id.store_title);
        description = v.findViewById(R.id.store_desc);

        btn = v.findViewById(R.id.store_btn);
        btn.setOnClickListener(view -> storeMonitorSession());

        return v;
    }

    private void storeMonitorSession() {
        System.out.println("here");
    }

}
