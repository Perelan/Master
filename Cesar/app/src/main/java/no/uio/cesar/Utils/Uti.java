package no.uio.cesar.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import no.uio.cesar.ExportObject;
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

    public static File writeToInternalStorage(Context context, String data) {
        File folder = new File(context.getFilesDir(), "cesar");

        if (!folder.exists()) {
            folder.mkdir();
        }

        try {
            File file = new File(folder, "record_" + new Date().getTime() + ".json");
            FileWriter writer = new FileWriter(file);

            System.out.println("DATA " + data);
            writer.write(data);
            writer.flush();
            writer.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void shareFileIntent(Activity a, File file) {

        System.out.println("HERERER " + file.getName());

        Uri fileUri = FileProvider.getUriForFile(a.getApplicationContext(), a.getApplicationContext().getPackageName() + ".provider", file);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setType("text/*");
        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Share Records");
        intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri);
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        a.startActivity(Intent.createChooser(intentShareFile, "Share Via"));
    }

    public static void parseRecordFile(Context context, String data) {

        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(data));

        File f = new File(fileUri.getPath());

        Type IMPORT_TYPE = new TypeToken<List<ExportObject>>(){}.getType();

        try {
            JsonReader reader = new JsonReader(new FileReader(f.getAbsoluteFile()));
            List<ExportObject> listofrecords = new Gson().fromJson(reader, IMPORT_TYPE);

            System.out.println("HERERE " + listofrecords.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
