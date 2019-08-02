package no.uio.cesar.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Constant;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.View.ModuleView.ModuleFragment;
import no.uio.cesar.View.FeedView.FeedFragment;
import no.uio.cesar.ViewModel.RecordViewModel;


public class MainActivity extends AppCompatActivity {

    private RecordViewModel recordViewModel;

    private FeedFragment feedFragment;
    private ModuleFragment moduleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences(Constant.STORAGE_NAME, Context.MODE_PRIVATE);

        String userString = sharedPref.getString(Constant.USER_KEY, null);

        if (userString == null || userString.isEmpty()) {
            startActivity(new Intent(this, LandingActivity.class));
            finish();
        }

        // Inject the home fragment initially.
        Uti.commitFragmentTransaction(this, new FeedFragment());

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (feedFragment == null) feedFragment = new FeedFragment();
                    selectedFragment = feedFragment;
                    Uti.commitFragmentTransaction(this, selectedFragment);
                    break;
                case R.id.navigation_record:
                    startActivity(new Intent(this, MonitorActivity.class));
                    return false;
                case R.id.navigation_notifications:
                    if (moduleFragment == null) moduleFragment = new ModuleFragment();
                    selectedFragment = moduleFragment;
                    Uti.commitFragmentTransaction(this, selectedFragment);
                    break;
            }
            return true;
        });
    }
}
