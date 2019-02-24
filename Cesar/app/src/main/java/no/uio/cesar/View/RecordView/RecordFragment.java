package no.uio.cesar.View.RecordView;


import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sensordroid.MainServiceConnection;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.R;
import no.uio.cesar.View.MonitorActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {

    ArrayList<Sensor> mDummyData;

    private RecyclerView mRecyclerView;
    private SensorAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private MainServiceConnection msc;


    public RecordFragment() {
        // Required empty public constructor
    }

    private ServiceConnection serviceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msc = MainServiceConnection.Stub.asInterface(service);

            try {
                Log.d("RecordFragment", "onServiceConnected: " + msc.getPublishers());

                mAdapter.parseSensorData(msc.getPublishers());
            } catch (Exception e) { }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (serviceCon != null) {
            getActivity().unbindService(serviceCon);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_record, container, false);

        CardView cv = v.findViewById(R.id.monitor_button);

        cv.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), MonitorActivity.class));
        });

        mRecyclerView = v.findViewById(R.id.available_sensors);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SensorAdapter();

        mRecyclerView.setAdapter(mAdapter);

        Intent intent = new Intent(MainServiceConnection.class.getName());
        intent.setAction("com.sensordroid.ADD_DRIVER");
        intent.setPackage("com.sensordroid");
        getActivity().bindService(intent, serviceCon, Service.BIND_AUTO_CREATE);

        return v;
    }

}
