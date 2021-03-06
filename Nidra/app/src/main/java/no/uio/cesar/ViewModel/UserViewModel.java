package no.uio.cesar.ViewModel;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import no.uio.cesar.Model.User;
import no.uio.cesar.Utils.Constant;

public class UserViewModel {

    private User user;
    private SharedPreferences pref;

    public UserViewModel(Context context) {
        pref = context.getSharedPreferences(Constant.STORAGE_NAME, Context.MODE_PRIVATE);

        String userString = pref.getString(Constant.USER_KEY, null);

        this.user = userString == null
                ? null
                : new Gson().fromJson(userString, User.class);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean deleteUser() {
        pref.edit().clear().apply();
        return false;
    }
}

