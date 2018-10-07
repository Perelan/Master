package com.sensordroid.flow.Handlers;

import android.os.RemoteException;

import com.sensordroid.MainServiceConnection;
import com.sensordroid.flow.util.JSONHelper;

import org.json.JSONObject;

/**
 * Created by sveinpg on 27.01.16.
 */
public class DataHandler implements Runnable {
    private static MainServiceConnection binder;
    private static Object[] data;
    private static int id;
    private static int[] channels;

    // TODO 8: Change type of "data" to match your format
    public DataHandler(MainServiceConnection binder, int id, Object[] data, int[] channels) {
        this.binder = binder;
        this.id = id;
        this.data = data;
        this.channels = channels;
    }

    @Override
    /*
        Create JSON-object and send it using the binder object.
     */
    public void run() {
        /*
            TODO 9: Rewrite the collected data from the data format in to an array
                  containing the sampled values.
         */
        try {
            JSONObject res = JSONHelper.construct(id, this.channels, this.data);
            binder.putJson(res.toString());
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }
}
