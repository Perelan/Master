package com.sensordroid.flow.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sensordroid.flow.WrapperService;
import com.sensordroid.flow.util.FlowTransfer;

import java.util.ArrayList;

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

        System.out.println(WrapperService.name);

        Log.d(TAG, "onReceive: " + WrapperService.name);
        extras.putStringArrayList("SUPPORTED_TYPES", getSupportedTypes(context));
        extras.putIntegerArrayList("FREQUENCIES", null);
        //extras.putIntegerArrayList("FREQUENCIES", getFrequencies(context));

        i.putExtras(extras);
        context.sendBroadcast(i);
    }

    private ArrayList<String> getSupportedTypes(Context context) {
        ArrayList<String> typesWithDescription = new ArrayList<>();

        int counter = 0;
        String type = FlowTransfer.getType(FlowTransfer.TYPE_RIP);
        String metric = FlowTransfer.getMetric(FlowTransfer.TYPE_RIP);
        String description = "SweetZpot: Flow";

        typesWithDescription.add(counter + "," + type + "," + metric + "," + description);

        return typesWithDescription;
    }
}
