package com.github.kaktushose.lsmodmanager.utils;

import com.github.kaktushose.lsmodmanager.exceptions.ThreadExecutionException;
import javafx.application.Platform;

import java.util.concurrent.ThreadFactory;

public class WorkerThreadFactory implements ThreadFactory {

    private final String prefix;
    private int counter = 0;

    public WorkerThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, prefix + "-" + counter++);
        thread.setUncaughtExceptionHandler((t, e) -> Platform.runLater(() -> {
            throw new ThreadExecutionException(t.getName() + " had an uncaught exception", e);
        }));
        return thread;
    }
}
