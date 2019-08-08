package no.uio.cesar.Utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.OpenableColumns;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

import androidx.core.content.FileProvider;
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

    public static void commitFragmentTransaction(FragmentActivity a, Fragment fragment, String tag) {
        a.getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(tag)
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
        for (String dataValue : dataValues) {
            //values[i] = Integer.parseInt(dataValues[i]);
            avg += Integer.parseInt(dataValue);
        }

        return avg / dataValues.length;
    }

    public static long calcElapsedTime(long offset) {
        return SystemClock.elapsedRealtime() - offset;
    }

    public static void shareFileIntent(Activity a, File file) {

        Uri fileUri = FileProvider.getUriForFile(a.getApplicationContext(), a.getApplicationContext().getPackageName() + ".provider", file);

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setType("text/*");
        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Share Records");
        intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri);
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentShareFile.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        a.startActivity(Intent.createChooser(intentShareFile, "Share Via"));
    }

    public static List<ExportObject> parseRecordFile(Activity a, Uri uri) {

        File f = getAbsoluteFilePath(a, uri);
        if (f == null) return null;

        Type IMPORT_TYPE = new TypeToken<List<ExportObject>>(){}.getType();

        List<ExportObject> listOfRecords = null;

        try {
            JsonReader reader = new JsonReader(new FileReader(f.getAbsoluteFile()));
            listOfRecords = new Gson().fromJson(reader, IMPORT_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listOfRecords;
    }


    private static File getAbsoluteFilePath(Activity a, Uri uri) {

        String uriString = uri.toString();

        if (uriString.startsWith("content://")) {
            try (Cursor cursor = a.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    return new File(dir, name);
                }
            }
        } else if (uriString.startsWith("file://")) {
        }

        return null;
    }
}
