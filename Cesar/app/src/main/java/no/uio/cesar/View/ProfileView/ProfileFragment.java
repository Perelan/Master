package no.uio.cesar.View.ProfileView;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Locale;

import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.Model.User;
import no.uio.cesar.R;
import no.uio.cesar.View.LandingActivity;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.cesar.ViewModel.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends DialogFragment {
    private static final String ARG_USER = "user";

    private UserViewModel userViewModel;
    private User currentUser;

    private TextView tvName, tvAge, tvHeight, tvWeight, tvGender;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        userViewModel = new UserViewModel(v.getContext());
        RecordViewModel recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        currentUser = userViewModel.getUser();

        tvName = v.findViewById(R.id.user_name);
        tvName.setText(currentUser.getName());

        tvAge = v.findViewById(R.id.user_age);
        tvAge.setText(String.format(Locale.getDefault(), "%d", currentUser.getAge()));

        tvHeight = v.findViewById(R.id.user_height);
        tvHeight.setText(String.format(Locale.getDefault(), "%d cm", currentUser.getHeight()));

        tvWeight = v.findViewById(R.id.user_weight);
        tvWeight.setText(String.format(Locale.getDefault(), "%d kg", currentUser.getWeight()));

        tvGender = v.findViewById(R.id.user_gender);
        tvGender.setText(currentUser.getGender());

        View delete = v.findViewById(R.id.user_delete);
        delete.setOnClickListener(l -> {
            recordViewModel.getAllRecords().observe(this, records -> {

                if (records.isEmpty()) {
                    deleteUserAndRestart();
                    return;
                }

                for (int i = 0; i < records.size(); i++) {
                    recordViewModel.delete(records.get(i));

                    if (i == records.size() - 1) {
                        deleteUserAndRestart();
                    }
                }
            });
        });

        return v;
    }

    public void deleteUserAndRestart() {
        userViewModel.deleteUser();
        getActivity().finish();
        startActivity(new Intent(getActivity(), LandingActivity.class));
    }

}
