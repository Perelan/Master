package com.cesar.moduletemplate;

import android.os.Bundle;

import com.cesar.moduletemplate.Payload.PayloadFormat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class DataExtraction {
    public static List<PayloadFormat> extract(Bundle b) {
        String jsonString = b.getString("data");

        return new Gson().fromJson(jsonString,  new TypeToken<List<PayloadFormat>>(){}.getType());
    }
}
