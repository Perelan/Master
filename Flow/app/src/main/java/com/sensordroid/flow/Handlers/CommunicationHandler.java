package com.sensordroid.flow.Handlers;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.sensordroid.MainServiceConnection;
import com.sensordroid.flow.WrapperService;
import com.sensordroid.flow.util.JSONHelper;

import java.util.ArrayList;
import java.util.Locale;
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


    private void bluetoothConnection() {

        bluetoothConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                bluetoothHandler = ((BluetoothHandler.LocalBinder) service).getService();
                
                if (bluetoothHandler == null) Log.d(TAG, "onServiceConnected: wtf");
                
                bluetoothHandler.setCallback(callback);

                connect();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bluetoothHandler = null;
            }
        };

        Intent i = new Intent(context, BluetoothHandler.class);
        context.bindService(i, bluetoothConnection, context.BIND_AUTO_CREATE);
    }

    @Override
    public void run() {
        int sleepTime = 1000;
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
                    if (sleepTime < 30_000) sleepTime *= 2;
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }
        }

        if (bluetoothHandler != null) {
            Log.d(TAG, "run: CLOSING");
            bluetoothHandler.closeAndNotifyGattDisconnection();
        }

        /*
        while (!interrupted) {
            if (Thread.currentThread().isInterrupted()) {
                interrupted = true;
                break;
            }

            if (connect()) {
                Log.d(TAG, "run: Connection successfull");
                sleepTime = 1000;

                // Collect data
                collectData();
            }

            //resetConnection();

            if (!interrupted) {
                try {
                    Thread.sleep(sleepTime);

                    if (sleepTime < 30_000) {
                        sleepTime = sleepTime * 2;
                    }

                } catch (InterruptedException e) {
                    interrupted = true;
                    e.printStackTrace();
                    return;
                }
            }

        }*/
    }

    private void collectData() {
        boolean status = bluetoothHandler.connect();
    }

    private boolean connect() {

        Log.d(TAG, "connect: bluetooth");

        Log.d(TAG, "connect: bluetooth done");

        if (bluetoothHandler == null) return false;

        Log.d(TAG, "connect: Initing");
        if (!bluetoothHandler.init()) {
            Log.d(TAG, "connect: Unable to init Bluetooth");

            return false;
        }

        Log.d(TAG, "connect: Connecting");
        ArrayList<BluetoothDevice> devices = JSONHelper.retrieveDeviceList(context);

        if (devices == null) return false;

        BluetoothDevice device = devices.get(0);

        bluetoothHandler.setSelectedFlowSensor(device);

        bluetoothHandler.connect();

        return true;
    }

    private void resetConnection() {
        if (bluetoothHandler == null || bluetoothConnection == null) {
            Log.d(TAG, "resetConnection: Null refs");
        }

        bluetoothHandler.closeAndNotifyGattDisconnection();
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

                Log.d(TAG, "handleBroadcastEvent: Respiration " + flowRespirationData);

                // Get the data from the sensor and send it the the demuxer.

                executor.submit(new DataHandler(
                        binder,
                        driverId,
                        new String[] {flowRespirationData},
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
