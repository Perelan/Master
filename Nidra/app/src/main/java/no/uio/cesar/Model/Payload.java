package no.uio.cesar.Model;

import androidx.annotation.NonNull;

public class Payload {

    private String id, value, time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @NonNull
    @Override
    public String toString() {
        return "Payload{" +
                "id='" + id + '\'' +
                ", value='" + value + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

}
