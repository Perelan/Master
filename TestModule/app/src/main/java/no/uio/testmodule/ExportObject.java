package no.uio.testmodule;

import java.util.List;

public class ExportObject {
    private Record record;
    private List<Sample> samples;

    public ExportObject(Record record, List<Sample> samples) {
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
