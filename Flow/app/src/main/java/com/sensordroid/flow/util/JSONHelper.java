package com.sensordroid.flow.util;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sveinpg on 20.02.16.
 * Edited by torsteiw on 29.06.17.
 * Edited by jagatds on 05.11.18.
 */
public class JSONHelper {

    public static JSONObject construct(int id, int[] ids, Object[] values) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        dateFormat.setTimeZone(timeZone);
        String time = dateFormat.format(new Date());

        JSONObject res = new JSONObject();
        try {
            res.put("type", "data");
            res.put("id", id);
            res.put("time", time);

            JSONArray data = new JSONArray();
            for (int i = 0; i < ids.length; i++) {
                JSONObject element = new JSONObject();
                element.put("id", ids[i]);
                element.put("value", values[i]);
                data.put(element);
            }
            res.put("data", data);
        }catch(JSONException je){
            je.printStackTrace();
        }
        return res;
    }

    public static JSONObject metadata(String name, int id, int[] ids, String[] dataTypes,
                                      String[] metrics, String[] descriptions) {

        JSONObject res = new JSONObject();
        try{
            res.put("type", "meta");
            res.put("name", name);
            res.put("id", id);

            JSONArray channels = new JSONArray();
            for(int i = 0; i <ids.length; i++){
                JSONObject element = new JSONObject();
                element.put("id", ids[i]);
                element.put("type", dataTypes[i]);
                element.put("metric", metrics[i]);
                element.put("description", descriptions[i]);
                channels.put(element);
            }
            res.put("channels", channels);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void storeToDeviceList(Context context, BluetoothDevice device) {
        Log.d("FlowWrapper", "storeToDeviceList: " + device.getName());
        SharedPreferences pref = context.getSharedPreferences("com.sensordroid.flow", Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = pref.edit();

        edit.putString("device", device.toString());
        edit.apply();
    }

    public static String retrieveDeviceList(Context context) {
        SharedPreferences pref = context.getSharedPreferences("com.sensordroid.flow", Context.MODE_PRIVATE);

        String deviceString = pref.getString("device", null);

        if (deviceString == null) return null;

        return deviceString;
    }
}
