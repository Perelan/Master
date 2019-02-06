package no.uio.cesar.View;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sensordroid.MainServiceConnection;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.DSDService;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Constant;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.View.HomeView.HomeFragment;
import no.uio.cesar.View.ModuleView.ModuleFragment;
import no.uio.cesar.View.RecordView.RecordFragment;
import no.uio.cesar.ViewModel.RecordViewModel;

public class MainActivity extends AppCompatActivity {

    private RecordViewModel recordViewModel;

    public MainServiceConnection msc;

    private ServiceConnection serviceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msc = MainServiceConnection.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serviceCon != null) {
            unbindService(serviceCon);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CESAR", "onCreate: PENCHOD");
        System.out.println("HAHAHAH");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainServiceConnection.class.getName());
        intent.setAction("com.sensordroid.ADD_DRIVER");
        intent.setPackage("com.sensordroid");
        bindService(intent, serviceCon, Service.BIND_AUTO_CREATE);

        SharedPreferences sharedPref = getSharedPreferences(Constant.STORAGE_NAME, Context.MODE_PRIVATE);

        String username = sharedPref.getString(Constant.USER_KEY_NAME, null);

        System.out.println(username);

        if (username == null) {
            startActivity(new Intent(this, LandingActivity.class));

            username = sharedPref.getString(Constant.USER_KEY_NAME, null);
            System.out.println(username);
        }

        // Inject the home fragment initially.
        Uti.commitFragmentTransaction(this, new HomeFragment());

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


        findViewById(R.id.test).setOnClickListener(view -> {
            if (msc != null) {
                try {
                    System.out.println(msc.getPublishers());

                    List<String> publishers = msc.getPublishers();
                    if (publishers.isEmpty()) return;

                    String s = publishers.get(0).split(",")[0];

                    System.out.println(msc.Subscribe(s, 0, getPackageName(), DSDService.class.getName()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.test2).setOnClickListener(view -> {
            if (msc != null) {
                try {
                    List<String> publishers = msc.getPublishers();

                    String s = publishers.get(0).split(",")[0];
                    System.out.println(msc.Unsubscribe(s, DSDService.class.getName()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
