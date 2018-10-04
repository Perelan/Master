package com.sensordroid.bitalino.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sensordroid.bitalino.WrapperService;

public class StopReceiver extends BroadcastReceiver {
    private static final String TAG = "StopReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Got stop intent");
        int driverId = -1;
        Bundle b = intent.getExtras();

        if(b!=null)
        {
            if(b.getString("WRAPPER_ID").equals(context.getPackageName())){
                driverId = b.getInt("WRAPPER_NUMBER");
            }
        }

        if(driverId != -1) {
            // Sending stop-intent to Service
            Intent service = new Intent(context, WrapperService.class);
            service.putExtra("ACTION", WrapperService.STOP_ACTION);
            service.putExtra("DRIVER_ID", driverId);
            service.putExtra("CHANNEL", b.getInt("WRAPPER_CHANNEL"));
            service.putExtra("FREQUENCY", b.getInt("WRAPPER_FREQUENCY"));
            context.startService(service);
        }
    }
}
