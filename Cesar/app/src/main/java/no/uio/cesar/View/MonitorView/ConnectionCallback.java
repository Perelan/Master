package no.uio.cesar.View.MonitorView;

import java.util.List;

public interface ConnectionCallback {
    void connected(List<String> publishers);
}
