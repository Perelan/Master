package com.sensordroid.flow.Handlers;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sensordroid.MainServiceConnection;
import com.sensordroid.flow.util.GattAttributes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by jagatds on 11.10.2018
 */
public class BluetoothHandler extends Service implements IBluetoothLEService {
    private final static String TAG = "BluetoothHandler";

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothManager mBluetoothManager;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice selectedFlowSensor;

    public SensorMetadata mSensorMetadata = new SensorMetadata();

    private IBinder mBinder = new LocalBinder();

    private BluetoothCallback callback;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            mBluetoothGatt = gatt;

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange: Connected to GATT server, starting service discover");

                mBluetoothGatt.discoverServices();

                sendMetadataUpdated();
                callback.onDataReceived(new Intent(ACTION_GATT_CONNECTED));
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange: Disconnected from GATT server");
                closeAndNotifyGattDisconnection();
            } else {
                Log.e(TAG, "onConnectionStateChange: Bluetooth profile not handled");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onServicesDiscovered: Status = " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothGatt = gatt;
                setRespirationFlowNotification(true);       // For testing purposes.
                fetchSensorMetaData();
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                decodeCharacteristic(characteristic, false);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            decodeCharacteristic(characteristic, false);
        }
    };

    private void fetchSensorMetaData() {
        if (hasCharacteristic(GattAttributes.DEVICE_INFORMATION_SERVICE, GattAttributes.FIRMWARE_REVISION))
            mSensorMetadata.firmwareRevisionSupported = true;
        if (mSensorMetadata.manufacturerName == null) {
            readCharacteristic(GattAttributes.DEVICE_INFORMATION_SERVICE, GattAttributes.MANUFACTURER_NAME);
        } else if (mSensorMetadata.firmwareRevision == null && mSensorMetadata.firmwareRevisionSupported) {
            readCharacteristic(GattAttributes.DEVICE_INFORMATION_SERVICE, GattAttributes.FIRMWARE_REVISION);
        } else if (mSensorMetadata.batteryLevel == null) {
            readCharacteristic(GattAttributes.BATTERY_SERVICE, GattAttributes.BATTERY_LEVEL);
        }
    }

    public void setCallback(BluetoothCallback callback) {
        this.callback = callback;
    }

    public class LocalBinder extends Binder {
        public BluetoothHandler getService() {
            return BluetoothHandler.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public boolean init() {
        Log.d(TAG, "initialize");

        if (mBluetoothManager == null) {
            Log.d(TAG, "init: Getting Bluetooth Manager");
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            if (mBluetoothManager == null) {
                Log.e(TAG, "init: Error on initializing BluetoothManager");
                return false;
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Log.e(TAG, "init: Error on BluetoothAdapter");
            return false;
        }

        Log.d(TAG, "init: Initalized");

        return true;
    }

    public boolean connect() {
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "connect: BluetoothAdapter not initalized");
            return false;
        }

        if (mBluetoothGatt != null) {
            if (mBluetoothGatt.getDevice().equals(selectedFlowSensor)) {
                Log.d(TAG, "connect: Already connected to a device");

                callback.onDataReceived(new Intent(ACTION_GATT_CONNECTED));
                return true;
            }

            closeAndNotifyGattDisconnection();
        }

        Log.i(TAG, "connect: Connecting to a device: " + selectedFlowSensor.getName());

        mBluetoothGatt = selectedFlowSensor.connectGatt(this, false, mGattCallback);

        if (mBluetoothGatt == null) {
            Log.e(TAG, "connect: Device not connected");
            return false;
        }

        return true;
    }

    public void closeAndNotifyGattDisconnection() {
        if (mBluetoothGatt != null) {
            Log.w(TAG, "closeAndNotifyGattDisconnection: Closing GATT Connection");
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }

        callback.onDataReceived(new Intent(ACTION_GATT_DISCONNECTED));
    }

    public void setSelectedFlowSensor(BluetoothDevice device) {
        selectedFlowSensor = device;
    }

    public int getConnectionState(BluetoothDevice device) {
        if (mBluetoothManager == null) return BluetoothGatt.GATT_FAILURE;

        return mBluetoothManager.getConnectionState(device, BluetoothGatt.GATT);
    }

    public BluetoothDevice getConnectedSensor() {
        return mBluetoothGatt == null ? null : mBluetoothGatt.getDevice();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void readCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "readCharacteristic: No device connected");
            return;
        }

        BluetoothGattService mDI = mBluetoothGatt.getService(serviceUUID);
        if (mDI == null) {
            Log.w(TAG, "readCharacteristic: Service not found: " + serviceUUID);
            return;
        }
        BluetoothGattCharacteristic characteristic = mDI.getCharacteristic(characteristicUUID);
        if (characteristic == null) {
            Log.w(TAG, "readCharacteristic(): Characteristic not found: " + characteristicUUID);
            return;
        }
        boolean ok = mBluetoothGatt.readCharacteristic(characteristic);
        if (!ok) {
            Log.w(TAG, "readCharacteristic() not OK: " + characteristicUUID);
        }
    }

    private void decodeCharacteristic(final BluetoothGattCharacteristic characteristic, boolean store) {
        if (GattAttributes.HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            Log.i(TAG, "decodeCharacteristic: >>> Heart Rate Measurement");
            heartRateDataReceived(characteristic, false);
        } else if (GattAttributes.MANUFACTURER_NAME.equals(characteristic.getUuid())) {
            Log.i(TAG, "decodeCharacteristic:>>> Manufacturer Name");
            manufacturerNameReceived(characteristic);
        } else if (GattAttributes.FIRMWARE_REVISION.equals(characteristic.getUuid())) {
            Log.i(TAG, "decodeCharacteristic:>>> Firmware Revision");
            firmwareRevisionReceived(characteristic);
        } else if (GattAttributes.BATTERY_LEVEL.equals(characteristic.getUuid())) {
            Log.i(TAG, "decodeCharacteristic:>>> Battery Level");
            batteryLevelReceived(characteristic);
            setBatteryNotification(true);
        } else if (GattAttributes.FLOW_MEASUREMENT.equals(characteristic.getUuid())) {
            Log.i(TAG, "decodeCharacteristic:>>> Flow Measurement");
            flowDataReceived(characteristic, false);
        } else if (GattAttributes.RAW_DATA_MEASUREMENT.equals(characteristic.getUuid())) {
            Log.i(TAG, "decodeCharacteristic:>>> Raw data");
        } else if (GattAttributes.ANGLES_MEASUREMENT.equals(characteristic.getUuid())) {
            Log.i(TAG, "decodeCharacteristic:>>> Angles");
        } else {
            Log.i(TAG, "decodeCharacteristic:>>> Extra Data");
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));

                callback.onDataReceived(new Intent(ACTION_DATA_AVAILABLE)
                        .putExtra(EXTRA_DATA_MISC, new String(data) + "\n" + stringBuilder.toString()));
            }
        }

        fetchSensorMetaData();
    }

    private void manufacturerNameReceived(BluetoothGattCharacteristic characteristic) {
        byte[] manufacturerName = characteristic.getValue();

        if (manufacturerName == null) {
            return;
        }

        mSensorMetadata.manufacturerName = new String(manufacturerName, StandardCharsets.UTF_8);
        sendMetadataUpdated();
    }

    private void firmwareRevisionReceived(BluetoothGattCharacteristic characteristic) {
        final byte[] bytes = characteristic.getValue();
        if (bytes != null) {
            try {
                mSensorMetadata.firmwareRevision = new String(bytes, StandardCharsets.UTF_8);
                sendMetadataUpdated();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void batteryLevelReceived(BluetoothGattCharacteristic characteristic) {
        try {
            mSensorMetadata.batteryLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            sendMetadataUpdated();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void flowDataReceived(BluetoothGattCharacteristic characteristic, boolean store) {
        byte[] values = characteristic.getValue();
        if (values.length == 0)
            Log.w(TAG, "flowDataReceived - No Respiration Data");
        else {
            ShortBuffer shortBuffer = ByteBuffer.wrap(values).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            List<Integer> intValues = new ArrayList<>();
            for (int i = 0; i < shortBuffer.capacity(); i++) {
                intValues.add((int) shortBuffer.get(i));
            }

            long ts = intValues.get(7) & 0x0000ffff;
            String timeStamp = String.format("%d", (long) ts);
            String deltaTime = "100";
            String minValueTimeStamp = String.format("%d", intValues.get(8));
            String maxValueTimeStamp = String.format("%d", intValues.get(9));

            String samples = String.format("%d,%d,%d,%d,%d,%d,%d",
                    intValues.get(0), intValues.get(1), intValues.get(2),
                    intValues.get(3), intValues.get(4), intValues.get(5), intValues.get(6));

            Intent intent = new Intent(ACTION_DATA_AVAILABLE);
            intent.putExtra(EXTRA_DATA_FLOW_TIMESTAMP, ts);
            intent.putExtra(EXTRA_DATA_FLOW_INFO_STRING, String.format("Time=%sms, deltaT=%s, data=%s", timeStamp, deltaTime, samples));
            if (store) {
                //XmlSampleStorage.HandleNewSample("FLOW", samples, timeStamp, deltaTime, minValueTimeStamp, maxValueTimeStamp);
            }
            int sum = 0;
            int max = intValues.get(1);
            int min = max;
            for (int i = 0; i < 7; i++) {
                int sample = intValues.get(i);
                sum += sample;
                if (sample > max)
                    max = sample;

                if (sample < min)
                    min = sample;
            }

            int avg = sum / 7;

            int[] avgMinMaxList = new int[]{avg, min, max};

            System.out.println(Arrays.toString(avgMinMaxList));

            //intent.putExtra(EXTRA_DATA_FLOW_AVG_MIN_MAX, avgMinMaxList);
            callback.onDataReceived(intent);

            // If it has gone less than a second, don't broadcast
            // if (System.currentTimeMillis() - mLastRespirationTimeStamp > 1000) {
            //     sendBroadcast(intent);
            //     mLastRespirationTimeStamp = System.currentTimeMillis();
            // }
        }
    }

    private void heartRateDataReceived(BluetoothGattCharacteristic characteristic, boolean store) {
        int flags = characteristic.getProperties();
        int format = (flags & 0x01) != 0 ? BluetoothGattCharacteristic.FORMAT_UINT16 : BluetoothGattCharacteristic.FORMAT_UINT8;
        final int heartRate = characteristic.getIntValue(format, 1);

        long rrInterval=0;
        if ((flags & 0x10) != 0) {  // Contains RR interval?
            Integer rrIntervalInt = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 2);
            if (rrIntervalInt != null)
                rrInterval = (long) rrIntervalInt;
        }
        //Log.d(TAG, String.format("Received heart rate: %d", heartRate));
        if (store) {
            String stringToStore = String.format("BPM=\"%d\"", heartRate);
            //XmlSampleStorage.HandleNewSample("HR",stringToStore );
        }

        //sendMessageToWearable(heartRate);
        callback.onDataReceived(new Intent(ACTION_DATA_AVAILABLE)
                .putExtra(EXTRA_DATA_HEART_RATE, String.valueOf(heartRate))
                .putExtra(EXTRA_DATA_RR_INTERVAL, String.valueOf(rrInterval)));
    }

    private void sendMetadataUpdated() {
        callback.onDataReceived(new Intent(ACTION_DEVICE_METADATA_UPDATED));

        if (mSensorMetadata.isComplete() && !mSensorMetadata.completeSent) {
            // We update them as the last field to make sure that all other communication is done
            // before letting the user start to use the services.
            mSensorMetadata.batterySupported =
                    hasCharacteristic(GattAttributes.BATTERY_SERVICE, GattAttributes.BATTERY_LEVEL);
            mSensorMetadata.flowSupported =
                    hasCharacteristic(GattAttributes.VENTILATION_SERVICE, GattAttributes.FLOW_MEASUREMENT);
            mSensorMetadata.heartRateSupposted =
                    hasCharacteristic(GattAttributes.HEART_RATE_SERVICE, GattAttributes.HEART_RATE_MEASUREMENT);

            callback.onDataReceived(new Intent(ACTION_DEVICE_METADATA_COMPLETE));  // Update GUI
            mSensorMetadata.completeSent = true;
        }
    }

    // Retrieves a list of supported GATT services on the connected device. This should be
    // invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
    public boolean hasCharacteristic(UUID serviceUuid, UUID characteristicUuid) {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "hasCharacteristic: No device connected");
            return false;
        }

        for (BluetoothGattService s : mBluetoothGatt.getServices()) {
            if (s.getUuid().equals(serviceUuid)) {
                return s.getCharacteristic(characteristicUuid) != null;
            }
        }

        return false;
    }

    public void setHeartRateNotification(boolean enable) {
        setNotification(GattAttributes.HEART_RATE_SERVICE, GattAttributes.HEART_RATE_MEASUREMENT, enable);
        mSensorMetadata.heartRateEnabled = enable;
    }

    public void setRespirationFlowNotification(boolean enable) {
        setNotification(GattAttributes.VENTILATION_SERVICE, GattAttributes.FLOW_MEASUREMENT, enable);
        mSensorMetadata.flowEnabled = enable;
    }

    public void setBatteryNotification(boolean enable) {
        setNotification(GattAttributes.BATTERY_SERVICE, GattAttributes.BATTERY_LEVEL, enable);
        mSensorMetadata.batteryEnabled = enable;
    }

    private void setNotification(UUID service, UUID characteristic, boolean enable) {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "setNotification(): No device connected");
            return;
        }
        BluetoothGattService gattServiceService = mBluetoothGatt.getService(service);
        if (gattServiceService == null) {
            Log.w(TAG, "setNotification(): No gatt service");
            return;
        }
        BluetoothGattCharacteristic gattCharacteristic = gattServiceService.getCharacteristic(characteristic);
        if (gattCharacteristic == null) {
            Log.w(TAG, "setNotification(): No characteristic=" + characteristic + "in Service: " + service);
            return;
        }

        if (!mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true))
            Log.w(TAG, "setNotification(): mBluetoothGatt.setCharacteristicNotification: FAILED");

        BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
        if (descriptor == null) {
            Log.w(TAG, "setNotification(): no ClientCharacteristicConfig descriptor in characteristic=" + characteristic);
            return;
        }

        descriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

        if (!mBluetoothGatt.writeDescriptor(descriptor)) {
            Log.w(TAG, "EnableNotification(): mBluetoothGatt.writeDescriptor() failed");
        }
    }
}
