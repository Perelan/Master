package no.uio.cesar.Utils;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.ViewModel.SampleViewModel;

public class Export {

    private static class ExportObject {
        Record record;
        List<Sample> samples;

        public ExportObject(Record record, List<Sample> samples) {
            this.record = record;
            this.samples = samples;
        }

        public Record getRecord() {
            return record;
        }

        public List<Sample> getSamples() {
            return samples;
        }
    }

    public static void export(Fragment f, Context context, Record record) {

        SampleViewModel sampleViewModel = ViewModelProviders.of(f).get(SampleViewModel.class);
        sampleViewModel.getSamplesForRecord(record.getId()).observe(f, samples -> {

            ArrayList<ExportObject> listOfExportObjects = new ArrayList<>();
            listOfExportObjects.add(new ExportObject(record, samples));

            String exportString = new Gson().toJson(listOfExportObjects);

            File file = Uti.writeToInternalStorage(context, exportString);

            Uti.shareFileIntent(f.getActivity(), file);
        });

    }

    public static void exportAll() {

    }

}
