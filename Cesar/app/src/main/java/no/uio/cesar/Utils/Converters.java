package no.uio.cesar.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;

import androidx.room.TypeConverter;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.Model.User;

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Sample> list) {
        return list == null ? null : new Gson().toJson(list);
    }

    @TypeConverter
    public static ArrayList<Sample> fromString(String value) {
        return new Gson().fromJson(value, new TypeToken<ArrayList<Sample>>(){}.getType());
    }

    @TypeConverter
    public static String fromUser(User user) {
        return user == null ? null : new Gson().toJson(user);
    }

    @TypeConverter
    public static User fromUserString(String value) {
        return new Gson().fromJson(value, User.class);
    }
}
