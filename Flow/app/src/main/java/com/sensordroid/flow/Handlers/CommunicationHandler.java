package com.sensordroid.flow.Handlers;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sensordroid.MainServiceConnection;
import com.sensordroid.flow.Bluetooth.BluetoothCallback;
import com.sensordroid.flow.Bluetooth.BluetoothHandler;
import com.sensordroid.flow.WrapperService;
import com.sensordroid.flow.util.JSONHelper;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommunicationHandler implements Runnable, BluetoothCallback {
    private static final String TAG ="CommunicationHandler";

    private int driverId;
    private String driverName;

    private Context context;
    private MainServiceConnection binder;

    private BluetoothHandler bluetoothHandler;
    private ServiceConnection bluetoothConnection;
    private BluetoothCallback callback;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private boolean interrupted;

    public CommunicationHandler(final MainServiceConnection binder, String name, int id, Context context) {
        this.driverId = id;
        this.driverName = name;

        this.context = context;
        this.binder = binder;

        this.interrupted = false;

        callback = this;
    }

    @Override
    public void run() {
        int sleepTime = 10_000;
        Log.d(TAG, "run: Running");

        bluetoothConnection();

        while (!interrupted) {
            if (Thread.currentThread().isInterrupted()) {
                interrupted = true;
                break;
            }

            if (!interrupted) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }
        }

        if (bluetoothHandler != null) {
            Log.d(TAG, "run: CLOSING");
            bluetoothHandler.closeAndNotifyGattDisconnection();
            context.unbindService(bluetoothConnection);
        }
    }

    /**
     * Establish a connection to the {@link BluetoothHandler} service. Upon service connection
     * store a references to named service, add a callback listener for the different action
     * events the service provides, and then connect {@link #connect()} to the handler.
     */
    private void bluetoothConnection() {

        bluetoothConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                bluetoothHandler = ((BluetoothHandler.LocalBinder) service).getService();

                if (bluetoothHandler == null) Log.d(TAG, "onServiceConnected: Bluetooth Handler Null?!");

                bluetoothHandler.setCallback(callback);

                connect();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bluetoothHandler = null;
                interrupted = true;
            }
        };

        Intent i = new Intent(context, BluetoothHandler.class);
        context.bindService(i, bluetoothConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Initialize the bluetooth handler {@link BluetoothHandler#init()} and proceed to fetch the
     * locally stored (in sharedpreferences) device and start collecting data from the device.
     */
    private void connect() {

        Log.d(TAG, "connect: Initing");
        if (!bluetoothHandler.init()) {
            Log.d(TAG, "connect: Unable to init Bluetooth");

            return;
        }

        Log.d(TAG, "connect: Connecting");
        String address = JSONHelper.retrieveDeviceList(context);

        Log.d(TAG, "connect: devices empty");
        if (address == null) return;

        Log.d(TAG, "connect: to device " + address);

        bluetoothHandler.setSelectedFlowSensor(address);

        bluetoothHandler.connect();
    }

    @Override
    public void onDataReceived(Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();

        if (action == null)  {
            Log.d(TAG, "handleBroadcastEvent: No action found");
            return;
        }

        Log.d(TAG, "onReceive: Action " + action);

        switch (action) {
            case BluetoothHandler.ACTION_GATT_CONNECTED:
                break;
            case BluetoothHandler.ACTION_GATT_DISCONNECTED:
                interrupted = true;
                break;
            case BluetoothHandler.ACTION_GATT_SERVICES_DISCOVERED:
                break;
            case BluetoothHandler.ACTION_DATA_AVAILABLE:
                String flowRespirationData = intent.getStringExtra(BluetoothHandler.EXTRA_DATA_FLOW_INFO_STRING);
                String heartRateData = intent.getStringExtra(BluetoothHandler.EXTRA_DATA_HEART_RATE);

                //Log.d(TAG, "handleBroadcastEvent: Respiration " + flowRespirationData);

                executor.submit(new DataHandler(
                        binder,
                        driverId,
                        new String[] { flowRespirationData },
                        WrapperService.getChannelList()));
                break;
            case BluetoothHandler.ACTION_DEVICE_METADATA_COMPLETE:

                System.out.println(">>> Updated");
                System.out.println(">>> " + bluetoothHandler.mSensorMetadata.batteryLevel);
                System.out.println(">>> " + bluetoothHandler.mSensorMetadata.firmwareRevision);
                System.out.println(">>> " + bluetoothHandler.mSensorMetadata.manufacturerName);
                break;
            case BluetoothHandler.ACTION_DEVICE_METADATA_UPDATED:

                break;
            default:
                Log.d(TAG, "handleBroadcastEvent: Unknown action:" + action);
                break;
        }
    }
}
