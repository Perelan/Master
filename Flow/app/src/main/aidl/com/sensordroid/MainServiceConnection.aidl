// MainServiceConnection.aidl
package com.sensordroid;

// Declare any non-default types here with import statements

interface MainServiceConnection {
    void putJson(in String json);
    int Subscribe(String capabilityId, int frequency, String componentPackageName, String componentClassName);
    int Unsubscribe(String capabilityId, String componentClassName);
    String Publish(String capabilityId, String type, String metric, String description);
    void Unpublish(String capabilityId, String key);
    List<String> getPublishers();
}
