package com.sensordroid.flow;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sensordroid.flow.Handlers.BluetoothHandler;
import com.sensordroid.flow.util.JSONHelper;
import com.sensordroid.ripple.RippleEffect;

import java.util.ArrayList;
import java.util.Locale;

// bgcolor: #44628e
// iconcolor: #4572b6

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG ="FlowWrapper";

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
    private BluetoothHandler mComHandler = null;

    private boolean mScanning;

    private RippleEffect rp;
    private TextView mSensorTitle, mSensorMac, mSensorBattery, mSensorFirmware, mSensorState, mNewConnection, mIndicator;

    private CardView mButton;
    private TextView mButtonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                Log.i(TAG, "onServiceConnected");

                mComHandler = ((BluetoothHandler.LocalBinder) service).getService();


                if (!mComHandler.init()) {
                    Log.e(TAG, "onServiceConnected: Unable to initialize Bluetooth");
                    finish();
                    return;
                }


                ArrayList<BluetoothDevice> devices = JSONHelper.retrieveDeviceList(getApplicationContext());

                if (devices == null) return;

                BluetoothDevice device = devices.get(0);
                mComHandler.setSelectedFlowSensor(device);


                System.out.println("Name " + BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress()).getName());
                System.out.println("Address " + device.getAddress());

                //connect();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mComHandler = null;
            }
        };

        bindService(new Intent(this, BluetoothHandler.class), mServiceConnection, BIND_AUTO_CREATE);
        // Start a new service

        ArrayList<BluetoothDevice> devices = JSONHelper.retrieveDeviceList(this);

        if (devices == null) {
            Log.d(TAG, "onCreate: HEI");

            Intent i = new Intent(MainActivity.this, DeviceListActivity.class);
            startActivityForResult(i, REQUEST_SELECT_SENSOR);
        }
    }



    @Override
    protected void onDestroy() {

        if (mServiceConnection != null) {
            unbindService(mServiceConnection);

            if (isFinishing()) {
                stopService(new Intent(this, BluetoothHandler.class));
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

                    JSONHelper.storeToDeviceList(this, device);

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
                //handleSensorButton();
                removeCurrentDevice();
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

    public void removeCurrentDevice() {
        Log.d(TAG, "removeCurrentDevice: ToDo");
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
                //mButtonText.setText("DISCONNECT");
                mIndicator.getBackground().setColorFilter(getResources().getColor(R.color.colorConnected), PorterDuff.Mode.SRC_ATOP);
                mButton.setEnabled(true);
                mNewConnection.setVisibility(View.INVISIBLE);
                mButton.setCardBackgroundColor(getResources().getColor(R.color.colorButtonDisconnect));
                break;
            case SENSOR_STATE_DISCONNECTED:
                mSensorState.setText("Disconnected");
                //mButtonText.setText("RECONNECT");
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
