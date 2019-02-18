package no.uio.cesar.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;


@Entity(tableName = "sample_table", foreignKeys = @ForeignKey(entity = Record.class,
                                                                parentColumns = "id",
                                                                childColumns = "recordId",
                                                                onDelete = CASCADE))
public class Sample {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long recordId;
    private String sample;
    private Date explicitTS, implicitTS;

    public Sample(long recordId) {
        this.recordId = recordId;
        this.implicitTS = new Date();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() { return id; }

    public long getRecordId() {
        return recordId;
    }

    public String getSample() {
        return sample;
    }

    public Date getExplicitTS() {
        return explicitTS;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public void setExplicitTS(Date explicitTS) {
        this.explicitTS = explicitTS;
    }

    public Date getImplicitTS() {
        return implicitTS;
    }

    public void setImplicitTS(Date implicitTS) {
        this.implicitTS = implicitTS;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Id: %d - recordid: %d - sample: %s", id, recordId, sample);
    }
}
