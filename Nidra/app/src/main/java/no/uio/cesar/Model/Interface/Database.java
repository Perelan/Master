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

    static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    Database.class,
                    Constant.DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    //.addCallback(roomCallback)
                    .build();
        }

        return instance;
    }
}
