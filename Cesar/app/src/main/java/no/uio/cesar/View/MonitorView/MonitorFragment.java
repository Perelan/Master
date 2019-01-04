package no.uio.cesar.View.MonitorView;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.ripple.RippleEffect;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonitorFragment extends Fragment {

    private Context mContext;

    private RippleEffect rp;
    private Chronometer cm;

    private LineGraphSeries<DataPoint> mSeries;

    private RecordViewModel recordViewModel;

    private BottomSheetBehavior mBottomSheetBehavior;


    public MonitorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_monitor, container, false);
        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        View bottomSheet = v.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    return;
                }

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                    return;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        cm = v.findViewById(R.id.monitor_time);
        cm.start();

        rp = v.findViewById(R.id.ripple);

        rp.setOnClickListener(view -> rp.pulse(rp.BREATH));

        rp.pulse(rp.BATTERY);

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> dialogSessionEnd());

        return v;
    }


    public void dialogSessionEnd() {
        new AlertDialog.Builder(mContext)
                .setMessage("Are you done with the monitor session?")
                .setCancelable(false)
                .setNegativeButton("NO!", null)
                .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                    storeAndFinishSession();
                }).create().show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    public void storeAndFinishSession() {
        cm.stop();

        Fragment f = new StoreFragment();

        Uti.commitFragmentTransaction(getActivity(), f);

        /* returns MS elapsed time
        System.out.println(SystemClock.elapsedRealtime() - cm.getBase());

        ArrayList<Sample> sample = new ArrayList<>();

        Record newRecord = new Record("test", sample);

        recordViewModel.insert(newRecord);*/
    }
}
