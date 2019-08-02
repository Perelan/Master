package no.uio.cesar.Dispatcher;

import java.util.List;

public interface ConnectionCallback {
    void connected(List<String> publishers);
}
