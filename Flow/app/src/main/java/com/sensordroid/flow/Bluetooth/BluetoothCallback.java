package com.sensordroid.flow.Bluetooth;

import android.content.Intent;

public interface BluetoothCallback {
    void onDataReceived(Intent data);
}
