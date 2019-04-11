package com.sensordroid.flow;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sensordroid.flow.View.DeviceListActivity;
import com.sensordroid.flow.util.JSONHelper;
import com.sensordroid.ripple.RippleEffect;

import java.util.Locale;

import static com.sensordroid.flow.Bluetooth.BluetoothService.ACTION_DEVICE_METADATA_COMPLETE;
import static com.sensordroid.flow.Bluetooth.BluetoothService.ACTION_GATT_CONNECTED;
import static com.sensordroid.flow.Bluetooth.BluetoothService.ACTION_GATT_DISCONNECTED;

// bgcolor: #44628e
// iconcolor: #4572b6

public class MainActivity extends Activity {
    private static final String TAG ="FlowWrapper";

    private static final int REQUEST_SELECT_SENSOR = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    public static final String sharedKey = "com.sensordroid.flow";

    private TextView mSensorTitle;
    private TextView mSensorMac;
    private TextView mSensorBattery;
    private TextView mSensorFirmware;
    private TextView mSensorState;
    private TextView mIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorTitle    = findViewById(R.id.sensor_title);
        mSensorMac      = findViewById(R.id.sensor_mac);
        mSensorBattery  = findViewById(R.id.sensor_battery);
        mSensorFirmware = findViewById(R.id.sensor_firmware);
        mIndicator      = findViewById(R.id.sensor_state_indicator);
        mSensorState    = findViewById(R.id.sensor_state);

        TextView newConButton = findViewById(R.id.sensor_new_device);
        newConButton.setOnClickListener(l -> startDeviceListActivity());

        CardView removeButton = findViewById(R.id.sensor_button);
        removeButton.setOnClickListener(l -> removeCurrentDevice());

        sendVisibilityBroadcast();

        String address = JSONHelper.retrieveDeviceList(this);

        if (address == null) {
            startDeviceListActivity();
        } else {
            mSensorMac.setText(String.format(Locale.getDefault(), "Mac: %s", address));
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DEVICE_METADATA_COMPLETE);
        filter.addAction(ACTION_GATT_CONNECTED);
        filter.addAction(ACTION_GATT_DISCONNECTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(listener, filter);
    }

    public void startDeviceListActivity() {
        Intent i = new Intent(MainActivity.this, DeviceListActivity.class);
        startActivityForResult(i, REQUEST_SELECT_SENSOR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: request code " + requestCode);

        switch (requestCode) {
            case REQUEST_SELECT_SENSOR:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    JSONHelper.storeToDeviceList(this, device);

                    Log.d(TAG, "onActivityResult: " + device.getName());

                    mSensorTitle.setText(device.getName());
                    mSensorMac.setText(String.format(Locale.getDefault(), "Mac: %s", device.getAddress()));
                }
                break;
            default:
                Log.w(TAG, "onActivityResult: wrong request code " + requestCode);
                break;
        }
    }


    /**
     * Send a broadcast to the DataStreamDispatchingModule to notify the existence of the sensor
     * wrapper
     */
    private void sendVisibilityBroadcast() {
        Intent broadcast = new Intent();
        ComponentName name = new ComponentName("com.sensordroid", "com.sensordroid.SensorDiscovery");
        Bundle b = new Bundle();
        b.putString("name", "Flow");
        b.putString("packageName", "com.sensordroid.flow");
        broadcast.putExtras(b);
        broadcast.setComponent(name);
        sendBroadcast(broadcast);
    }

    public void removeCurrentDevice() {
        Log.d(TAG, "removeCurrentDevice: Todo");
    }

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent);

            if (intent == null) return;

            if (intent.getAction() != null && intent.getAction().equals(ACTION_DEVICE_METADATA_COMPLETE)) {
                Integer batteryLevel = intent.getIntExtra("batteryLevel", -1);
                String manufacturerName = intent.getStringExtra("manufacturerName");
                String firmwareRevision = intent.getStringExtra("firmwareRevision");

                mSensorBattery.setText(String.format(Locale.getDefault(), "%d %%", batteryLevel));
                mSensorFirmware.setText(String.format(Locale.getDefault(), "Firmware: %s", firmwareRevision));
            } else if (intent.getAction() != null && intent.getAction().equals(ACTION_GATT_CONNECTED)) {
                mIndicator.getBackground().setColorFilter(getResources().getColor(R.color.colorConnected), PorterDuff.Mode.SRC_ATOP);
                mSensorState.setText(getString(R.string.main_connected));
            } else if (intent.getAction() != null && intent.getAction().equals(ACTION_GATT_DISCONNECTED)) {
                mIndicator.getBackground().setColorFilter(getResources().getColor(R.color.colorDisconnected), PorterDuff.Mode.SRC_ATOP);
                mSensorState.setText(getString(R.string.main_disconnected));
            } else {
                Log.d(TAG, "onReceive: Unknown action!");
            }
        }
    };
}
