package no.uio.cesar.View;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import no.uio.cesar.R;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.View.RecordView.RecordingFragment;

public class RecordingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        Fragment f = new RecordingFragment();
        Uti.commitFragmentTransaction(this, f);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(
                this,
                "Stop the monitor and save/discard session",
                Toast.LENGTH_LONG).show();
    }
}
