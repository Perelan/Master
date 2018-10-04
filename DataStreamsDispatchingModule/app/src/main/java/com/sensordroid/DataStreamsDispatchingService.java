package com.sensordroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service which is responsible for data streams dispatching. This Service provides publish-subscribe
 * interface via AIDL whose implementation (binder)  is returned when a client bind to this service.
 * During creation the sensor-capability model is populated by sending a HELLO Intent and receiveing
 * information about provided data types from all installed sensor wrappers.
 *
 * All data packets are received in putJson() method and added to packetQueue. The workerThread
 * running the Worker class dispatch all packets from the packetQueue.
 *
 * @author Daniel Bugajski
 */
public class DataStreamsDispatchingService extends Service {

    private static final String TAG = "DSDService";
    // definition of actions used to communicate with wrappers
    private static final String HELLO_ACTION = "com.sensordroid.HELLO";
    private static final String START_ACTION = "com.sensordroid.START";
    private static final String STOP_ACTION = "com.sensordroid.STOP";
    private static final String ADD_DRIVER_ACTION = "com.sensordroid.ADD_DRIVER";

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private BlockingQueue<String> packetQueue;
    private Thread workerThread;
    private HashMap<String, MainServiceConnection> connections;
    private HashMap<Integer, Wrapper> wrappers;
    private static int wrapperIndex;
    private Lock serviceLock;
    private static String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
    private static SecureRandom random;


    /**
     * Method called at the startup of component. All local variables of this object are initialized
     * in this method. A new wrapper object for logical capabilities also is created and added to
     * the sensor-capability model. This method registers BoradcastReceiver for wrappers and sends
     * Hello Intent to discovery all installed wrappers. Finally this Service object is set to run
     * as a foreground component.
     */
    public void onCreate(){
        // Initializes all local variables
        wrapperIndex = 0;
        connections = new HashMap<String, MainServiceConnection>();
        wrappers = new HashMap<Integer, Wrapper>();
        packetQueue = new ArrayBlockingQueue<String>(10000);
        serviceLock = new ReentrantLock();
        powerManager = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DSDServiceWakeLock");
        random = new SecureRandom();

        // Create and add new wrapper for logical sensors
        ArrayList<Integer> frequencies = new ArrayList<Integer>();
        frequencies.addAll(Arrays.asList(1,10,100,1000));
        wrappers.put(wrapperIndex, new Wrapper("LogicalWrapper", "MobileWrapper", wrapperIndex, frequencies));
        wrapperIndex++;

        // Register receiver of new wrappers and send hello Intent
        registerReceiver((wrapperReceiver), new IntentFilter(ADD_DRIVER_ACTION));
        sendBroadcast(new Intent(HELLO_ACTION));
        toForeground();
        super.onCreate();
    }

    /**
     * Method called when a component binds to this service. The local implementation of
     * AIDL interface is returned.
     *
     * @param intent call intent
     * @return local implementation of AIDL interface
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() finished: "+intent.getComponent()+" - "+intent.getDataString());
        return binder;
    }

    /**
     * Method calle when the Service is going to be destroyed. Running as foreground component is
     * stopped and all registred receivers are unregistered.
     */
    public void onDestroy(){
        stopForeground(true);
        unregisterReceiver(wrapperReceiver);
        Log.d(TAG, "onDestroy() finished");
        super.onDestroy();
    }

    /**
     * Class performing the dispatching of data packets. Picks up one by one data packet from the
     * packetQueue and dispatch them to recipients defined in the sensor-capability model.
     */
    private class Worker implements Runnable{
        @Override
        public void run() {
            Log.i(TAG, "dispatching packet");
            while(true){
                String packet = packetQueue.peek();
                if(packet!=null){
                    int wrapperNum = -1;
                    StringBuilder s = new StringBuilder();
                    try{
                        JSONObject pack = new JSONObject(packet);
                        wrapperNum = pack.getInt("id");
                        String time = pack.getString("time");
                        JSONArray dataPacksArray = pack.getJSONArray("data");
                        wrappers.get(wrapperNum).distributePackages(dataPacksArray, time);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    SystemClock.sleep(100);
                }
                if (Thread.currentThread().interrupted()) {
                    return;
                }
            }
        }
    }

    /**
     * Class responisble for estabilishing of connection (binding) to the client which has
     * subscribed on a capability for the first time. When the connection is estabilished
     * (when onServiceConencted callback adds returned MainServiceConnection to the connections hashmap)
     * the requested capability is subscribed.
     */
    private class ConnectionCreator implements Runnable{

        String capabilityId;
        int frequency;
        String componentPackageName;
        String componentClassName;

