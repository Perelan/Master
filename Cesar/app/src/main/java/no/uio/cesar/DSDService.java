package no.uio.cesar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.sensordroid.MainServiceConnection;

import java.util.List;

public class DSDService extends Service {
    public DSDService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    MainServiceConnection.Stub binder = new MainServiceConnection.Stub() {
        @Override
        public void putJson(String json) throws RemoteException {
            System.out.println("HAHAHAH " + json);
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
