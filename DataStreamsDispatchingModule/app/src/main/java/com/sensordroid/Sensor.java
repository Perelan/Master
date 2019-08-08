package com.sensordroid;

// Created by: Jagat Deep Singh (2018-2019).
// See section 5.1.2 in thesis from Jagat Deep Singh for further elaboration.
public class Sensor {
    private String name, packageName;

    public Sensor(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
