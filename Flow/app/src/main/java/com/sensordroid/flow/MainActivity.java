package com.sensordroid.flow;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import com.sensordroid.flow.Handlers.CommunicationHandler;
import com.sensordroid.flow.util.GattAttributes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainActivity extends Activity {
    private static final String TAG ="MainClass";

    public static final String sharedKey = "com.sensordroid.flow";
    public static final String channelKey = sharedKey + ".channels";
    public static final String macKey = sharedKey + ".mac";
    public static final String frequencyKey = sharedKey + ".frequency";
    public static final String frequenciesKey = sharedKey + ".frequencies";
    public static final String[] descriptionKeys = new String[]{sharedKey + ".d1", sharedKey + ".d2",
            sharedKey + ".d3", sharedKey + ".d4", sharedKey + ".d5", sharedKey + ".d6"};

    private String flow = "E7:B4:33:F2:ED:52";

    private static final long SCAN_PERIOD = 10_000; // 10 sec

    private CommunicationHandler mComHandler = new CommunicationHandler();

    private boolean mScanning;
    private Handler mHandler = new Handler();

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
            if (device.getName() != null && device.getName().equals("OarZpot")) {
                mComHandler.selectedFlowSensor = device;
                mComHandler.connect();

                scanLeDevice(false);
            }
    });

    private ServiceConnection mServiceConnection = new ServiceConnection() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent bindSensorIntent = new Intent(this, CommunicationHandler.class);
        startService(bindSensorIntent);
        bindService(bindSensorIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
