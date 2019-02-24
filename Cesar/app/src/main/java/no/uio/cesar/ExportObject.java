package no.uio.cesar;

import java.util.List;

import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;

public class ExportObject {
    Record record;
    List<Sample> samples;

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
