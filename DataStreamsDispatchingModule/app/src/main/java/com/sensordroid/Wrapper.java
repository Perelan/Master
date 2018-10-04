package com.sensordroid;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Wrapper object representing a sensor wrapper and containing all capabilities corresponding to this
 * sensor wrapper.
 */
public class Wrapper {
    private static final String TAG = "Wrapper";
    private String id;
    private String wrapperName;
    private int number;
    private ArrayList<Integer> frequencies;
    private int maxWrapperFreq;
    private int currentWrapperFreq;
    private HashMap<Integer, Capability> capabilities;
    private Lock wrapperLock;

    /**
     * Class constructor.
     *
     * @param id id of the wrapper
     * @param wrapperName wrapper name
     * @param number wrapper number given by the service
     * @param frequencies frequencies supported by the wrapper
     */
    public Wrapper(String id, String wrapperName, int number, ArrayList<Integer> frequencies) {
        this.id = id;
        this.wrapperName = wrapperName;
        this.number = number;
        if(frequencies == null){
            this.frequencies = new ArrayList<Integer>();
        }else{
            this.frequencies = frequencies;
        }
        frequencies.add(0);
        Collections.sort(this.frequencies);
        this.currentWrapperFreq = 0;
        this.maxWrapperFreq = Collections.max(frequencies);
        this.capabilities = new HashMap<Integer, Capability>();
        this.wrapperLock = new ReentrantLock();
    }

    public String getId(){
        return this.id;
    }

    /**
     * Returns the name of this wrapper.
     *
     * @return name of the wrapper
     */
    public String getWrapperName(){
        return this.wrapperName;
    }

    /**
     * Returns the maximal frequency supported by this wrapper.
     *
     * @return maximal frequency supported by the wrapper
     */
    public int getMaxFrequency(){
        return this.maxWrapperFreq;
    }

    /**
     * Returns current frequency of the wrapper.
     *
     * @return current frequency
     */
    public int getCurrentFreq(){
        return this.currentWrapperFreq;
    }

    /**
     * Returns the number of this wrapper.
     *
     * @return number of this wrapper.
     */
    public int getWrapperNumber(){
        return this.number;
    }

    /**
     * Return the capabilities hashmap
     *
     * @return capabilities hashmap
     */
    public HashMap<Integer, Capability> getCapabilities(){
        return this.capabilities;
    }

    /**
     * Adds capability to capabilities hashmap.
     *
     * @param c new capability which will be added
     */
    public void addCapability(Capability c){
        this.capabilities.put(c.getId(), c);
    }

    /**
     * Method responsible for adding a new subscriber for requested capability.
     *
     * @param capabilityId id of capability
     * @param frequency requested frequency of subscription
     * @param binder binder of the subscriber
     * @return status code: -2  requested frequency is too high
     *                      -1  capability does not exists
     *                       0  update of frequency is needed
     *                       1  subscription added, no updates needed
     */
    public int addSubscriber(int capabilityId, int frequency, MainServiceConnection binder){
        Capability c = this.capabilities.get(capabilityId);
        boolean allreadySubscribedCapability = c.allreadySubscribed();
        if(c == null){
            // capability does not exists, subscriber not added
            return -1;
        }
        if(frequency > this.maxWrapperFreq){
            // frequency is too high, subscriber not added
            return -2;
        }
        int statusCode = c.addSubscriber(frequency, binder);
        // subscriber added
        boolean frequencyUpdated = false;
        if(statusCode == 0){
            // frequency might be updated
            frequencyUpdated = (updateCurrentFrequency()==0);
        }
        if(!allreadySubscribedCapability || frequencyUpdated){
            // updadte needed
            return 0;
        }
        // no update needed
        return 1;
    }

    /**
     * Removes given binder from the given capability.
     *
     * @param capabilityId id of capability which the binder subscribed on
     * @param binder binder which will be removed
     * @return
     */
    public int removeSubscriber(int capabilityId, MainServiceConnection binder){
        Capability c = this.capabilities.get(capabilityId);
        if(c == null){
            // capability does not exists, subscriber not removed
            return -1;
        }
        int statusCode = c.removeSubscriber(binder);
        // subscriber removed
        if(statusCode == 0){
            // frequency or channel list might be updated
            return updateCurrentFrequency();
        }
        return statusCode;
    }

    /**
     * Method responsible for uppdating the current needed frequency for this sensor wrapper.
     *
     * @return status code: 0 if frequency was updated
     *                      1 if no update was needed
     */
    public int updateCurrentFrequency(){
        int maxNeededFreq = 0;
        for(Capability c : capabilities.values()){
            int currentCapabilityFreqOfC = c.getCurrentCapabilityFreq();
            if(currentCapabilityFreqOfC > maxNeededFreq){
                maxNeededFreq = currentCapabilityFreqOfC;
            }
        }
        if(currentWrapperFreq != maxNeededFreq){
            this.currentWrapperFreq = maxNeededFreq;
            return 0;
        }
        return 1;
    }

    /**
     * Compares two wrappers object, if the given object has the same id then true is returned,
     * if not false is returned.
     *
     * @param a an object
     * @return true if a has the same id as this object
     */
    @Override
    public boolean equals(Object a){
        if (a instanceof Wrapper){
            Wrapper obj = (Wrapper) a;
            return obj.getId().compareTo(this.getId()) == 0;
        }
        return false;
    }

    /**
     * Method responsible for dispatching of packets from sensor wrapper which this object represent.
     *
     * @param array JSON array with data values from channels from this wrapper
     * @param time date in String format
     */
    public void distributePackages(JSONArray array, String time){
        wrapperLock.lock();
        try{
            int channelId;
            JSONObject dataPack;
            for(int i=0; i<array.length(); i++){
                dataPack = array.getJSONObject(i);
                dataPack.put("time", time);
                channelId = dataPack.getInt("id");
                dataPack.put("id", this.number+"-"+channelId);
                capabilities.get(channelId).sendJson(dataPack.toString());
            }
        }catch(JSONException je){
            Log.d(TAG, "Unknown data package received: "+number);
        }finally {
            wrapperLock.unlock();
        }
    }
}