package no.uio.cesar.ViewModel;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import no.uio.cesar.Model.Interface.Repository;
import no.uio.cesar.Model.Module;

public class ModuleViewModel extends AndroidViewModel {
    private Repository repository;

    public ModuleViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository(application);
    }

    public void insert(Module module) { repository.insertModule(module); }

    public void delete(Module module) { repository.deleteModule(module); }

    public LiveData<List<Module>> getAllModules() { return repository.getAllModules(); }
}
