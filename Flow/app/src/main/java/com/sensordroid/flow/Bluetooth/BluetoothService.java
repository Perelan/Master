package com.sensordroid.flow.Bluetooth;

public interface BluetoothService {
    String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";

    String EXTRA_DATA_HEART_RATE = "EXTRA_DATA_HEART_RATE";
    String EXTRA_DATA_RR_INTERVAL = "EXTRA_DATA_RR_INTERVAL";

    String EXTRA_DATA_FLOW_TIMESTAMP = "EXTRA_DATA_FLOW_INFO_TIMESTAMP";
    String EXTRA_DATA_FLOW_INFO_STRING = "EXTRA_DATA_FLOW_INFO_STRING";
    String EXTRA_DATA_MISC = "EXTRA_DATA_MISC";

    String ACTION_DEVICE_METADATA_UPDATED = "ACTION_DEVICE_METADATA_UPDATED";
    String ACTION_DEVICE_METADATA_COMPLETE = "ACTION_DEVICE_METADATA_COMPLETE";


    class SensorMetadata {
        public Integer batteryLevel;
        public String manufacturerName;
        public String firmwareRevision;

        public boolean firmwareRevisionSupported = false;

        public boolean flowEnabled;
        public boolean flowSupported;

        public boolean heartRateEnabled;
        public boolean heartRateSupposted;

        public boolean batteryEnabled;
        public boolean batterySupported;

        public boolean completeSent;

        public boolean isComplete() {
            if (firmwareRevisionSupported)
                return batteryLevel != null && manufacturerName != null && firmwareRevision != null;
            else
                return batteryLevel != null && manufacturerName != null;
        }

        public void reset() {
            flowEnabled = false;
            heartRateEnabled = false;
            batteryEnabled = false;

            batteryLevel = null;
            firmwareRevision = null;
            manufacturerName = null;
        }
    }
}
