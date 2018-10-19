package com.sensordroid.flow;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import com.sensordroid.flow.Handlers.CommunicationHandler;
import com.sensordroid.ripple.RippleEffect;

import java.util.Locale;

public class MainActivity extends Activity {
    private static final String TAG ="MainClass";
    private static final int REQUEST_SELECT_SENSOR = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    public static final String sharedKey = "com.sensordroid.flow";
    public static final String channelKey = sharedKey + ".channels";
    public static final String macKey = sharedKey + ".mac";
    public static final String frequencyKey = sharedKey + ".frequency";
    public static final String frequenciesKey = sharedKey + ".frequencies";
    public static final String[] descriptionKeys = new String[]{sharedKey + ".d1", sharedKey + ".d2",
            sharedKey + ".d3", sharedKey + ".d4", sharedKey + ".d5", sharedKey + ".d6"};

    private String flow = "E7:B4:33:F2:ED:52";

    private ServiceConnection mServiceConnection;
    private CommunicationHandler mComHandler = new CommunicationHandler();

    private boolean mScanning;

    private RippleEffect rp;
    private TextView mSensorTitle, mSensorMac, mSensorBattery, mSensorFirmware;

    private BroadcastReceiver mSensorStateListender = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Data " + context.getPackageName());
            runOnUiThread(() -> {
                handleBroadcastEvent(intent);
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rp              = findViewById(R.id.ripple);
        mSensorTitle    = findViewById(R.id.sensor_title);
        mSensorMac      = findViewById(R.id.sensor_mac);
        mSensorBattery  = findViewById(R.id.sensor_battery);
        mSensorFirmware = findViewById(R.id.sensor_firmware);

        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                Log.i(TAG, "onServiceConnected");

                mComHandler = ((CommunicationHandler.LocalBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mComHandler = null;
            }
        };

        Intent bindSensorIntent = new Intent(this, CommunicationHandler.class);
        startService(bindSensorIntent);
        bindService(bindSensorIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(CommunicationHandler.ACTION_GATT_CONNECTED);
        filter.addAction(CommunicationHandler.ACTION_GATT_DISCONNECTED);
        filter.addAction(CommunicationHandler.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(CommunicationHandler.ACTION_DATA_AVAILABLE);
        filter.addAction(CommunicationHandler.ACTION_DEVICE_METADATA_COMPLETE);
        filter.addAction(CommunicationHandler.ACTION_DEVICE_METADATA_UPDATED);
        registerReceiver(mSensorStateListender, filter);

        Intent i = new Intent(MainActivity.this, DeviceListActivity.class);
        startActivityForResult(i, REQUEST_SELECT_SENSOR);
    }

    private void handleBroadcastEvent(Intent intent) {
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
            case CommunicationHandler.ACTION_GATT_CONNECTED:
                break;
            case CommunicationHandler.ACTION_GATT_DISCONNECTED:
                break;
            case CommunicationHandler.ACTION_GATT_SERVICES_DISCOVERED:
                break;
            case CommunicationHandler.ACTION_DATA_AVAILABLE:
                String flowRespirationData = intent.getStringExtra(CommunicationHandler.EXTRA_DATA_FLOW_INFO_STRING);
                String heartRateData = intent.getStringExtra(CommunicationHandler.EXTRA_DATA_HEART_RATE);

                rp.pulse(rp.BREATH);
                Log.d(TAG, "handleBroadcastEvent: Respiration " + flowRespirationData);

                break;
            case CommunicationHandler.ACTION_DEVICE_METADATA_COMPLETE:
                rp.pulse(rp.IDLE);

                System.out.println(">>> Updated");
                System.out.println(">>> " + mComHandler.mSensorMetadata.batteryLevel);
                System.out.println(">>> " + mComHandler.mSensorMetadata.firmwareRevision);
                System.out.println(">>> " + mComHandler.mSensorMetadata.manufacturerName);
                mSensorBattery.setText(String.format(Locale.getDefault(), "%d%%", mComHandler.mSensorMetadata.batteryLevel));
                mSensorFirmware.setText(String.format("Firmware: %s", mComHandler.mSensorMetadata.firmwareRevision));
                break;
            case CommunicationHandler.ACTION_DEVICE_METADATA_UPDATED:

                break;
            default:
                Log.d(TAG, "handleBroadcastEvent: Unknown action:" + action);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mSensorStateListender);

        if (mServiceConnection != null) {
            unbindService(mServiceConnection);

            if (isFinishing()) {
                stopService(new Intent(this, CommunicationHandler.class));
            }
        }

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: request code " + requestCode);
        switch (requestCode) {
            case REQUEST_SELECT_SENSOR:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    System.out.println(" >>> " + device.getName());
                    mComHandler.setSelectedFlowSensor(device);

                    mComHandler.connect();
                }
                break;
            default:
                Log.w(TAG, "onActivityResult: wrong request code " + requestCode);
                break;
        }
    }
}
