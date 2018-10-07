// MainServiceConnection.aidl
package com.sensordroid;

// Declare any non-default types here with import statements

interface MainServiceConnection {
    oneway void putJson(in String json);
}
