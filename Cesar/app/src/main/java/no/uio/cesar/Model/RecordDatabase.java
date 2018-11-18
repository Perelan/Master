package no.uio.cesar.Model;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import no.uio.cesar.Utils.Converters;

@Database(entities = {Record.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class RecordDatabase extends RoomDatabase {

    private static RecordDatabase instance;

    public abstract RecordDao recordDao();

    public static synchronized RecordDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    RecordDatabase.class,"record_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDummyDataAsyncTask(instance).execute();
        }
    };

    private static class PopulateDummyDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private RecordDao recordDao;

        PopulateDummyDataAsyncTask(RecordDatabase db) {
            recordDao = db.recordDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Integer> hr = new ArrayList<>();
            hr.add(100);

            ArrayList<Integer> respiration = new ArrayList<>();
            respiration.add(1000);

            recordDao.insert(new Record("test1"));
            return null;
        }
    }
}
