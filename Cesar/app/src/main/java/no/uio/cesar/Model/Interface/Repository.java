package no.uio.cesar.Model.Interface;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.User;

public class Repository {
    private RecordDao recordDao;
    private LiveData<List<Record>> allRecords;

    private UserDao userDao;
    private int userCount;

    public Repository(Application application) {
        Database database = Database.getInstance(application);

        recordDao = database.recordDao();
        allRecords = recordDao.getAllRecords();

        userDao = database.userDao();
    }

    public void insertRecord(Record record) {
        new InsertRecordAsyncTask(recordDao).execute(record);
    }

    public void updateRecord(Record record) {
        // To be implemented
    }

    public void deleteRecord(Record record) {
        // To be implemented
        new DeleteRecordAsyncTask(recordDao).execute(record);
    }

    public void insertUser(User user) {
        new InsertUserAsyncTask(userDao).execute(user);
    }

    public int getUserCount() { return userCount; }

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

    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao userDao;

        private InsertUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            // Single record passed, thus accessing the only element
            userDao.insert(users[0]);

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
}
