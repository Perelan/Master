package no.uio.cesar.Dispatcher;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.sensordroid.MainServiceConnection;

import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DSDService extends Service {
    private final static String TAG = "CESARservice";

    LocalBroadcastManager localBroadcastManager;

    public DSDService() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    MainServiceConnection.Stub binder = new MainServiceConnection.Stub() {
        @Override
        public void putJson(String json) throws RemoteException {
            Log.d(TAG, "putJson: " + json);

            Intent localIntent = new Intent("PUT_DATA");
            Bundle b = new Bundle();
            b.putString("data", json);

            localIntent.putExtras(b);

            localBroadcastManager.sendBroadcast(localIntent);
        }

        @Override
        public int Subscribe(String capabilityId, int frequency, String componentPackageName, String componentClassName) throws RemoteException {
            return 0;
        }

        @Override
        public int Unsubscribe(String capabilityId, String componentClassName) throws RemoteException {
            return 0;
        }

        @Override
        public String Publish(String capabilityId, String type, String metric, String description) throws RemoteException {
            return null;
        }

        @Override
        public void Unpublish(String capabilityId, String key) throws RemoteException {

        }

        @Override
        public List<String> getPublishers() throws RemoteException {
            return null;
        }
    };

}
