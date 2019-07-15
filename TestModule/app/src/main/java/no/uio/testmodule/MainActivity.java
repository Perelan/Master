package no.uio.testmodule;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Extrapolate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extra = getIntent().getExtras();

        if (extra == null) return;

        String a = extra.getString("data");

        List<ExportObject> test = new Gson().fromJson(a,  new TypeToken<List<ExportObject>>(){}.getType());

        if (test == null) return;

        Record r = null;

        for (ExportObject eo : test) {
            if (r == null) {
                r = eo.getRecord();
            } else {

                if (r.getNrSamples() < eo.getRecord().getNrSamples()) {
                    r = eo.getRecord();
                }
            }
        }

        TextView tv = findViewById(R.id.data);
        TextView nrRecords = findViewById(R.id.number_records);

        tv.setText(r.getName() +" - " + r.getNrSamples());

        nrRecords.setText("" + test.size());
    }
}
