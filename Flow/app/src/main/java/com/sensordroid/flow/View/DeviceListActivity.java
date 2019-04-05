package com.sensordroid.flow.View;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sensordroid.flow.Bluetooth.BluetoothHandler;
import com.sensordroid.flow.R;

import java.util.ArrayList;



public class DeviceListActivity extends AppCompatActivity {
    private static final String TAG = "DeviceListActivity";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Handler mHandler = new Handler();
    private BluetoothHandler mComHandler = new BluetoothHandler();

    private ServiceConnection mBluetoothServiceConnection = null;

    private boolean mScanning;

    private static final long SCAN_PERIOD = 10_000; // 10 sec

    private ArrayList<BluetoothDevice> devicesDataset;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView title;
    private ProgressBar progress;
    private ImageView refresh;

    private final int REQUEST_ENABLE_BT = 1;
    private final int REQUEST_ACCESS_LOC = 2;

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

        handlePrivileges();

        title = findViewById(R.id.connect_title);
        progress = findViewById(R.id.loading);
        refresh = findViewById(R.id.refresh);

        refresh.setOnClickListener(v -> refreshDeviceLoading());

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);

        mRecyclerView = findViewById(R.id.devicelist);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        devicesDataset = new ArrayList<>();

        mAdapter = new DeviceAdapter(devicesDataset, mListen);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            refreshDeviceLoading();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        mBluetoothServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                Log.i(TAG, "onServiceConnected");

                mComHandler = ((BluetoothHandler.LocalBinder) service).getService();

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

        Intent bindIntent = new Intent(this, BluetoothHandler.class);
        bindService(bindIntent, mBluetoothServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                System.out.println(">>>>>>>>> Here " + resultCode);

                if (resultCode == RESULT_OK) {
                    // Bluetooth toggle granted.
                    refreshDeviceLoading();
                } else {
                    // Bluetooth toggle denied.
                    title.setText("Enable Bluetooth and Restart the App!");
                    Toast.makeText(this, "Please enable Bluetooth.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                Log.w(TAG, "onActivityResult: wrong request code: " +  requestCode);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ACCESS_LOC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                refreshDeviceLoading();
            } else {

            }
        }
    }

    private void handlePrivileges() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth LE is not supported on this device", Toast.LENGTH_LONG).show();
            finish();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOC);
        }

        BluetoothAdapter adapter = mComHandler.getBluetoothAdapter();

        if (adapter == null || !adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    void refreshDeviceLoading() {
        scanLeDevice(false);
        devicesDataset.clear();
        mAdapter.notifyDataSetChanged();
        progress.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.INVISIBLE);
        title.setText("Searching for devices...");
        scanLeDevice(true);
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
        BluetoothAdapter adapter = mComHandler.getBluetoothAdapter();

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                mScanning = false;
                adapter.stopLeScan(mLeScanCallback);
                showBluetoothDeviceResult();
            }, SCAN_PERIOD);

            mScanning = true;
            adapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            adapter.stopLeScan(mLeScanCallback);
        }
    }

    public void showBluetoothDeviceResult() {
        if (devicesDataset.size() == 0) {
            // Bluetooth scanning has terminated, and there are no device results.
            title.setText("No devices found, try to refresh...");
        } else {
            // Bluetooth scanning has terminated, and there are results.
            title.setText("Device results...");
        }

        progress.setVisibility(View.INVISIBLE);
        refresh.setVisibility(View.VISIBLE);
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

