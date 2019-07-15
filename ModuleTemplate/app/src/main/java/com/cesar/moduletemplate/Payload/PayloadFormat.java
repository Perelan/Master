package com.cesar.moduletemplate.Payload;

import java.util.List;

public class PayloadFormat {
    private Record record;
    private List<Sample> samples;

    public PayloadFormat(Record record, List<Sample> samples) {
        this.record = record;
        this.samples = samples;
    }

    public Record getRecord() {
        return record;
    }

    public List<Sample> getSamples() {
        return samples;
    }
}