        ConnectionCreator(String capabilityId, int frequency, String componentPackageName, String componentClassName){
            this.capabilityId = capabilityId;
            this.frequency = frequency;
            this.componentPackageName = componentPackageName;
            this.componentClassName = componentClassName;
        }

        @Override
        public void run() {
            Intent intent = new Intent();
            intent.setClassName(componentPackageName, componentClassName);
            if(!connections.containsKey(componentClassName)){
                bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
            }
            while(!connections.containsKey(componentClassName)){
                SystemClock.sleep(100);
            }
            serviceLock.lock();
            try{
                String wrapperAndCapabilityId[] = capabilityId.split("-");
                Wrapper wrapper = wrappers.get(Integer.parseInt(wrapperAndCapabilityId[0]));
                if(wrapper == null){
                    // wrapper not found
                    return;
                }
                int capabilityIdInteger = Integer.parseInt(wrapperAndCapabilityId[1]);
                wrapper.addSubscriber(capabilityIdInteger, frequency, connections.get(componentClassName));
                startCollection(wrapper.getId(), wrapper.getWrapperNumber(), capabilityIdInteger, wrapper.getCurrentFreq());
            }catch(NumberFormatException e){
                e.printStackTrace();
            }finally {
                serviceLock.unlock();
                Log.i(TAG, "connection with: "+componentClassName+" estabilished");
                if(workerThread==null){
                    Log.i(TAG, ("t.start()"));
                    workerThread = new Thread(new Worker());
                    workerThread.start();
                }
                Thread.currentThread().interrupt();
                if (Thread.currentThread().interrupted()) {
                    return;
                }
            }
        }
    }


