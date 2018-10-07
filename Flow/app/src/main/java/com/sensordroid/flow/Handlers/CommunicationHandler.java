package com.sensordroid.flow.Handlers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import android.os.Handler;
import android.widget.Toast;

import com.sensordroid.MainServiceConnection;
import com.sensordroid.flow.WrapperService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sveinpg on 27.01.16.
 */
public class CommunicationHandler implements Runnable {
    private final String TAG = "CommuncationHandler";

    private static MainServiceConnection binder;
    private static int driverId;
    private static String driverName;
    private static int sampling_freq;
    private static boolean interrupted;
    private Context context;

    private BluetoothSocket mSocket;

    // private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CommunicationHandler(MainServiceConnection binder, String name, int id, Context context) {
        this.binder = binder;
        this.driverName = name;
        this.driverId = id;
        this.interrupted = false;
        this.context = context;

        this.mSocket = null;
        sampling_freq = WrapperService.current_frequency;
    }

    @Override
    public void run() {

        final int SLEEP_TIME = 1000;

        // While loop to reconnect in case of disconnection
        while (!interrupted){
            // Check if interrupted
            if (Thread.currentThread().isInterrupted()){
                interrupted = true;
                break;
            }

            if (connect()) {
                Log.d(TAG, "connection successfull");

                collectData();
            }

            //collectData();
            //resetConnection();
        }
    }

    private boolean connect() {

        // Get Mac address


        final BluetoothAdapter blueAdapt = BluetoothAdapter.getDefaultAdapter();
        final BluetoothDevice dev = blueAdapt.getRemoteDevice("");

        if (!blueAdapt.isEnabled()) {
            blueAdapt.enable();

            Handler h = new Handler(context.getMainLooper());

            h.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Turning on bluetooth", Toast.LENGTH_LONG).show();
                }
            });

            return false;
        }



        return false;
    }

    private void collectData() {
        /*
            TODO 6: Code to collect data,
                - Collect data until the thread is interrupted.
         */
        while(!interrupted){
            if (Thread.currentThread().isInterrupted()) {
                interrupted = true;
                break;
            }
            // TODO 7: Change type of collectedData to match your data format
            Object[] collectedData = new Object[]{};
            int[] channelsUsed = new int[]{};

            // Pass the collected data to the working threads for computations and sending.
            executor.submit(new DataHandler(binder, driverId, collectedData, channelsUsed));
        }
    }

    private void resetConnection() {
        /*
            TODO 8: Code to reset connection
                - For instance close sockets, files etc.
         */

    }
}
