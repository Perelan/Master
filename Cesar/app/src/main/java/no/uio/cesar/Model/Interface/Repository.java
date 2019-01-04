package no.uio.cesar.Model.Interface;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import no.uio.cesar.Model.Module;
import no.uio.cesar.Model.Record;

public class Repository {
    private RecordDao recordDao;
    private LiveData<List<Record>> allRecords;

    private ModuleDao moduleDao;
    private LiveData<List<Module>> allModules;

    public Repository(Application application) {
        Database database = Database.getInstance(application);

        recordDao = database.recordDao();
        allRecords = recordDao.getAllRecords();

        moduleDao = database.moduleDao();
        allModules = moduleDao.getAllModules();

    }

    public void insertRecord(Record record) {
        new InsertRecordAsyncTask(recordDao).execute(record);
    }

    public void updateRecord(Record record) {
        // To be implemented
    }

    public void deleteRecord(Record record) {
        new DeleteRecordAsyncTask(recordDao).execute(record);
    }

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

        private InsertRecordAsyncTask(RecordDao recordDao) {
            this.recordDao = recordDao;
        }

        @Override
        protected Void doInBackground(Record... records) {
            // Single record passed, thus accessing the only element
            recordDao.insert(records[0]);

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
