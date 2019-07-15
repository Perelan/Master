package no.uio.testmodule;

import java.util.Date;

public class Sample {
    private int id;
    private long recordId;
    private String sample;
    private Date explicitTS, implicitTS;

    public Sample(long recordId) {
        this.recordId = recordId;
        this.implicitTS = new Date();
    }

    public int getId() {
        return id;
    }

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
}
