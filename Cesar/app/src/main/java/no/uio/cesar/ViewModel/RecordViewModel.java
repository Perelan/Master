package no.uio.cesar.ViewModel;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import no.uio.cesar.Model.Interface.DatabaseCallback;
import no.uio.cesar.Model.Payload;
import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Interface.Repository;

public class RecordViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Record>> allRecords;

    public RecordViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository(application);
        allRecords = repository.getAllRecords();
    }

    public void insert(Record record, DatabaseCallback callback) {
        repository.insertRecord(record, callback);
    }

    public void update(Record record) {
        repository.updateRecord(record);
    }

    public void delete(Record record) {
        repository.deleteRecord(record);
    }

    public LiveData<List<Record>> getAllRecords() {
        return allRecords;
    }
}
