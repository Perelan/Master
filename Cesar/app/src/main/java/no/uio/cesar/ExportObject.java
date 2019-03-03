package no.uio.cesar;

import java.util.List;

import no.uio.cesar.Model.Record;
import no.uio.cesar.Model.Sample;
import no.uio.cesar.Model.User;

public class ExportObject {
    private User user;
    private Record record;
    private List<Sample> samples;

    public ExportObject(User user, Record record, List<Sample> samples) {
        this.user = user;
        this.record = record;
        this.samples = samples;
    }

    public User getUser() {
        return user;
    }

    public Record getRecord() {
        return record;
    }

    public List<Sample> getSamples() {
        return samples;
    }
}
