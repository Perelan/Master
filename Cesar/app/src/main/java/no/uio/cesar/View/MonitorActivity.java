package no.uio.cesar.View;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import no.uio.cesar.R;
import no.uio.ripple.RippleEffect;

public class MonitorActivity extends AppCompatActivity {

    private RippleEffect rp;
    private Chronometer cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> dialogSessionEnd());

        cm = findViewById(R.id.monitor_time);
        cm.start();

        rp = findViewById(R.id.ripple);

        rp.setOnClickListener(view -> rp.pulse(rp.BREATH));

        rp.pulse(rp.BATTERY);
    }

    @Override
    public void onBackPressed() {
        dialogSessionEnd();
    }

    public void dialogSessionEnd() {
        new AlertDialog.Builder(this)
                .setMessage("Are you done with the monitor session?")
                .setCancelable(false)
                .setNegativeButton("NO!", null)
                .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                    storeAndFinishSession();
                }).create().show();
    }

    public void storeAndFinishSession() {
        cm.stop();

        // returns MS elapsed time
        System.out.println(SystemClock.elapsedRealtime() - cm.getBase());

        finish();
    }
}
