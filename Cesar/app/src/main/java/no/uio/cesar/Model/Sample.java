package no.uio.cesar.Model;

import java.util.ArrayList;

public class Sample {
    private ArrayList<Integer> sample;
    private Long timestamp;

    public Sample(ArrayList<Integer> sample, Long timestamp) {
        this.sample = sample;
        this.timestamp = timestamp;
    }

    public ArrayList<Integer> getSample() {
        return sample;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
