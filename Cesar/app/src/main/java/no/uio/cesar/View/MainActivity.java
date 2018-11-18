package no.uio.cesar.View;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.R;
import no.uio.cesar.View.HomeView.HomeFragment;
import no.uio.cesar.View.RecordView.RecordFragment;
import no.uio.cesar.ViewModel.RecordViewModel;

public class MainActivity extends AppCompatActivity {

    private RecordViewModel recordViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inject the home fragment initially.
        commitFragmentTransaction(new HomeFragment());

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = new RecordFragment();
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = new RecordFragment();
                    break;
            }

            commitFragmentTransaction(selectedFragment);

            return true;
        });

        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        recordViewModel.getAllRecords().observe(this, records -> {
            System.out.println(">>> new data");

        });
    }

    private void commitFragmentTransaction(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


}
