package com.sensordroid.flow.Receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sensordroid.flow.WrapperService;


public class RespondReceiver extends BroadcastReceiver {
    private static final String TAG = "RespondReceiver";
    private static final String REGISTER_ACTION = "com.sensordroid.ADD_DRIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Got Intent, responding...");

        Intent i = new Intent(REGISTER_ACTION);

        Bundle extras = new Bundle();

        extras.putString("ID", context.getPackageName());
        extras.putString("NAME", WrapperService.name);
        //extras.putStringArrayList("SUPPORTED_TYPES", getSupportedTypes(context));
        //extras.putIntegerArrayList("FREQUENCIES", getFrequencies(context));

        i.putExtras(extras);
        context.sendBroadcast(i);
    }

}
