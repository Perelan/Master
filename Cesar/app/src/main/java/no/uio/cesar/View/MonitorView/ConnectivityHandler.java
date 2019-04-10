package no.uio.cesar.View.MonitorView;

import android.os.Handler;

class ConnectivityHandler {
    private Handler handler;
    private Runnable runnable;

    private final int BASE_START_TIME = 20_000;

    private int wait, attempts;

    ConnectivityHandler(Runnable runnable) {
        handler = new Handler();
        this.runnable = runnable;
        wait = BASE_START_TIME;
        attempts = 0;
    }

    void start() {
        handler.postDelayed(this.runnable, wait);
    }

    void stop() {
        handler.removeCallbacks(runnable);
    }

    void retry() {
        System.out.println("Trying to reconnect, attempt: " + attempts);
        if (attempts++ > 5) wait += 10_000; // Increment with 10secs

        stop();
        start();
    }

    void reset() {
        wait = BASE_START_TIME;
        attempts = 0;

        stop();
        start();
    }
}
