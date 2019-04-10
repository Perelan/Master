package no.uio.cesar.View.MonitorView;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.sensordroid.MainServiceConnection;

import java.util.List;

import no.uio.cesar.DSDService;

class ConnectionHandler {

    private static final String TAG = "ConnectionHandler";

    private Context context;

    private List<String> publishers;

    private MainServiceConnection msc;

    private ConnectionCallback callback;

    ConnectionHandler(Context context, ConnectionCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    private ServiceConnection serviceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msc = MainServiceConnection.Stub.asInterface(service);

            init();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    void establish() {
        Intent intent = new Intent(MainServiceConnection.class.getName());
        intent.setAction("com.sensordroid.ADD_DRIVER");
        intent.setPackage("com.sensordroid");
        context.bindService(intent, serviceCon, Service.BIND_AUTO_CREATE);
    }

    private void init() {
        Handler h = new Handler();

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    publishers = msc.getPublishers();

                    if (publishers.isEmpty()) {
                        h.postDelayed(this, 1_000);
                        return;
                    }

                    h.removeCallbacks(this);

                    connect();

                    callback.connected(publishers);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 1_000);
    }

    private void connect() {
        try {
            String s = publishers.get(0).split(",")[0];
            int res = msc.Subscribe(s, 0, context.getPackageName(), DSDService.class.getName());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        if (msc != null) {
            try {
                if (publishers.isEmpty()) {
                    System.out.println("IN STORE: NO PUBLISHERS");
                } else {
                    String s = publishers.get(0).split(",")[0];

                    int res = msc.Unsubscribe(s, DSDService.class.getName());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    void reconnect() {
        disconnect();
        connect();
    }

    void cleanup() {
        Log.d(TAG, "cleanup");

        disconnect();

        try {
            context.unbindService(serviceCon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