    /**
     * BroadcastReceiver responsible for receiving of sensor wrappers. Received information are used
     * to create a wrapper object which is added to the wrapper hashmap. All corresponding
     * capabilities are added to the created wrapper object.
     */
    BroadcastReceiver wrapperReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String wrapperId = intent.getStringExtra("ID");
            String wrapperName = intent.getStringExtra("NAME");
            ArrayList<Integer> frequencies = intent.getIntegerArrayListExtra("FREQUENCIES");
            //sjekk om wrapper med samme id eksisterer
            int existingWrapperId = -1;
            for(Wrapper w : wrappers.values()){
                if(w.getId()==wrapperId){
                    existingWrapperId = w.getWrapperNumber();
                    break;
                }
            }
            //hvis eksisterer bruk samme wrapperIndex som den eksisterende har
            Wrapper wrapper = null;
            if(existingWrapperId > 0){
                wrapper = new Wrapper(wrapperId, wrapperName, existingWrapperId, frequencies);
            }else{
                wrapper = new Wrapper(wrapperId, wrapperName, wrapperIndex++, frequencies);
            }
            //add capabilities suported by wrapper
            ArrayList<String> supported_types = intent.getStringArrayListExtra("SUPPORTED_TYPES");
            for(String s : supported_types){
                String channel[] = s.split(",");
                Capability c = new Capability(channel[0], channel[1], channel[2], channel[3], wrapper);
                wrapper.addCapability(c);
            }
            //add wrapper to wrappersArray
            Wrapper existingWrapper = wrappers.put(wrapper.getWrapperNumber(), wrapper);
            if(existingWrapper != null){
                moveSubscribersToNewWrapper(existingWrapper, wrapper);
            }
        }
    };


    /**
     * Connection callback with two methods. onServiceConnected() method called when a connection
     * with a client is estabilished and its implementation of AIDL interface is returned. The returned
     * implementation is saved in connection hashamp with the class name of client as key.
     * onServiceDisconnected() method is called when an existing connection is disconnected. The
     * implementation of AIDL interface is removed from the connection hashmap.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "RECEIVED CONNECTION from "+componentName.getClassName());
            connections.put(componentName.getClassName(), MainServiceConnection.Stub.asInterface(iBinder));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            connections.remove(componentName.getClassName());
        }
    };

    /*
        Implementation of the interface defined in MainServiceConnection.aidl
     */
    private final MainServiceConnection.Stub binder = new MainServiceConnection.Stub() {

        /**
         * This method receives data packets from all wrappers and clients which publish a data type.
         * The received data packet is added to the packetQueue.
         *
         * @param json data packet received in JSON format
         */
        public void putJson(String json) {
            try{
                packetQueue.put(json);
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }

        /**
         * Method resposnible for creation of susbcription for a capability. If the client subscribes
         * for the first time a new thread with ConnectionCreator object is started to handle the
         * connection estabilishing, otherwise if client allready exists in connections hashmap
         * only the subscrption operation is performed.
         *
         * @param capabilityId id of requested capability
         * @param frequency selected frequency of subscription
         * @param componentPackageName package name of subscriber
         * @param componentClassName class name of subscribed
         * @return status code: -3  invalid wrapper
         *                      -2  requested frequency is too high
         *                      -1  requested capability does not exists
         *                       0  if capability is subscribed
         *                       1  if this is the first subscription from this client and a new connection has to be estabilished
         */
        public int Subscribe(String capabilityId, int frequency, String componentPackageName, String componentClassName){
            Log.d(TAG, "Subscriprion from "+componentClassName+", to "+capabilityId);

            String key = componentClassName;
            if(!connections.containsKey(key)){
                Thread connectionCreatorThread = new Thread(new ConnectionCreator(capabilityId, frequency, componentPackageName, componentClassName));
                connectionCreatorThread.start();
                return 1;
            }
            if(workerThread==null){
                workerThread = new Thread(new Worker());
                workerThread.start();
            }
            serviceLock.lock();
            try{
                String wrapperAndCapabilityId[] = capabilityId.split("-");
                Wrapper wrapper = wrappers.get(Integer.parseInt(wrapperAndCapabilityId[0]));
                if(wrapper == null){
                    // wrapper not found
                    return -3;
                }
                int capabilityIdInteger = Integer.parseInt(wrapperAndCapabilityId[1]);
                int statusCode = wrapper.addSubscriber(capabilityIdInteger, frequency, connections.get(key));
                if(statusCode == 0){
                    startCollection(wrapper.getId(), wrapper.getWrapperNumber(), capabilityIdInteger, wrapper.getCurrentFreq());
                    return statusCode;
                }else{
                    return statusCode;
                }
            }catch(NumberFormatException e){
                e.printStackTrace();
                return -1;
            }finally {
                serviceLock.unlock();
            }
        }

        /**
         * Method responisble for unsubscribing of a capability.
         *
         * @param capabilityId id of capability which is going to be unsubscribed
         * @param componentClassName class name of component calling this method
         * @return
         */
        public int Unsubscribe(String capabilityId, String componentClassName){
            Log.d(TAG, "Unsubscribing: "+capabilityId);

            String key = componentClassName;
            if(!connections.containsKey(key)){
                return -3;
            }
            serviceLock.lock();
            try{
                String wrapperAndCapabilityId[] = capabilityId.split("-");
                Wrapper wrapper = wrappers.get(Integer.parseInt(wrapperAndCapabilityId[0]));
                if(wrapper == null){
                    // wrapper not found
                    return -3;
                }
                int capabilityIdInteger = Integer.parseInt(wrapperAndCapabilityId[1]);
                int statusCode = wrapper.removeSubscriber(capabilityIdInteger, connections.get(key));
                if(statusCode == 0){
                    stopCollection(wrapper.getId(), wrapper.getWrapperNumber(), capabilityIdInteger, wrapper.getCurrentFreq());
                    checkSubscriptionExistance();
                    return statusCode;
                }else{
                    return statusCode;
                }
            }catch(NumberFormatException e){
                e.printStackTrace();
                return -1;
            }finally {
                serviceLock.unlock();
            }
        }

        /**
         * Creates a new logical capability and generates a key which is returned.
         *
         * @param capabilityId id of published capability
         * @param type type of published data
         * @param metric metric of published data
         * @param description description of published data
         * @return keyToken generated under creation of published capability
         */
        public String Publish(String capabilityId, String type, String metric, String description){
            Wrapper wrapper = wrappers.get(0);
            Capability capability = new Capability(capabilityId, type, metric, description, wrapper);
            if(wrapper.getCapabilities().keySet().contains(capabilityId)){
                return "";//add kode return
            }
            String key = generatesRandomKey();
            capability.setKey(key);
            wrapper.addCapability(capability);
            return key;
        }

        /**
         * Method responsible for unpublishing a capability. The same key as returned during creation
         * of the logical capability for publishing should be given as parameter. Otherwise the
         * capability will not be unsubscribed.
         *
         * @param capabilityId id of logical capability which should be unsubscribed
         * @param key key returned while publishing of the capability
         */
        public void Unpublish(String capabilityId, String key){
            Wrapper wrapper = wrappers.get(0);
            Capability c = wrapper.getCapabilities().get(capabilityId);
            if(c != null){
                String capKey = c.getKey();
                if(!capKey.equals("") && capKey.equals(key)){
                    wrapper.getCapabilities().remove(capabilityId);
                }
            }
        }

        /**
         * Method that returns all capabilities in the sensor-capability model.
         *
         * @return list of capabilities from sensor-capability model.
         */
        public List<String> getPublishers(){
            Log.d(TAG, "getPublishers() called");
            List<String> tmp = new ArrayList<String>();
            int index = 0;
            for(Wrapper w : wrappers.values()){
                for(Capability c : w.getCapabilities().values()){
                    tmp.add(c.getInfo());
                }
            }
            Log.d(TAG, "Lengde på publishers: "+tmp.size());
            return tmp;
        }
    };

    /**
     * Method which update sensor wrapper if necessary.
     *
     * @param wrapperId wrapper id which the channel contains to
     * @param wrapperNum wrapper number defined by this service which should be added to each packet
     * @param channel channel id of a new channel or channel which should be updated
     * @param newFrequency a new frequency which should be updated
     */
    private void startCollection(String wrapperId, int wrapperNum, int channel, int newFrequency){
        Log.d(TAG, "startCollection() called, freq = "+newFrequency);
        Intent start = new Intent(START_ACTION);
        start.putExtra("WRAPPER_ID", wrapperId);
        start.putExtra("WRAPPER_NUMBER", wrapperNum);
        start.putExtra("WRAPPER_CHANNEL", channel);
        start.putExtra("WRAPPER_FREQUENCY", newFrequency);

        start.putExtra("SERVICE_ACTION", "com.sensordroid.service.START_SERVICE");
        start.putExtra("SERVICE_PACKAGE", "com.sensordroid");
        start.putExtra("SERVICE_NAME", "com.sensordroid.DataStreamsDispatchingService");

        sendBroadcast(start);
    }

    /**
     * Stops collection on given channel.
     *
     * @param wrapperId wrapper id which the channel contains to
     * @param wrapperNum wrapper number defined by this service
     * @param channel channel id which should be stoped
     * @param newFrequency frequency which is 0
     */
    private void stopCollection(String wrapperId, int wrapperNum, int channel, int newFrequency){
        Log.d(TAG, "stopCollection() called, freq = "+newFrequency);
        Intent stop = new Intent(STOP_ACTION);
        stop.putExtra("WRAPPER_ID", wrapperId);
        stop.putExtra("WRAPPER_NUMBER", wrapperNum);
        stop.putExtra("WRAPPER_CHANNEL", channel);
        stop.putExtra("WRAPPER_FREQUENCY", newFrequency);

        stop.putExtra("SERVICE_ACTION", "com.sensordroid.service.STOP_SERVICE");
        stop.putExtra("SERVICE_PACKAGE", "com.sensordroid");
        stop.putExtra("SERVICE_NAME", "com.sensordroid.DataStreamsDispatchingService");

        sendBroadcast(stop);
    }

    /**
     * Generates a random String alphanumeric key of given length.
     *
     * @return String key
     */
    private String generatesRandomKey(){
        int keyLength = 15;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < keyLength; ++i) {
            stringBuilder.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return stringBuilder.toString();
    }

    /**
     * Goes through all wrappers and checkos whether all wrappers are used, if yes the worker thread
     * which dispatchs data packets is stopped. If at least one wrapper collects data no action is
     * performed.
     */
    private void checkSubscriptionExistance(){
        Log.i(TAG, "checkSubscriptionExistance()");
        for(Wrapper w : wrappers.values()){
            if(w.getCurrentFreq()>0){
                return;
            }
        }
        if(workerThread!=null){
            Log.i(TAG, "t.interrupt()");
            while(!workerThread.isInterrupted()){
                workerThread.interrupt();
            }
            workerThread=null;
        }
    }

    /**
     * Maps capabilites from a new wrapper to the old wrapper and moves all subscribers of same
     * capabilites from the old wrapper to a new wrapper.
     *
     * @param oldWrapper old wrapper object
     * @param newWrapper new wrapper object
     */
    private void moveSubscribersToNewWrapper(Wrapper oldWrapper, Wrapper newWrapper){
        HashMap<Integer, Capability> oldCapabilities = oldWrapper.getCapabilities();
        HashMap<Integer, Capability> newCapabilities = newWrapper.getCapabilities();
        for(Integer i : newCapabilities.keySet()){
            Capability newCapability = newCapabilities.get(i);
            if(oldCapabilities.containsKey(i)){
                Capability oldCapability = oldCapabilities.get(i);
                if(oldCapability.getType().equals(newCapability.getType()) &&
                        oldCapability.getMetric().equals(newCapability.getMetric())){
                    newCapability.setSubscribers(oldCapability.getSubscribers());
                }
                oldCapabilities.remove(i);
            }
        }
    }

    /**
     * Method responsible for make this component a foreground component.
     */
    public void toForeground() {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.stat_notify_chat);
        builder.setContentTitle("DataStreamsDispatchingService");
        builder.setTicker("Forwarding");
        builder.setContentText("Forwarding data");

        Intent i = new Intent(this, DataStreamsDispatchingService.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        builder.setContentIntent(pi);

        final Notification note = builder.build();

        startForeground(android.os.Process.myPid(), note);
    }
}
