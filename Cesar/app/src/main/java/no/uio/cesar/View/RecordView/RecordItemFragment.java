package no.uio.cesar.View.RecordView;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import no.uio.cesar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordItemFragment extends Fragment {


    public RecordItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_item, container, false);
        return view;
    }
}
