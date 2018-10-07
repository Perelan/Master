package com.sensordroid.flow.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sensordroid.flow.WrapperService;

public class StartReceiver extends BroadcastReceiver {
    private static final String TAG = "StartReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Got intent. Checking list...");

        // Checking if driver is suppose to start
        int driverId = -1;
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            if (bundle.getString("WRAPPER_ID").equals(context.getPackageName())) {
                driverId = bundle.getInt("WRAPPER_NUMBER");
            }
        }

        if (driverId != -1) {
            String serv_action = bundle.getString("SERVICE_ACTION");
            String serv_name = bundle.getString("SERVICE_NAME");
            String serv_pack = bundle.getString("SERVICE_PACKAGE");
            int channel_num = bundle.getInt("WRAPPER_CHANNEL");
            int serv_freq = bundle.getInt("WRAPPER_FREQUENCY");

            Intent service = new Intent(context, WrapperService.class);
            service.putExtra("ACTION", WrapperService.START_ACTION);
            service.putExtra("DRIVER_ID", driverId);
            service.putExtra("CHANNEL", channel_num);
            service.putExtra("FREQUENCY", serv_freq);
            service.putExtra("SERVICE_ACTION", serv_action);
            service.putExtra("SERVICE_NAME", serv_name);
            service.putExtra("SERVICE_PACKAGE", serv_pack);
            context.startService(service);
        }
    }
}
