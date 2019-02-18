package no.uio.cesar.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;

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

    // Example data: Time=700ms, deltaT=100, data=1797,1772,1791,1786,1788,1781,1790
    public static int extractFlowData(String data) {
        String[] split = data.split(", ");
        String[] dataValues = split[2].split("=")[1].split(",");

        //int[] values = new int[dataValues.length];
        int avg = 0;
        for (int i = 0; i < dataValues.length; i++) {
            //values[i] = Integer.parseInt(dataValues[i]);
            avg += Integer.parseInt(dataValues[i]);
        }

        return avg / dataValues.length;
    }

    public static long calcElapsedTime(long offset) {
        return SystemClock.elapsedRealtime() - offset;
    }
}
