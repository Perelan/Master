package no.uio.cesar.View.ProfileView;


import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import no.uio.cesar.Model.User;
import no.uio.cesar.R;
import no.uio.cesar.ViewModel.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends DialogFragment {
    private static final String ARG_USER = "user";

    private User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        UserViewModel userViewModel = new UserViewModel(v.getContext());

        System.out.println("HERERER " + userViewModel.getUser());

        return v;
    }

}
