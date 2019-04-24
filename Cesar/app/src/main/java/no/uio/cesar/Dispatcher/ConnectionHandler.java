package no.uio.cesar.Dispatcher;

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

public class ConnectionHandler {

    private static final String TAG = "ConnectionHandler";

    private Context context;

    private List<String> publishers;

    private MainServiceConnection msc;

    private ConnectionCallback callback;

    public ConnectionHandler(Context context, ConnectionCallback callback) {
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

    public void establish() {
        Log.d(TAG, "establish");

        Intent intent = new Intent(MainServiceConnection.class.getName());
        intent.setAction("com.sensordroid.ADD_DRIVER");
        intent.setPackage("com.sensordroid");
        context.bindService(intent, serviceCon, Service.BIND_AUTO_CREATE);
    }

    private void init() {
        Log.d(TAG, "init");
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
        Log.d(TAG, "connect");

        try {
            String s = publishers.get(0).split(",")[0];
            int res = msc.Subscribe(
                    s,
                    0,
                    context.getPackageName(),
                    DSDService.class.getName());

            Log.d(TAG, "connect: result " + res);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        Log.d(TAG, "disconnect");

        if (msc != null) {
            try {
                if (publishers.isEmpty()) {
                    Log.d(TAG, "disconnect: no publishers");
                } else {
                    String s = publishers.get(0).split(",")[0];

                    int res = msc.Unsubscribe(s, DSDService.class.getName());

                    Log.d(TAG, "disconnect: result " + res);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void reconnect() {
        Log.d(TAG, "reconnect");

        disconnect();
        connect();
    }

    public void cleanup() {
        Log.d(TAG, "cleanup");

        disconnect();

        try {
            context.unbindService(serviceCon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
