package no.uio.cesar.ViewModel;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import no.uio.cesar.Model.Interface.Repository;
import no.uio.cesar.Model.Sample;

public class SampleViewModel extends AndroidViewModel {
    private Repository repository;

    public SampleViewModel(@NonNull Application application) {
        super(application);

        this.repository = new Repository(application);
    }

    public void insert(Sample sample) {
        repository.insertSample(sample);
    }

    public LiveData<List<Sample>> getSamplesForRecord(long id) {
        return repository.getSamplesForRecord(id);
    }
}
