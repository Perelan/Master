package no.uio.cesar.Dispatcher;

import android.os.Handler;
import android.util.Log;

public class ConnectivityHandler {
    private Handler handler;
    private Runnable runnable;

    private final int BASE_START_TIME = 20_000;

    private int wait, attempts;

    public ConnectivityHandler(Runnable runnable) {
        handler = new Handler();
        this.runnable = runnable;
        wait = BASE_START_TIME;
        attempts = 0;
    }

    public void start() {
        handler.postDelayed(this.runnable, wait);
    }

    public void stop() {
        handler.removeCallbacks(runnable);
    }

    public void retry() {
        Log.d("ConnectivityHandler", "Trying to reconnect, attempt: " + attempts);

        if (attempts++ > 5) wait += 10_000; // Increment with 10secs

        stop();
        start();
    }

    public void reset() {
        wait = BASE_START_TIME;
        attempts = 0;

        stop();
        start();
    }
}
