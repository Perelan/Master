package com.sensordroid.flow;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensordroid.MainServiceConnection;
import com.sensordroid.flow.Handlers.CommunicationHandler;
import com.sensordroid.ripple.RippleEffect;

import java.util.List;
import java.util.Locale;

// bgcolor: #44628e
// iconcolor: #4572b6

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG ="MainClass";

    private static int sensorState = 0;
    private static final int SENSOR_STATE_CONNECTING = 1;
    private static final int SENSOR_STATE_CONNECTED = 2;
    private static final int SENSOR_STATE_DISCONNECTED = 3;

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
    private TextView mSensorTitle, mSensorMac, mSensorBattery, mSensorFirmware, mSensorState, mNewConnection, mIndicator;

    private CardView mButton;
    private TextView mButtonText;

    private BroadcastReceiver mSensorStateListener = new BroadcastReceiver() {
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

        System.out.println("HAHHAHA");

        rp              = findViewById(R.id.ripple);
        mSensorTitle    = findViewById(R.id.sensor_title);
        mSensorMac      = findViewById(R.id.sensor_mac);
        mSensorBattery  = findViewById(R.id.sensor_battery);
        mSensorFirmware = findViewById(R.id.sensor_firmware);
        mIndicator      = findViewById(R.id.sensor_state_indicator);


        mNewConnection  = findViewById(R.id.sensor_new_device);
        mNewConnection.setOnClickListener(this);

        mSensorState    = findViewById(R.id.sensor_state);

        mButtonText     = findViewById(R.id.info_text);
        mButton         = findViewById(R.id.sensor_button);
        mButton.setOnClickListener(this);

        Intent broadcast = new Intent();
        ComponentName name = new ComponentName("com.sensordroid", "com.sensordroid.SensorDiscovery");
        Bundle b = new Bundle();
        b.putString("name", "Flow");
        b.putString("packageName", "com.sensordroid.flow");
        broadcast.putExtras(b);
        broadcast.setComponent(name);
        sendBroadcast(broadcast);

        System.out.println("HERERER");

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

        // Start a new service
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommunicationHandler.ACTION_GATT_CONNECTED);
        filter.addAction(CommunicationHandler.ACTION_GATT_DISCONNECTED);
        filter.addAction(CommunicationHandler.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(CommunicationHandler.ACTION_DATA_AVAILABLE);
        filter.addAction(CommunicationHandler.ACTION_DEVICE_METADATA_COMPLETE);
        filter.addAction(CommunicationHandler.ACTION_DEVICE_METADATA_UPDATED);
        registerReceiver(mSensorStateListener, filter);


        //Display the list of devices
        //Intent i = new Intent(MainActivity.this, DeviceListActivity.class);
        //startActivityForResult(i, REQUEST_SELECT_SENSOR);
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
                setSensorState(SENSOR_STATE_CONNECTED);
                break;
            case CommunicationHandler.ACTION_GATT_DISCONNECTED:
                setSensorState(SENSOR_STATE_DISCONNECTED);
                break;
            case CommunicationHandler.ACTION_GATT_SERVICES_DISCOVERED:
                break;
            case CommunicationHandler.ACTION_DATA_AVAILABLE:
                String flowRespirationData = intent.getStringExtra(CommunicationHandler.EXTRA_DATA_FLOW_INFO_STRING);
                String heartRateData = intent.getStringExtra(CommunicationHandler.EXTRA_DATA_HEART_RATE);

                rp.pulse(rp.BREATH);
                Log.d(TAG, "handleBroadcastEvent: Respiration " + flowRespirationData);

                // Get the data from the sensor and send it the the demuxer.



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
        unregisterReceiver(mSensorStateListener);

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

                    mSensorTitle.setText(device.getName());
                    mSensorMac.setText(String.format("Mac: %s", device.getAddress()));

                    mComHandler.setSelectedFlowSensor(device);

                    connect();
                }
                break;
            default:
                Log.w(TAG, "onActivityResult: wrong request code " + requestCode);
                break;
        }
    }

    private void connect() {
        if (mComHandler != null) {
            setSensorState(SENSOR_STATE_CONNECTING);
            mComHandler.connect();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sensor_button:
                Log.d(TAG, "onClick: Sensor Button " + sensorState);
                handleSensorButton();
                break;
            case R.id.sensor_new_device:
                Log.d(TAG, "onClick: Connect to new sensor");
                Intent i = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(i, REQUEST_SELECT_SENSOR);
                break;
            default:
                break;
        }
    }

    /**
     * Handle the button state on click.
     *
     * SENSOR_STATE_CONNECTED       -> SENSOR_STATE_DISCONNECTED & change text to reconnect.
     * SENSOR_STATE_DISCONNECTED    -> SENSOR_STATE_CONNECTED & change text to disconnect.
     *
     * @states: connected | disconnected
     */
    private void handleSensorButton() {
        switch (sensorState) {
            case SENSOR_STATE_CONNECTED:
                setSensorState(SENSOR_STATE_DISCONNECTED);
                mComHandler.closeAndNotifyGattDisconnection();
                break;
            case SENSOR_STATE_DISCONNECTED:
                setSensorState(SENSOR_STATE_CONNECTING);
                connect();
                break;
        }
    }

    /**
     * Following method changes the state of the button and the corresponding text.
     *
     * @param state
     */
    private void setSensorState(int state) {
        switch (state) {
            case SENSOR_STATE_CONNECTED:
                mSensorState.setText("Connected");
                mButtonText.setText("DISCONNECT");
                mIndicator.getBackground().setColorFilter(getResources().getColor(R.color.colorConnected), PorterDuff.Mode.SRC_ATOP);
                mButton.setEnabled(true);
                mNewConnection.setVisibility(View.INVISIBLE);
                mButton.setCardBackgroundColor(getResources().getColor(R.color.colorButtonDisconnect));
                break;
            case SENSOR_STATE_DISCONNECTED:
                mSensorState.setText("Disconnected");
                mButtonText.setText("RECONNECT");
                mIndicator.getBackground().setColorFilter(getResources().getColor(R.color.colorDisconnected), PorterDuff.Mode.SRC_ATOP);
                mButton.setEnabled(true);
                mNewConnection.setVisibility(View.VISIBLE);
                mButton.setCardBackgroundColor(getResources().getColor(R.color.colorButtonReconnect));
                break;
            case SENSOR_STATE_CONNECTING:
                mSensorState.setText("Connecting...");
                mIndicator.getBackground().setColorFilter(getResources().getColor(R.color.colorConnecting), PorterDuff.Mode.SRC_ATOP);
                mButton.setCardBackgroundColor(getResources().getColor(R.color.colorButtonConnecting));
                mButton.setEnabled(false);
        }

        sensorState = state;
    }

}
