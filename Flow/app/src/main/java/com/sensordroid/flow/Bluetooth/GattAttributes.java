package com.sensordroid.flow.Bluetooth;

import java.util.HashMap;
import java.util.UUID;

public class GattAttributes {
    public static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static UUID MANUFACTURER_NAME = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
    public static UUID FIRMWARE_REVISION = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public static UUID DEVICE_NAME = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    public static UUID APPEARANCE = UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb");
    public static UUID PERIPHERAL_PRIVACY_FLAG = UUID.fromString("00002a02-0000-1000-8000-00805f9b34fb");
    public static UUID RECONNECTION_ADDRESS = UUID.fromString("00002a03-0000-1000-8000-00805f9b34fb");
    public static UUID PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = UUID.fromString("00002a04-0000-1000-8000-00805f9b34fb");
    public static UUID SERVICE_CHANGED = UUID.fromString("00002a05-0000-1000-8000-00805f9b34fb");

    public static UUID SERIAL_NUMBER = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");

    public static UUID HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    public static UUID HEART_RATE_CONTROL_POINT = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");
    public static UUID BODY_SENSOR_LOCATION = UUID.fromString("00002a38-0000-1000-8000-00805f9b34fb");

    public static UUID BATTERY_LEVEL = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");

    public static UUID ANGLES_MEASUREMENT = UUID.fromString("00003f10-0000-1000-8000-00805f9b34fb");
    public static UUID FLOW_MEASUREMENT = UUID.fromString("0000ffb3-0000-1000-8000-00805f9b34fb");
    public static UUID RAW_DATA_MEASUREMENT = UUID.fromString("00000200-0000-1000-8000-00805f9b34fb");

    // Services
    public static UUID BATTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static UUID DEVICE_INFORMATION_SERVICE = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static UUID GENERIC_ACCESS_SERVICE = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    public static UUID GENERIC_ATTRIBUTE_SERVICE = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    public static UUID HEART_RATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    public static UUID RAW_DATA_SERVICE = UUID.fromString("0000ffb2-0000-1000-8000-00805f9b34fb");
    public static UUID ANGLE_SERVICE = UUID.fromString("0000ffaa-0000-1000-8000-00805f9b34fb");
    public static UUID VENTILATION_SERVICE = UUID.fromString("0000ffb0-0000-1000-8000-00805f9b34fb");
}
