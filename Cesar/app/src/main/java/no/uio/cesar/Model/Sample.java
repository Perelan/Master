package no.uio.cesar.Model;

import java.util.ArrayList;
import java.util.Date;

public class Sample {
    private int sample;
    private Date timestamp;

    public Sample(int sample, Date timestamp) {
        this.sample = sample;
        this.timestamp = timestamp;
    }

    public int getSample() {
        return sample;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
