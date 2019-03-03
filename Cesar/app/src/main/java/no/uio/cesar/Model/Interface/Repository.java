package no.uio.cesar.Model.Interface;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.lifecycle.LiveData;
import no.uio.cesar.Model.Module;
import no.uio.cesar.Model.Payload;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;

public class Repository {

    public static final String TAG = "Repository";
    private RecordDao recordDao;
    private LiveData<List<Record>> allRecords;

    private SampleDao sampleDao;

    private ModuleDao moduleDao;
    private LiveData<List<Module>> allModules;

    public Repository(Application application) {
        Database database = Database.getInstance(application);

        recordDao = database.recordDao();
        allRecords = recordDao.getAllRecords();

        sampleDao = database.sampleDao();

        moduleDao = database.moduleDao();
        allModules = moduleDao.getAllModules();
    }

    /* Record */

    public void insertRecord(Record record, DatabaseCallback callback) {
        new InsertRecordAsyncTask(recordDao, callback).execute(record);
    }

    public void updateRecord(Record record) {
        // To be implemented
        new UpdateRecordAsyncTask(recordDao).execute(record);
    }

    public void deleteRecord(Record record) {
        new DeleteRecordAsyncTask(recordDao).execute(record);
    }

    /* Sample */

    // Change this to specificly Flow
    public void insertSample(Sample sample, String data) {
        Payload parse  = new Gson().fromJson(data, new TypeToken<Payload>(){}.getType());

        Log.d(TAG, "insertData: parse " + parse.getId() + " " + parse.getTime() + " " + parse.getValue());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS", Locale.getDefault());
        Date d = null;
        try {
            d = sdf.parse(parse.getTime());
            System.out.println(d);

        } catch(Exception e) {
            System.out.println("error on date parse: " + e);
        }

        sample.setExplicitTS(d);
        sample.setSample(parse.getValue());

        Log.d(TAG, "insertSample: " + sample);

        new InsertSampleAsyncTask(sampleDao).execute(sample);
    }

    public void updateSample(Sample sample) { throw new UnsupportedOperationException(); }

    public void deleteSample(Sample sample) { throw new UnsupportedOperationException(); }

    public LiveData<List<Sample>> getSamplesForRecord(long id) {
        return sampleDao.getSamplesForRecord(id);
    }

    /* Module */

    public void insertModule(Module module) {
        new InsertModuleAsyncTask(moduleDao).execute(module);
    }

    public void deleteModule(Module module) {
        new DeleteModuleAsyncTask(moduleDao).execute(module);
    }

    public LiveData<List<Record>> getAllRecords() {
        return allRecords;
    }

    public LiveData<List<Module>> getAllModules() {
        return allModules;
    }

    private static class InsertRecordAsyncTask extends AsyncTask<Record, Void, Void> {

        private RecordDao recordDao;
        private long generatedId;
        private DatabaseCallback callback;

        private InsertRecordAsyncTask(RecordDao recordDao, DatabaseCallback callback) {
            this.recordDao = recordDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Record... records) {
            // Single record passed, thus accessing the only element
            generatedId = recordDao.insert(records[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (callback != null) callback.onInsertGetRecordId(generatedId);
        }
    }

    private static class InsertSampleAsyncTask extends AsyncTask<Sample, Void, Void> {

        private SampleDao sampleDao;

        private InsertSampleAsyncTask(SampleDao sampleDao) { this.sampleDao = sampleDao; }

        @Override
        public Void doInBackground(Sample... samples) {

            Log.d(TAG, "doInBackground: Inserting in Sample: " + samples[0]);
            
            sampleDao.insert(samples[0]);

            return null;
        }
    }

    private static class InsertModuleAsyncTask extends AsyncTask<Module, Void, Void> {

        private ModuleDao moduleDao;

        private InsertModuleAsyncTask(ModuleDao moduleDao) { this.moduleDao = moduleDao; }

        @Override
        public Void doInBackground(Module... modules) {
            moduleDao.insert(modules[0]);

            return null;
        }
    }



    private static class UpdateRecordAsyncTask extends AsyncTask<Record, Void, Void> {

        private RecordDao recordDao;

        private UpdateRecordAsyncTask(RecordDao recordDao) {
            this.recordDao = recordDao;
        }

        @Override
        protected Void doInBackground(Record... records) {
            // Single record passed, thus accessing the only element
            records[0].setUpdatedAt(new Date());

            Log.d(TAG, "doInBackground: Updating");
            recordDao.update(records[0]);

            return null;
        }
    }

    private static class DeleteRecordAsyncTask extends AsyncTask<Record, Void, Void> {

        private RecordDao recordDao;

        private DeleteRecordAsyncTask(RecordDao recordDao) {
            this.recordDao = recordDao;
        }

        @Override
        protected Void doInBackground(Record... records) {
            // Single record passed, thus accessing the only element

            recordDao.delete(records[0]);

            return null;
        }
    }

    private static class DeleteModuleAsyncTask extends AsyncTask<Module, Void, Void> {

        private ModuleDao moduleDao;

        private DeleteModuleAsyncTask(ModuleDao moduleDao) { this.moduleDao = moduleDao; }

        @Override
        public Void doInBackground(Module... modules) {
            moduleDao.delete(modules[0]);

            return null;
        }
    }
}
