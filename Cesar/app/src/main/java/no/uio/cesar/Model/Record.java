package no.uio.cesar.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import no.uio.cesar.Utils.Converters;

@Entity(tableName = "record_table")
public class Record {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name, description;
    private long monitorTime;
    private float rating;
    private Date createdAt, updatedAt;

    private int nrSamples;

    public Record() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    public int getNrSamples() {
        return nrSamples;
    }

    public void setNrSamples(int nrSamples) {
        this.nrSamples = nrSamples;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getMonitorTime() {
        return monitorTime;
    }

    public void setMonitorTime(long monitorTime) {
        this.monitorTime = monitorTime;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", monitorTime=" + monitorTime +
                ", rating=" + rating +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", nrSamples=" + nrSamples +
                '}';
    }
}
