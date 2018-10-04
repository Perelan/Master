package com.sensordroid.bitalino.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sensordroid.bitalino.WrapperService;

public class StartReceiver extends BroadcastReceiver {
            private static final String TAG = "StartReceiver";

            @Override

            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Got intent. Connecting to service...");

                int driverId = -1;
                Bundle b = intent.getExtras();

                if(b!=null)
                {
                    if(b.getString("WRAPPER_ID").equals(context.getPackageName())){
                        driverId = b.getInt("WRAPPER_NUMBER");
                    }
                }

                if(driverId != -1) {
                    String serv_action = b.getString("SERVICE_ACTION");
                    String serv_name = b.getString("SERVICE_NAME");
                    String serv_pack = b.getString("SERVICE_PACKAGE");
                    int channel_num = b.getInt("WRAPPER_CHANNEL");
                    int serv_freq = b.getInt("WRAPPER_FREQUENCY");

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
