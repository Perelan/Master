package com.sensordroid.flow;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {
    public static final String sharedKey = "com.sensordroid.flow";
    public static final String channelKey = sharedKey + ".channels";
    public static final String macKey = sharedKey + ".mac";
    public static final String frequencyKey = sharedKey + ".frequency";
    public static final String frequenciesKey = sharedKey + ".frequencies";
    public static final String[] descriptionKeys = new String[]{sharedKey + ".d1", sharedKey + ".d2",
            sharedKey + ".d3", sharedKey + ".d4", sharedKey + ".d5", sharedKey + ".d6"};

    private String flow = "E7:B4:33:F2:ED:52";

    private final UUID BATTERY_LIFE = UUID.fromString("0000180F-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences preferences = getSharedPreferences(sharedKey, MODE_PRIVATE);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                System.out.println(String.format("> Saved devices %s (%s)", deviceName, deviceHardwareAddress));
            }
        }


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        mBluetoothAdapter.startDiscovery();
        //collectDataFromDevice(flow);

    }

    BluetoothDevice flowDevice = null;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                System.out.println(String.format("> Discovered device %s (%s) - uuids: %s", deviceName, deviceHardwareAddress, Arrays.toString(device.getUuids())));

               if (deviceName != null && deviceName.equals("OarZpot")) {
                   mBluetoothAdapter.cancelDiscovery();
                   flowDevice = device;

                   final BluetoothDevice dev = mBluetoothAdapter.getRemoteDevice(deviceHardwareAddress);

                   System.out.println("Hha " + dev);
               }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                System.out.println("Finished ");

                if (flowDevice != null) {
                    boolean result = flowDevice.fetchUuidsWithSdp();
                    System.out.println("Result " + result);
                }
            }
            else if (BluetoothDevice.ACTION_UUID.equals(action)) {
                System.out.println("Action UUID");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);

                System.out.println(uuidExtra);

                System.out.println(String.format("> Discovered device %s (%s) - uuids: %s", device.getName(), device.getAddress(), Arrays.toString(uuidExtra)));
            }
        }
    };

    private void collectDataFromDevice(String address) {
        System.out.println("Inside");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }
}
