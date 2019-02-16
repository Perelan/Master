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

    public static long[] splitSecondsToHMS(long time) {
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return new long[] { hours % 24, minutes % 60, seconds % 60 };
    }
}
