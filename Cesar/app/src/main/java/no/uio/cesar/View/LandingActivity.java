package no.uio.cesar.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.Locale;

import androidx.cardview.widget.CardView;
import no.uio.cesar.Model.User;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Constant;

public class LandingActivity extends AppCompatActivity {
    private FloatingActionButton nextButton, prevButton;
    private CardView getStarted;

    private ViewFlipper viewFlipper;

    private SeekBar sbWeight, sbHeight;
    private EditText name;
    private RadioGroup radioGroup;
    private RadioButton gender;
    private TextView tvWeight, tvHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        nextButton = findViewById(R.id.next);
        prevButton = findViewById(R.id.prev);

        getStarted = findViewById(R.id.get_started);
        getStarted.setOnClickListener(view -> {
            gender = findViewById(radioGroup.getCheckedRadioButtonId());

            storeUserData();
            finish();
        });

        viewFlipper = findViewById(R.id.view_flipper);
        radioGroup = findViewById(R.id.gender_group);

        sbWeight = findViewById(R.id.sb_weight);
        sbHeight = findViewById(R.id.sb_height);

        tvWeight = findViewById(R.id.weight_text);
        tvHeight = findViewById(R.id.height_text);

        name = findViewById(R.id.edittext_name);

        sbHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvHeight.setText(String.format(Locale.getDefault(), "%d cm", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvWeight.setText(String.format(Locale.getDefault(), "%d kg", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void previousView(View v) {

        if (viewFlipper.getDisplayedChild() != 0) {
            viewFlipper.showPrevious();
        }

        if (viewFlipper.getDisplayedChild() == 0) {
            prevButton.hide();
        }

        if (nextButton.isOrWillBeHidden()) {
            nextButton.show();
        }

        if (getStarted.isShown()) {
            getStarted.setVisibility(View.GONE);
        }
    }

    public void nextView(View v) {
        viewFlipper.showNext();

        if (viewFlipper.getDisplayedChild() == viewFlipper.getChildCount() - 1) {
            nextButton.hide();
            getStarted.setVisibility(View.VISIBLE);
        }

        if (prevButton.isOrWillBeHidden()) {
            prevButton.show();
        }
    }

    // TODO: switch to user model
    public void storeUserData() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constant.STORAGE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        User user = new User(
                name.getText().toString(),
                gender.getText().toString(),
                sbHeight.getProgress(),
                sbWeight.getProgress(),
                10);

        editor.putString(Constant.USER_KEY_NAME, name.getText().toString());
        editor.putString(Constant.USER_KEY_GENDER, gender.getText().toString());
        editor.putInt(Constant.USER_KEY_HEIGHT, sbHeight.getProgress());
        editor.putInt(Constant.USER_KEY_WEIGHT, sbWeight.getProgress());
        editor.putLong(Constant.USER_KEY_CREATED, new Date().getTime());

        editor.apply();
    }
}
