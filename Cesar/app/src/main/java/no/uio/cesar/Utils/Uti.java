package no.uio.cesar.Utils;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import no.uio.cesar.R;

public class Uti {
    public static void commitFragmentTransaction(FragmentActivity a, Fragment fragment) {
        a.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
