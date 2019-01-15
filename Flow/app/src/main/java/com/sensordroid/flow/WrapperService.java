package com.sensordroid.flow;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sensordroid.MainServiceConnection;
import com.sensordroid.flow.Handlers.CommunicationHandler;
import com.sensordroid.flow.util.JSONHelper;

import java.util.ArrayList;


public class WrapperService extends Service {
    private static final String TAG = "WrapperService";

    public static final String START_ACTION = "com.sensordroid.START";
    public static final String STOP_ACTION = "com.sensordroid.STOP";

    private static Context mContext;
    public static final String name = "FLOW";

    private static int driverId;

    private static MainServiceConnection binder;
    private ServiceBackConnection serviceConnection;

    public static int current_frequency;
    public static ArrayList<Integer> channelList;

    @Override
    public void onCreate() {
        driverId = -1;
        binder = null;
        mContext = this;
        current_frequency = -1;
        channelList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1,new Notification());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "Got connection. onBind()");
        return null;
    }

    /*
        Called when started by intent, as from StartReceiver and StopReceiver
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent != null) {
            String action = intent.getStringExtra("ACTION");

            if (action.compareTo(START_ACTION) == 0) {

                driverId = intent.getIntExtra("DRIVER_ID", -1);
                start(intent.getStringExtra("SERVICE_ACTION"),
                        intent.getStringExtra("SERVICE_PACKAGE"),
                        intent.getStringExtra("SERVICE_NAME"),
                        intent.getIntExtra("CHANNEL", -1),
                        intent.getIntExtra("FREQUENCY", -1));
            } else if (action.compareTo(STOP_ACTION) == 0) {
                stop(intent.getIntExtra("CHANNEL", -1),
                        intent.getIntExtra("FREQUENCY", -1));
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }


    /*
        Start data acquisition by binding to the Collector application
            - binds to service_name, which is located in service_package and is listening for service_action
     */
    public void start(String action, String pack, String name, int channel, int frequency) {
        Log.w(TAG, "Adding new channel: "+channel+", with freq: "+frequency);
        Log.d(TAG, "start: event pack: " + pack + " name: " + name);

        if(!channelList.contains(channel)){
            channelList.add(channel);
        }

        if (binder == null) {
            serviceConnection = new ServiceBackConnection();
            toForeground();
            Intent service = new Intent(action);
            service.setComponent(new ComponentName(pack, name));
            mContext.bindService(service, serviceConnection, Service.BIND_AUTO_CREATE);
        }
    }


    /*
        Stop data acquisition, interrupt thread and unbind
     */
    public void stop(int channel, int frequency) {

        Log.w(TAG, "Stopping channel: "+channel+", new frequency: "+frequency);
        if(binder != null) {
            for(int i=0; i<channelList.size(); i++){
                if(channelList.get(i) == channel){
                    channelList.remove(i);
                    current_frequency = frequency;
                }
            }

            if(channelList.size() == 0){
                try {
                    Log.d(TAG, "stop: STOPPING");
                    serviceConnection.interruptThread();
                    mContext.unbindService(serviceConnection);
                    binder = null;
                    current_frequency = -1;
                    stopForeground(true);
                } catch (IllegalArgumentException iae){
                    iae.printStackTrace();
                }
            }
        }
    }

    public static int[] getChannelList(){
        int[] newOne = new int[channelList.size()];
        int index = 0;
        for(Integer i : channelList){
            newOne[index++] = i;
        }
        return newOne;
    }

    /**
     Sets the current service to the foreground
     */
    public void toForeground() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        builder.setSmallIcon(R.drawable.stat_notify_chat);
        builder.setContentTitle(WrapperService.name);
        builder.setTicker("Starting data collecting");
        builder.setContentText("Collecting data");

        Intent i = new Intent(this, WrapperService.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        builder.setContentIntent(pi);

        final Notification note = builder.build();

        startForeground(android.os.Process.myPid(), note);
    }

    private class ServiceBackConnection implements ServiceConnection {
        private Thread connectionThread;

        PowerManager powerManager = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, name + "Flow:WakeLock");

        /*
            Called when the service is connected,
                * Starts the working thread and acquires the wakelock
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = MainServiceConnection.Stub.asInterface(iBinder);
            Log.d(TAG, "onServiceConnected: In here");

            connectionThread = new Thread(
                    new CommunicationHandler(binder, name, driverId, getApplicationContext()));
            connectionThread.start();

            if (!wakeLock.isHeld()){
                Log.w("WakeLock", "Acquire");
                wakeLock.acquire(10*60*1000L /*10 minutes*/);
            }
        }

        /*
            Called if the service is unexpectedly disconnected
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: DISSCONNECTED");
            Log.w(TAG, "Service disconnected!");
             if (wakeLock.isHeld()){
                Log.w(TAG, "WakeLock released");
                wakeLock.release();
            }
            if (connectionThread != null) {
                connectionThread.interrupt();
                connectionThread = null;
            }
        }

        public void interruptThread() {
            if(wakeLock.isHeld()){
                Log.w(TAG, "WakeLock released");
                wakeLock.release();
            }
            if (connectionThread != null) {
                connectionThread.interrupt();
                connectionThread = null;
            }
        }
    }
}
