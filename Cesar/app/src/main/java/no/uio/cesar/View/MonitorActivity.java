package no.uio.cesar.View;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.R;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.ripple.RippleEffect;

public class MonitorActivity extends AppCompatActivity {

    private RippleEffect rp;
    private Chronometer cm;

    private LineGraphSeries<DataPoint> mSeries;

    Handler mHandler;

    private RecordViewModel recordViewModel;

    private BottomSheetBehavior mBottomSheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> dialogSessionEnd());

        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        View bottomSheet = findViewById(R.id.bottom_sheet);
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

        mHandler = new Handler();

        cm = findViewById(R.id.monitor_time);
        cm.start();

        rp = findViewById(R.id.ripple);

        rp.setOnClickListener(view -> rp.pulse(rp.BREATH));

        rp.pulse(rp.BATTERY);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        mSeries = new LineGraphSeries<>(generateData());
        graph.addSeries(mSeries);
    }

    public void onResume() {
        super.onResume();
        Runnable mTimer1 = new Runnable() {
            @Override
            public void run() {
                mSeries.resetData(generateData());
                mHandler.postDelayed(this, 300);
            }
        };
        mHandler.postDelayed(mTimer1, 300);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putLong("timeElapsed", cm.getBase());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        cm.setBase(SystemClock.elapsedRealtime() - savedInstanceState.getLong("timeElapsed"));
    }

    @Override
    public void onBackPressed() {
        dialogSessionEnd();
    }



    public void dialogSessionEnd() {
        new AlertDialog.Builder(this)
                .setMessage("Are you done with the monitor session?")
                .setCancelable(false)
                .setNegativeButton("NO!", null)
                .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                    storeAndFinishSession();
                }).create().show();
    }

    public void storeAndFinishSession() {
        cm.stop();

        // returns MS elapsed time
        System.out.println(SystemClock.elapsedRealtime() - cm.getBase());

        ArrayList<Sample> sample = new ArrayList<>();

        Record newRecord = new Record("test", sample);

        recordViewModel.insert(newRecord);

        finish();
    }

    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    double mLastRandom = 2;
    Random mRand = new Random();

    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }
}
