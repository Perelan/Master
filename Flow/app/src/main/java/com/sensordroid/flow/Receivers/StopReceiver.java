package com.sensordroid.flow.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.sensordroid.flow.WrapperService;

public class StopReceiver extends BroadcastReceiver {
    private static final String TAG = "StopReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Got intent. Checking list...");
        int driverId = -1;
        Bundle bundle = intent.getExtras();

        // Checking if driver is suppose to stop
        if (bundle!=null) {
            if (bundle.getString("WRAPPER_ID").equals(context.getPackageName())){
                driverId = bundle.getInt("WRAPPER_NUMBER");
            }
        }

        if(driverId != -1) {
            // Sending stop-intent to Service
            Intent service = new Intent(context, WrapperService.class);
            service.putExtra("ACTION", WrapperService.STOP_ACTION);
            service.putExtra("DRIVER_ID", driverId);
            service.putExtra("CHANNEL", bundle.getInt("WRAPPER_CHANNEL"));
            service.putExtra("FREQUENCY", bundle.getInt("WRAPPER_FREQUENCY"));

            Log.d(TAG, "onReceive: STOPPING");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service);
            } else {
                context.startService(service);
            }
        }
    }
}
