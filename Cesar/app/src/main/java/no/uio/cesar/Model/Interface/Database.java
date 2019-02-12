package no.uio.cesar.Model.Interface;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import no.uio.cesar.Model.Module;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.Utils.Constant;
import no.uio.cesar.Utils.Converters;

@androidx.room.Database(entities = {Record.class, Sample.class, Module.class}, version = Constant.DATABASE_VERSION)
@TypeConverters({Converters.class})
public abstract class Database extends RoomDatabase {

    private static Database instance;

    public abstract RecordDao recordDao();
    public abstract SampleDao sampleDao();
    public abstract ModuleDao moduleDao();

    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    Database.class,
                    Constant.DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    //.addCallback(roomCallback)
                    .build();
        }

        return instance;
    }

    /*
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDummyDataAsyncTask(instance).execute();
        }

    };

    private static class PopulateDummyDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private RecordDao recordDao;

        PopulateDummyDataAsyncTask(Database db) {
            recordDao = db.recordDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ArrayList<Sample> hr = new ArrayList<>();
            hr.add(new Sample("1", null, new Date()));

            recordDao.insert(new Record("test1", hr));

            return null;
        }
    }
    */
}
