package no.uio.cesar.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Date;
import java.util.Locale;

import androidx.cardview.widget.CardView;
import no.uio.cesar.Model.User;
import no.uio.cesar.R;
import no.uio.cesar.Utils.Constant;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton nextButton, prevButton;
    private CardView getStarted;

    private ViewFlipper viewFlipper;

    private SeekBar sbWeight, sbHeight;
    private EditText etName, etAge;
    private TextView tvWeight, tvHeight;
    private ImageView ivMale, ivFemale;

    private String gender = "Male"; // Default value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        nextButton = findViewById(R.id.next);
        prevButton = findViewById(R.id.prev);

        getStarted = findViewById(R.id.get_started);
        getStarted.setOnClickListener(this);

        viewFlipper = findViewById(R.id.view_flipper);

        sbWeight = findViewById(R.id.sb_weight);
        sbHeight = findViewById(R.id.sb_height);

        tvWeight = findViewById(R.id.weight_text);
        tvHeight = findViewById(R.id.height_text);

        ivMale = findViewById(R.id.gender_male);
        ivMale.setOnClickListener(this);
        ivFemale = findViewById(R.id.gender_female);
        ivFemale.setOnClickListener(this);

        etName = findViewById(R.id.edittext_name);
        etAge = findViewById(R.id.edittext_age);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gender_male:
                gender = "Male";
                ivMale.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                ivFemale.setBackgroundColor(0);
                break;
            case R.id.gender_female:
                gender = "Female";
                ivFemale.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                ivMale.setBackgroundColor(0);
                break;
            case R.id.get_started:
                if (validateInput()) {
                    Toast.makeText(this, "Name or age is not filled out, please do so", Toast.LENGTH_SHORT).show();

                } else {
                    storeUserData();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
        }
    }

    // TODO: switch to user model
    public void storeUserData() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constant.STORAGE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        User user = new User(
                etName.getText().toString(),
                gender,
                sbHeight.getProgress(),
                sbWeight.getProgress(),
                Integer.parseInt(etAge.getText().toString()));
        editor.putString(Constant.USER_KEY, new Gson().toJson(user));

        editor.apply();
    }

    public boolean validateInput() {
        return (etAge.getText().toString().isEmpty() || etName.getText().toString().isEmpty());
    }
}
