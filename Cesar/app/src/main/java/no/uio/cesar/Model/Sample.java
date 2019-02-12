package no.uio.cesar.Model;

import java.util.ArrayList;
import java.util.Date;

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

    public Sample(long recordId, String sample, Date explicitTS) {
        this.recordId = recordId;
        this.sample = sample;
        this.explicitTS = explicitTS;
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

    public Date getImplicitTS() {
        return implicitTS;
    }

    public void setImplicitTS(Date implicitTS) {
        this.implicitTS = implicitTS;
    }
}
