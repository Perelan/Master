package no.uio.cesar.Utils;

import android.os.Handler;

public class ConnectivityHandler {
    private Handler handler;
    private Runnable runnable;

    private final int BASE_START_TIME = 10_000;

    private int wait;

    private int attempts;

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
        System.out.println("Trying to reconnect, attempt: " + attempts);
        wait += 10_000; // Increment with 10secs
        attempts++;

        stop();
        start();
    }

    public void restart() {
        wait = BASE_START_TIME;
        attempts = 0;

        stop();
        start();
    }
}
