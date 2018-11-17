package no.uio.cesar.Model;

import java.util.ArrayList;
import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_table")
public class Record {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name = "Sleep #" + id;

    private ArrayList<Integer> hr;
    private ArrayList<Integer> respiration;

    private Date timestamp;

    public Record(ArrayList<Integer> hr, ArrayList<Integer> respiration) {
        this.hr = hr;
        this.respiration = respiration;
        hr = new ArrayList<>();
        respiration = new ArrayList<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Integer> getHr() {
        return hr;
    }

    public ArrayList<Integer> getRespiration() {
        return respiration;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
