package com.cesar.moduletemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cesar.moduletemplate.Payload.PayloadFormat;
import com.cesar.moduletemplate.Payload.Record;
import com.cesar.moduletemplate.Payload.Sample;
import com.cesar.moduletemplate.Payload.User;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            List<PayloadFormat> data = DataExtraction.extract(extras);

            test(data);
        }
    }

    public void test(List<PayloadFormat> data) {
        if (data == null) return;

        for (PayloadFormat pf : data) {
            Record r = pf.getRecord();
            User u = r.getUser();

            List<Sample> samples = pf.getSamples();

            System.out.println(
                    String.format(Locale.getDefault(),
                            "Record ID %d (User: %s) - Samples %d",
                            r.getId(), u.getName(), samples.size()));
        }
    }
}
