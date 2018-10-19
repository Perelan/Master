package com.sensordroid.flow;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensordroid.flow.Handlers.CommunicationHandler;

import java.util.ArrayList;

interface OnDeviceClickListener {
    void onDeviceClick(int position);
}

public class DeviceListActivity extends AppCompatActivity {
    private static final String TAG = "DeviceListActivity";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Handler mHandler = new Handler();
    private CommunicationHandler mComHandler = new CommunicationHandler();

    private ServiceConnection mBluetoothServiceConnection = null;

    private boolean mScanning;

    private static final long SCAN_PERIOD = 10_000; // 10 sec

    private ArrayList<BluetoothDevice> devicesDataset;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    Intent bindIntent;

    private OnDeviceClickListener mListen = position -> {
        Log.d(TAG, "postion: " + position);
        scanLeDevice(false);

        BluetoothDevice device = devicesDataset.get(position);

        if (device != null) {

            Bundle b = new Bundle();
            b.putParcelable(BluetoothDevice.EXTRA_DEVICE, device);

            Intent result = new Intent();
            result.putExtras(b);

            setResult(Activity.RESULT_OK, result);

            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);

        mRecyclerView = findViewById(R.id.devicelist);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        devicesDataset = new ArrayList<>();

        mAdapter = new DeviceAdapter(devicesDataset, mListen);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            scanLeDevice(false);
            devicesDataset.clear();
            mAdapter.notifyDataSetChanged();
            scanLeDevice(true);

            mSwipeRefreshLayout.setRefreshing(false);
        });

        mBluetoothServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                Log.i(TAG, "onServiceConnected");

                mComHandler = ((CommunicationHandler.LocalBinder) service).getService();

                if (!mComHandler.init()) {
                    Log.e(TAG, "onServiceConnected: Unable to initialize Bluetooth");
                    finish();
                    return;
                }

                scanLeDevice(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mComHandler = null;
            }
        };

        bindIntent = new Intent(this, CommunicationHandler.class);
        bindService(bindIntent, mBluetoothServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mBluetoothServiceConnection != null) {
            unbindService(mBluetoothServiceConnection);
        }

        super.onDestroy();
    }

    // Stops scanning after 10 seconds.
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                mScanning = false;
                mComHandler.mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }, SCAN_PERIOD);

            mScanning = true;
            mComHandler.mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mComHandler.mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            (BluetoothDevice device, int rssi, byte[] scanRecord) -> runOnUiThread(() -> {
                System.out.println(">>> Device: " + device.getName());
                if (device.getName() != null) {
                    addDevice(device);
                }
            });

    private void addDevice(BluetoothDevice device) {
        if (devicesDataset.contains(device)) {
            return;
        }

        devicesDataset.add(device);
        mAdapter.notifyDataSetChanged();
    }
}

class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private ArrayList<BluetoothDevice> mDataset;
    private OnDeviceClickListener mClickListener;

    class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mDeviceTitle, mDeviceMac;

        DeviceViewHolder(@NonNull View v) {
            super(v);
            mDeviceTitle = v.findViewById(R.id.device_title);
            mDeviceMac = v.findViewById(R.id.device_mac);
            v.setOnClickListener(this);
        }

        public void onClick(View v) {
            mClickListener.onDeviceClick(getAdapterPosition());
        }
    }

    DeviceAdapter(ArrayList<BluetoothDevice> data, OnDeviceClickListener listener) {
        mDataset = data;
        mClickListener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.device_item, viewGroup, false);

        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder viewHolder, int i) {
        viewHolder.mDeviceTitle.setText(mDataset.get(i).getName());
        viewHolder.mDeviceMac.setText(mDataset.get(i).getAddress());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
