package com.sensordroid.bitalino.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.sensordroid.bitalino.SettingsActivity;
import com.sensordroid.bitalino.WrapperService;
import com.sensordroid.bitalino.util.BitalinoTransfer;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class RespondReceiver extends BroadcastReceiver {
    private static final String TAG = "RespondReceiver";

    @Override
    public void onReceive(Context context, Intent intent1) {
        Log.d(TAG, "Got Intent, responding...");

        Intent intent = new Intent("com.sensordroid.ADD_DRIVER");
        Bundle extras = new Bundle();

        extras.putString("ID", context.getPackageName());
        extras.putString("NAME", WrapperService.name);
        extras.putStringArrayList("SUPPORTED_TYPES", getSupportedTypes(context));
        extras.putIntegerArrayList("FREQUENCIES", getFrequencies(context));

        intent.putExtras(extras);
        context.sendBroadcast(intent);
    }

    private ArrayList<String> getSupportedTypes(Context context){
        ArrayList<String> typesWithDescription = new ArrayList<String>();

        // Get types of channels from shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsActivity.sharedKey, Context.MODE_PRIVATE);
        String savedString = sharedPreferences.getString(SettingsActivity.channelKey, "0,0,0,0,0,0");
        StringTokenizer st = new StringTokenizer(savedString, ",");

        // Create list of the data types
        int active_channels = 0;
        int[] types;
        types = new int[SettingsActivity.NUM_CHANNELS];
        for (int i = 0; i < types.length; i++) {
            types[i] = Integer.parseInt(st.nextToken());
            if (types[i] != 0) {
                active_channels++;
            }
        }

        int counter = 0;
        for(int i = 0; i < types.length; i++) {
            if (types[i] == BitalinoTransfer.TYPE_OFF){
                continue;
            }
            String type = BitalinoTransfer.getType(types[i]);
            String metric = BitalinoTransfer.getMetric(types[i]);
            String description = sharedPreferences.getString(SettingsActivity.descriptionKeys[i], " ");
            typesWithDescription.add(counter+","+type+","+metric+","+description);
            counter++;
        }
        return typesWithDescription;
    }

    private ArrayList<Integer> getFrequencies(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsActivity.sharedKey, Context.MODE_PRIVATE);
        String s = sharedPreferences.getString(SettingsActivity.frequenciesKey, "");
        ArrayList<Integer> a = new ArrayList<Integer>();
        String[] t = s.split(",");
        for(String string : t){
            a.add(Integer.parseInt(string));
        }
        return a;
    }
}
