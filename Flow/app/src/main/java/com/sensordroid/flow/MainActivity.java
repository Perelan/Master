package com.sensordroid.flow;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sensordroid.flow.Bluetooth.BluetoothHandler;
import com.sensordroid.flow.util.JSONHelper;
import com.sensordroid.ripple.RippleEffect;

import java.util.ArrayList;

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

    private RippleEffect rp;

    private TextView mSensorTitle, mSensorMac,
            mSensorBattery, mSensorFirmware, mSensorState, mNewConnection, mIndicator;

    private CardView mButton;

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

        ArrayList<BluetoothDevice> devices = JSONHelper.retrieveDeviceList(this);

        if (devices == null) {
            Log.d(TAG, "onCreate: HEI");

            startDeviceListActivity();
        }
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

                    System.out.println(" >>> " + device.getName());

                    mSensorTitle.setText(device.getName());
                    mSensorMac.setText(String.format("Mac: %s", device.getAddress()));
                }
                break;
            default:
                Log.w(TAG, "onActivityResult: wrong request code " + requestCode);
                break;
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
                startDeviceListActivity();
                break;
            default:
                break;
        }
    }

    public void removeCurrentDevice() {
        Log.d(TAG, "removeCurrentDevice: ToDo");
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
