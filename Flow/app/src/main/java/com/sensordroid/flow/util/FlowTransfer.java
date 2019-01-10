package com.sensordroid.flow.util;

public class FlowTransfer {

    public final static int TYPE_OFF = 0,
                            TYPE_RAW = 1,
                            TYPE_ECG = 2,
                            TYPE_RIP = 3;

    private final static String[] types = new String[] {
            "Raw data", "ECG", "RIP"
    };

    private final static String[] metric = new String[] {
            "Raw data", "Milivolt", "milivolt"
    };

    public static String getMetric(int type) {
        return metric[type - 1];
    }

    public static String getType(int type) {
        return types[type - 1];
    }
}
