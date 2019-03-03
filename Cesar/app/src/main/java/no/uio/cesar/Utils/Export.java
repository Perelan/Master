package no.uio.cesar.Utils;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.ExportObject;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.Model.User;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.cesar.ViewModel.SampleViewModel;
import no.uio.cesar.ViewModel.UserViewModel;

public class Export {

    public static void export(Fragment f, Record record) {

        UserViewModel userViewModel = new UserViewModel(f.getContext());
        SampleViewModel sampleViewModel = ViewModelProviders.of(f).get(SampleViewModel.class);
        sampleViewModel.getSamplesForRecord(record.getId()).observe(f, samples -> {

            ArrayList<ExportObject> listOfExportObjects = new ArrayList<>();
            listOfExportObjects.add(new ExportObject(userViewModel.getUser(), record, samples));

            String exportString = new Gson().toJson(listOfExportObjects);

            File file = Uti.writeToInternalStorage(f.getContext(), exportString);

            Uti.shareFileIntent(f.getActivity(), file);
        });
    }

    // Todo: fix this logic
    public static void exportAll(Fragment f) {
        UserViewModel userViewModel = new UserViewModel(f.getContext());
        RecordViewModel recordViewModel = ViewModelProviders.of(f).get(RecordViewModel.class);
        SampleViewModel sampleViewModel = ViewModelProviders.of(f).get(SampleViewModel.class);

        recordViewModel.getAllRecords().observe(f, records -> {

            ArrayList<ExportObject> listOfExportObjects = new ArrayList<>();

            System.out.println(records.size());

            for (int i = 0; i < records.size(); i++) {
                int finalI = i;
                sampleViewModel.getSamplesForRecord(records.get(i).getId()).observe(f, samples -> {
                    listOfExportObjects.add(new ExportObject(userViewModel.getUser(), records.get(finalI), samples));

                    if (finalI == records.size() - 1) {
                        String exportString = new Gson().toJson(listOfExportObjects);

                        System.out.println("HERE " + exportString);

                        File file = Uti.writeToInternalStorage(f.getContext(), exportString);

                        Uti.shareFileIntent(f.getActivity(), file);
                    }
                });
            }

        });
    }
}
