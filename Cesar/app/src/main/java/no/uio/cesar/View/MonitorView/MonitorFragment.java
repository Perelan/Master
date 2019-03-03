package no.uio.cesar.View.MonitorView;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sensordroid.MainServiceConnection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.DSDService;
import no.uio.cesar.Model.Interface.DatabaseCallback;
import no.uio.cesar.Model.Payload;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.Model.Sensor;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.cesar.ViewModel.SampleViewModel;
import no.uio.ripple.RippleEffect;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonitorFragment extends Fragment implements DatabaseCallback {
    private static final String TAG = "Monitor";

    private Context mContext;

    private PowerManager.WakeLock wakeLock;

    private RippleEffect rp;
    private Chronometer cm;

    private TextView tvTitle;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SensorAdapter mAdapter;


    private LineGraphSeries<DataPoint> mSeries;

    private RecordViewModel recordViewModel;
    private Record currentRecord;
    private long currentRecordId = -1;

    private SampleViewModel sampleViewModel;

    private BottomSheetBehavior mBottomSheetBehavior;

    public MainServiceConnection msc;

    private List<String> publishers;

    private ServiceConnection serviceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msc = MainServiceConnection.Stub.asInterface(service);

            connect();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: intent" + intent);

            if (currentRecordId == -1) return;

            rp.pulse(rp.BREATH);

            Bundle b = intent.getExtras();
            String data = b.getString("data");

            Log.d(TAG, "onReceive: " + data + " id: " + currentRecordId);

            Sample newSample = new Sample(currentRecordId);

            sampleViewModel.insert(newSample, data);
        }
    };

    public MonitorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_monitor, container, false);

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

        PowerManager powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "CESAR::collection");

        wakeLock.acquire();

        mRecyclerView = v.findViewById(R.id.available_sensors);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SensorAdapter();

        mRecyclerView.setAdapter(mAdapter);

        tvTitle = v.findViewById(R.id.monitor_title);
        cm = v.findViewById(R.id.monitor_time);
        rp = v.findViewById(R.id.ripple);

        GraphView graph = v.findViewById(R.id.resp_graph);
        graph.getViewport().setMinY(1000);
        graph.getViewport().setMaxY(2000);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(1000);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        mSeries = new LineGraphSeries<>();

        graph.addSeries(mSeries);

        CardView stopMonitor = v.findViewById(R.id.monitor_stop);
        stopMonitor.setOnClickListener(view -> dialogSessionEnd());

        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        sampleViewModel = ViewModelProviders.of(this).get(SampleViewModel.class);

        currentRecord = new Record();
        recordViewModel.insert(currentRecord, this);

        return v;
    }

    private void dialogSessionEnd() {
        new AlertDialog.Builder(mContext)
                .setMessage("Are you done with the monitor session?")
                .setCancelable(false)
                .setNegativeButton("NO!", null)
                .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                    storeAndFinishSession();
                }).create().show();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: CALLED");
        
        super.onDestroy();

        cleanup();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onInsertGetRecordId(long id) {
        Log.d(TAG, "onInsertDatabaseId: " + id);

        currentRecordId = id;

        Intent intent = new Intent(MainServiceConnection.class.getName());
        intent.setAction("com.sensordroid.ADD_DRIVER");
        intent.setPackage("com.sensordroid");
        getActivity().bindService(intent, serviceCon, Service.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(mContext).registerReceiver(listener, new IntentFilter("PUT_DATA"));

        /*
        sampleViewModel.getSamplesForRecord(currentRecordId).observe(this, samples -> {
            Log.d(TAG, "onInsertGetRecordId: " + samples.size());
            if (samples.size() == 0) return;
            Sample latestSample = samples.get(samples.size() - 1);

            int values = Uti.extractFlowData(latestSample.getSample());

            DataPoint dp = new DataPoint(Uti.calcElapsedTime(cm.getBase()) / 1000.0, values);

            System.out.println("X: " + Uti.calcElapsedTime(cm.getBase()) + " y: " + values);

            mSeries.appendData(dp, true, 40);
        });*/
    }

    private void connect() {

        Handler h = new Handler();

        h.postDelayed(new Runnable() {
              @Override
              public void run() {
                  try {
                      publishers = msc.getPublishers();

                      if (publishers.isEmpty()) {
                          h.postDelayed(this, 1_000);

                          return;
                      }

                      h.removeCallbacks(this);

                      tvTitle.setText("Connection Established!");

                      cm.setBase(SystemClock.elapsedRealtime());
                      cm.start();

                      String s = publishers.get(0).split(",")[0];

                      mAdapter.parseSensorData(publishers);

                      System.out.println(msc.Subscribe(s, 0, getActivity().getPackageName(), DSDService.class.getName()));

                  } catch (RemoteException e) {
                      e.printStackTrace();
                  }
              }
          }, 1_000);
    }

    private void storeAndFinishSession() {
        cm.stop();

        long monitorTime = SystemClock.elapsedRealtime() - cm.getBase();

        Fragment f = StoreFragment.newInstance(currentRecordId, monitorTime);

        Uti.commitFragmentTransaction(getActivity(), f);
    }

    private void cleanup() {

        if (wakeLock.isHeld()) wakeLock.release();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(listener);

        if (msc != null) {
            try {
                if (publishers.isEmpty()) {
                    System.out.println("IN STORE: NO PUBLISHERS");
                } else {
                    String s = publishers.get(0).split(",")[0];

                    System.out.println(msc.Unsubscribe(s, DSDService.class.getName()));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (serviceCon != null) {
            getActivity().unbindService(serviceCon);
        }
    }
}
