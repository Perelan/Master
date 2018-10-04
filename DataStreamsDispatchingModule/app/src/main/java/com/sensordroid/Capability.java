package com.sensordroid;

import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Represents a data type from either a sensor channel or a logical sensor.
 *
 * @author Daniel Bugajski
 */
public class Capability {

    private int id;
    private String type;
    private String metric;
    private String description;
    private Wrapper wrapper;

    /* highest frequency currently required by this capability */
    private int currentCapabilityFreq;

    /* counter of received data packets */
    private int packCounter;

    /* key token generated when publishing of this capability started */
    private String key;

    /* hashmap with frequencies as keys and list of subscriber's MainServiceConnection subscribing on given frequency */
    private HashMap<Integer, ArrayList<MainServiceConnection>> subscribers;

    /* tag used to logging */
    private String TAG = "CAPABILITY";

    /**
     * Class constructor.
     *
     * @param id String value of id nummer to the channel this capability represents
     * @param type type of data this capability provide
     * @param metric metric in which data are collected
     * @param description additional description of the collected data
     * @param wrapper wrapper object which contains this capability
     */
    Capability(String id, String type, String metric, String description, Wrapper wrapper){
        this.id = Integer.parseInt(id);
        this.type = type;
        this.metric = metric;
        this.description = description;
        this.wrapper = wrapper;
        this.subscribers = new HashMap<Integer, ArrayList<MainServiceConnection>>();
        this.key = "";
        this.packCounter = 0;
        this.currentCapabilityFreq = 0;
        TAG = TAG + "(" + type +") ";
        Log.i(TAG, "created.");
    }

    /**
     * Set the key of this capability to the value given as parameter.
     *
     * @param key a new key value
     */
    protected void setKey(String key){
        this.key = key;
    }

    /**
     * Creates a new hashmap of subscribers which contins all records from newSubscribers parameter.
     *
     * @param newSubscribers hashmap with new subscribers
     */
    protected void setSubscribers(HashMap<Integer, ArrayList<MainServiceConnection>> newSubscribers){
        this.subscribers = new HashMap<>(newSubscribers);
    }

    /**
     * Returns the id of this Capability object.
     *
     * @return the id of this Capability object
     */
    protected int getId(){
        return this.id;
    }

    /**
     * Returns the current capability frequency.
     *
     * @return currentCapabilityFreq value.
     */
    protected int getCurrentCapabilityFreq(){
        return this.currentCapabilityFreq;
    }

    /**
     * Returns the type of this Capability object.
     *
     * @return the type of this capability
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the metric of this Capability object.
     *
     * @return the metric of this capability
     */
    public String getMetric() {
        return this.metric;
    }

    /**
     * Returns the key value of this capability.
     *
     * @return the key field value of this capability
     */
    protected String getKey(){
        return this.key;
    }

    /**
     * Returns the hashmap of subscribers.
     *
     * @return the hashmap object of subscribers
     */
    protected HashMap<Integer, ArrayList<MainServiceConnection>> getSubscribers(){
        return this.subscribers;
    }

    /**
     * Returns true if the subscriber hashmap is not empty, otherwise the false value is returned.
     *
     * @return true if at least one subscription exists in hashmap, false if not
     */
    protected boolean allreadySubscribed(){
        return this.subscribers.size()>0;
    }

