package no.uio.cesar.Model.Interface;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import no.uio.cesar.Model.Record;

public class Repository {
    private RecordDao recordDao;
    private LiveData<List<Record>> allRecords;

    public Repository(Application application) {
        RecordDatabase database = RecordDatabase.getInstance(application);

        recordDao = database.recordDao();
        allRecords = recordDao.getAllRecords();
    }

    public void insert(Record record) {
        new InsertRecordAsyncTask(recordDao).execute(record);
    }

    public void update(Record record) {
        // To be implemented
    }

    public void delete(Record record) {
        // To be implemented
    }

    public LiveData<List<Record>> getAllRecords() {
        return allRecords;
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
}
