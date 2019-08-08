package no.uio.cesar.View.RecordView;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.Dispatcher.ConnectionCallback;
import no.uio.cesar.Dispatcher.ConnectionHandler;
import no.uio.cesar.Dispatcher.ConnectivityHandler;
import no.uio.cesar.Model.Interface.DatabaseCallback;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Graph;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.cesar.ViewModel.SampleViewModel;
import no.uio.ripple.RippleEffect;

public class RecordingFragment extends Fragment implements DatabaseCallback, ConnectionCallback {
    private static final String TAG = "Monitor";

    private FragmentActivity mContext;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private RippleEffect rp;
    private Chronometer cm;

    private TextView tvTitle;

    private SensorAdapter mAdapter;

    private LiveData<List<Sample>> sampleCollection;

    private LineGraphSeries<DataPoint> mSeries;

    private RecordViewModel recordViewModel;
    private long currentRecordId = -1;

    private SampleViewModel sampleViewModel;

    private final LifecycleOwner self = this;

    private ConnectionHandler conHandler;
    private ConnectivityHandler conObserver;

    public RecordingFragment() {
        // Required empty public constructor
    }

    // Connectivity checker.
    private Runnable runnable = () -> {
        Log.d(TAG, ": Restarted");
        if (powerManager.isInteractive()) rp.pulse(rp.IDLE);

        conHandler.reconnect();
        conObserver.retry();
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_monitor, container, false);

        View bottomSheet = v.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);

        powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "CESAR::collection");

        wakeLock.acquire();

        RecyclerView mRecyclerView = v.findViewById(R.id.available_sensors);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SensorAdapter();
        mRecyclerView.setAdapter(mAdapter);

        tvTitle = v.findViewById(R.id.monitor_title);
        cm = v.findViewById(R.id.monitor_time);
        rp = v.findViewById(R.id.ripple);

        if (powerManager.isInteractive()) rp.pulse(rp.IDLE);

        GraphView graph = v.findViewById(R.id.resp_graph);
        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
        Graph.changeParams(graph);

        CardView stopMonitor = v.findViewById(R.id.monitor_stop);
        stopMonitor.setOnClickListener(view -> dialogSessionEnd());

        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        sampleViewModel = ViewModelProviders.of(this).get(SampleViewModel.class);

        conHandler = new ConnectionHandler(mContext, this);
        conObserver = new ConnectivityHandler(runnable);

        recordViewModel.insert(new Record(), this);

        return v;
    }

    @Override
    public void onInsertGetRecordId(long id) {
        Log.d(TAG, "onInsertDatabaseId: " + id);

        currentRecordId = id;

        conHandler.establish();

        LocalBroadcastManager
                .getInstance(mContext)
                .registerReceiver(listener, new IntentFilter("PUT_DATA"));

        sampleCollection = sampleViewModel.getSamplesForRecord(currentRecordId);
    }

    @Override
    public void connected(List<String> publishers) {
        tvTitle.setText("Recording!");

        cm.setBase(SystemClock.elapsedRealtime());
        cm.start();

        mAdapter.parseSensorData(publishers);

        conObserver.start();
    }


    // New data from the sensor! Sent from DSDService.
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: intent" + intent);

            if (currentRecordId == -1) return;
            if (powerManager.isInteractive()) rp.pulse(rp.BREATH);

            conObserver.reset();

            Bundle b = intent.getExtras();
            if (b == null) return;

            String data = b.getString("data");

            Log.d(TAG, "onReceive: " + data + " id: " + currentRecordId);

            sampleViewModel.insert(new Sample(currentRecordId), data);
        }
    };

    private void dialogSessionEnd() {
        new AlertDialog.Builder(mContext)
                .setMessage("Are you done with the monitor session?")
                .setCancelable(false)
                .setNegativeButton("No!", null)
                .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                    storeAndFinishSession();
                }).create().show();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();

        if (wakeLock.isHeld()) wakeLock.release();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(listener);

        conHandler.cleanup();
        conObserver.stop();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mContext = (FragmentActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mContext = null;
    }

    private void storeAndFinishSession() {
        cm.stop();

        long monitorTime = SystemClock.elapsedRealtime() - cm.getBase();

        Fragment f = StoreFragment.newInstance(currentRecordId, monitorTime);

        Uti.commitFragmentTransaction(mContext, f);
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                sampleCollection.removeObservers(self);
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                sampleCollection.observe(self, samples -> {

                    if (samples.size() == 0) return;
                    Sample latestSample = samples.get(samples.size() - 1);

                    int values = Uti.extractFlowData(latestSample.getSample());

                    DataPoint dp = new DataPoint(latestSample.getImplicitTS(), values);

                    mSeries.appendData(dp, true, 10_000);
                });
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

}
