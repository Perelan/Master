package no.uio.cesar.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.ViewFlipper;

import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.Model.User;
import no.uio.cesar.R;
import no.uio.cesar.ViewModel.UserViewModel;

public class LandingActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private SeekBar sbWeight, sbHeight;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        viewFlipper = findViewById(R.id.view_flipper);

        sbWeight = findViewById(R.id.sb_weight);
        sbHeight = findViewById(R.id.sb_height);
    }

    public void previousView(View v) {

        if (viewFlipper.getDisplayedChild() != 0) {
            viewFlipper.showPrevious();
        }
    }

    public void nextView(View v) {
        viewFlipper.showNext();

        if (viewFlipper.getDisplayedChild() == viewFlipper.getChildCount() - 1) {
            System.out.println("Weight: " + sbWeight.getProgress());
            System.out.println("Height: " + sbHeight.getProgress());

            createUser();
        }
    }


    private void createUser() {
        User user = new User("peter", 5, 5, 'F');
    }
}
