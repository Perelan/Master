package no.uio.cesar.Model;

public class Sensor {

    private String capAndId, type, metric, desc, freq;

    public Sensor(String capAndId, String type, String metric, String desc, String freq) {
        this.capAndId = capAndId;
        this.type = type;
        this.metric = metric;
        this.desc = desc;
        this.freq = freq;
    }

    public String getCapAndId() {
        return capAndId;
    }

    public String getType() {
        return type;
    }

    public String getMetric() {
        return metric;
    }

    public String getDesc() {
        return desc;
    }

    public String getFreq() {
        return freq;
    }
}
