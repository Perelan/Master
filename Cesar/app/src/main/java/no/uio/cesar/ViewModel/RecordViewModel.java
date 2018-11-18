package no.uio.cesar.ViewModel;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
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

    public void insert(Record record) {
        repository.insert(record);
    }

    public void update(Record record) {
        repository.update(record);
    }

    public void delete(Record record) {
        repository.delete(record);
    }

    public LiveData<List<Record>> getAllRecords() {
        return allRecords;
    }
}
