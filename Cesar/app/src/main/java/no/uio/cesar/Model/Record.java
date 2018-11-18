package no.uio.cesar.Model;

import java.util.ArrayList;
import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_table")
public class Record {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private Date createdAt, updatedAt;

    private ArrayList<Sample> hr;

    public Record(String name, ArrayList<Sample> hr) {
        this.name = name;
        this.hr = new ArrayList<>();
        this.hr = hr;

        createdAt = new Date();
        updatedAt = new Date();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Sample> getHr() {
        return hr;
    }
}
