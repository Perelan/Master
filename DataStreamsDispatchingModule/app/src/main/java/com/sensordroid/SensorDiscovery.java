package com.sensordroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class SensorDiscovery extends BroadcastReceiver {
    private static final String TAG = "DSDService-Sensor";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Here");

        Bundle b = intent.getExtras();
        Log.d(TAG, "onReceive: " + b.getString("name"));

        String name = b.getString("name");
        String packageName = b.getString("packageName");

        SharedPreferences sp = context.getSharedPreferences("SENSORS_FILES", Context.MODE_PRIVATE);

        String data = sp.getString("data", null);

        ArrayList<Sensor> sensors;

        if (data != null) {
            Log.d(TAG, "onReceive: fetching array");
            sensors = new Gson().fromJson(data, new TypeToken<ArrayList<Sensor>>(){}.getType());
        } else {
            Log.d(TAG, "onReceive: creating array");
            sensors = new ArrayList<>();
        }

        for (Sensor s : sensors) {
            if (s.getPackageName().equals(packageName)) {
                Log.d(TAG, "onReceive: Already exists");
                return;
            }
        }

        Sensor s = new Sensor(name, packageName);
        sensors.add(s);

        SharedPreferences.Editor edit = sp.edit();
        edit.putString("data", new Gson().toJson(sensors));
        edit.apply();

        Log.d(TAG, "onReceive: Storing sensor");
    }
}
