package no.uio.cesar.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Constant;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.View.HomeView.HomeFragment;
import no.uio.cesar.View.ModuleView.ModuleFragment;
import no.uio.cesar.View.RecordView.RecordFragment;
import no.uio.cesar.ViewModel.RecordViewModel;

public class MainActivity extends AppCompatActivity {

    private RecordViewModel recordViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences(Constant.STORAGE_NAME, Context.MODE_PRIVATE);

        String username = sharedPref.getString(Constant.USER_KEY_NAME, null);

        System.out.println(username);

        if (username == null) {
            startActivity(new Intent(this, LandingActivity.class));

            username = sharedPref.getString(Constant.USER_KEY_NAME, null);
            System.out.println(username);

        }

        // Inject the home fragment initially.
        Uti.commitFragmentTransaction(this, new ModuleFragment());

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle("Home");
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_dashboard:
                    setTitle("Modules");
                    selectedFragment = ModuleFragment.newInstance();
                    break;
                case R.id.navigation_notifications:
                    setTitle("Records");
                    selectedFragment = new RecordFragment();
                    break;
            }

            Uti.commitFragmentTransaction(this, selectedFragment);

            return true;
        });

        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        recordViewModel.getAllRecords().observe(this, records -> {
            System.out.println(">>> new data " + records.size());
        });
    }
}