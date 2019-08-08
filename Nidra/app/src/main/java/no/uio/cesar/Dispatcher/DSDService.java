package no.uio.cesar.Dispatcher;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.sensordroid.MainServiceConnection;

import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import no.uio.cesar.R;
import no.uio.cesar.View.RecordView.RecordingFragment;

public class DSDService extends Service {
    private final static String TAG = "CESARservice";

    LocalBroadcastManager localBroadcastManager;

    public DSDService() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onCreate() {
        toForeground();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
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

            // handled by the recordingfragment.
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


    public void toForeground() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this, notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setSmallIcon(R.drawable.ic_info_black_24dp);
        builder.setContentTitle("Nidra");
        builder.setTicker("Recording");
        builder.setContentText("Recoding data");

        Intent i = new Intent(this, DSDService.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        builder.setContentIntent(pi);

        final Notification note = builder.build();

        startForeground(android.os.Process.myPid(), note);
    }

}
