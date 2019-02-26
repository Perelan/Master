package no.uio.cesar.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.Model.Record;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Constant;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.View.ProfileView.ProfileFragment;
import no.uio.cesar.View.RecordView.RecordFragment;
import no.uio.cesar.View.ModuleView.ModuleFragment;
import no.uio.cesar.View.FeedView.FeedFragment;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.cesar.ViewModel.SampleViewModel;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {

    private RecordViewModel recordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences(Constant.STORAGE_NAME, Context.MODE_PRIVATE);

        String username = sharedPref.getString(Constant.USER_KEY_NAME, null);

        if (username == null) {
            startActivity(new Intent(this, LandingActivity.class));

            username = sharedPref.getString(Constant.USER_KEY_NAME, null);
            System.out.println(username);
        }

        // Inject the home fragment initially.
        Uti.commitFragmentTransaction(this, new FeedFragment());


        FloatingActionButton fab = findViewById(R.id.fab_record);
        fab.setOnClickListener(l -> {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            DialogFragment dialogFragment = new RecordFragment();
            dialogFragment.show(ft, "record_dialog");
        });


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new FeedFragment();
                    break;
                case R.id.navigation_profile:
                    selectedFragment = new ProfileFragment();
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = ModuleFragment.newInstance();
                    break;
            }

            Uti.commitFragmentTransaction(this, selectedFragment);

            return true;
        });

        /*
        SampleViewModel sampleViewModel = ViewModelProviders.of(this).get(SampleViewModel.class);

        sampleViewModel.getSamplesForRecord(40).observe(this, samples -> {
            System.out.println("DATA > " + samples);
        });

        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        recordViewModel.getAllRecords().observe(this, records -> {
            System.out.println(">>> new data " + records.size());
            for (Record r : records) {
                System.out.println(r);
            }
        });
        */
    }
}
