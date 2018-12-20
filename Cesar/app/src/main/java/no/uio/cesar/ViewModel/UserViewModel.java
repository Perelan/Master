package no.uio.cesar.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import no.uio.cesar.Model.Interface.Repository;
import no.uio.cesar.Model.User;

public class UserViewModel extends AndroidViewModel {
    private Repository repository;

    private int userCount;

    public UserViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository(application);

        userCount = repository.getUserCount();
    }

    public void insert(User user) {
        repository.insertUser(user);
    }

    public void update(User user) {

    }

    public void delete(User user) {

    }

    public int getUserCount() {
        return userCount;
    }
}