    /**
     * Adds a new subscriber to the hashmap of subscribers, with required frequency as key.
     * If a subscription on the same frequency allready exists the MainServiceConnection binder
     * is added to the existing list of MainServiceConnections, if not a new list including the
     * given MainServiceConnection is created.
     * If the subscriber hashmap contains allready the given MainServiceConnection binder the old
     * subscription is removed and the new is added.
     * In cases where the currentCapabilityFreq could be updated the updateCurrentCapabilityFreq()
     * method is called and its status code returned.
     *
     * @param freq required frequency of subscription
     * @param binder MainServiceConnection binder of subscriber
     * @return status code 0 if the currentCapabilityFreq was updated or 1 if not
     */
    public int addSubscriber(int freq, MainServiceConnection binder){
        Log.i(TAG, "adding of a new subscriber at frequency: "+freq);
        if(subscribers.size() == 0){
            ArrayList<MainServiceConnection> newList = new ArrayList<MainServiceConnection>();
            newList.add(binder);
            subscribers.put(freq, newList);
            return updateCurrentCapabilityFreq();
        }else{
            removeSubscriber(binder);
            if(subscribers.containsKey(freq)){
                subscribers.get(freq).add(binder);
                return 1;
            }else{
                ArrayList<MainServiceConnection> newList = new ArrayList<MainServiceConnection>();
                newList.add(binder);
                subscribers.put(freq, newList);
                return updateCurrentCapabilityFreq();
            }
        }
    }

    /**
     * Goes through all subscribers and if the binder given as parameter exists in subscriber
     * hashmap then it is removed from the subscriber hashmap.
     * In case where the currentCapabilityFreq could be updated the updateCurrentCapabilityFreq()
     * method is called and its status code is returned.
     *
     * @param binder MainServiceConnection object which is the binder of subscriber
     * @return status code 0 if the currentCapabilityFreq was updated or 1 if not
     */
    public int removeSubscriber(MainServiceConnection binder){
        Log.i(TAG, "removing of a subscriber");
        for(Integer i:subscribers.keySet()){
            if(subscribers.get(i).contains(binder)){
                if(subscribers.get(i).size()==1){
                    subscribers.remove(i);
                    return updateCurrentCapabilityFreq();
                }else{
                    subscribers.get(i).remove(binder);
                    return 1;
                }
            }
        }
        return 1;
    }

    /**
     * Goes through all subscribed frequencies and finds the new highest requested frequency.
     * If the highest requested frequency differs than the currentCapabilityFreq value,
     * the currentCapabilityFreq is upddated to the highest requested frequency and
     * a status code 0 which means that the currentCapabilityFreq was updated is returned.
     * If the currentCapabilityFreq was not updated, status code 1 is returned.
     *
     * @return status code 0 if currentCapabilityFreq was updated or 1 if not
     */
    private int updateCurrentCapabilityFreq(){
        int highestRequestedFrequency = 0;
        if(subscribers.size() == 0){
            highestRequestedFrequency = 0;
        }else{
            highestRequestedFrequency = Collections.max(subscribers.keySet());
        }
        if(this.currentCapabilityFreq != highestRequestedFrequency){
            this.currentCapabilityFreq = highestRequestedFrequency;
            return 0;
        }
        return 1;
    }

    /**
     *  Goes through all frequencies in the subscriber hashmap and calculate subscribers of which
     *  frequencies should receive the data packet. If a frequency is qualified for receiving of
     *  this packet then the remote procedure calls to all binders which are assosieated with this
     *  frequency are performed.
     *
     * @param json data packet in JSON format
     * @exception RemoteException thrown when the remote procedure call to
     *                            the binder of a subscriber goes wrong
     */
    public void sendJson(String json) {
        packCounter++;
        for(Integer i:subscribers.keySet()){
            if(((int)((double)packCounter % ((double)wrapper.getCurrentFreq()/(double)i))) == 0){
                for(MainServiceConnection b:subscribers.get(i)){
                    try{
                        b.putJson(json);
                    }catch (RemoteException re) {
                        re.printStackTrace();
                    }
                }
            }
        }
        if(packCounter == Integer.MAX_VALUE){
            packCounter = 0;
        }
    }

    /**
     * Returns main information about this Capability object in form of a String.
     * id, type, metric, description and the highest available frequency are returned
     * as comma separated values in one String.
     *
     * @return information about this Capability object as comma separated values
     */
    public String getInfo(){
        return wrapper.getWrapperNumber()+"-"+this.id+","+this.type+","+this.metric+","+this.description+","+wrapper.getMaxFrequency();
    }
}
