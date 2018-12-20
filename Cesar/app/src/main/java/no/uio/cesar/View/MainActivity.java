package no.uio.cesar.View;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.R;
import no.uio.cesar.View.HomeView.HomeFragment;
import no.uio.cesar.View.RecordView.RecordFragment;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.cesar.ViewModel.UserViewModel;

public class MainActivity extends AppCompatActivity {

    private RecordViewModel recordViewModel;
    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        if (userViewModel.getUserCount() == 0) {
            System.out.println("Here");

            startActivity(new Intent(this, LandingActivity.class));

        }

        // Inject the home fragment initially.
        commitFragmentTransaction(new HomeFragment());

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle("Home");
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_dashboard:
                    setTitle("Records");
                    selectedFragment = new RecordFragment();
                    break;
                case R.id.navigation_notifications:
                    setTitle("Modules");
                    selectedFragment = new RecordFragment();
                    break;
            }

            commitFragmentTransaction(selectedFragment);

            return true;
        });

        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        recordViewModel.getAllRecords().observe(this, records -> {
            System.out.println(">>> new data " + records.size());
        });
    }

    private void commitFragmentTransaction(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
