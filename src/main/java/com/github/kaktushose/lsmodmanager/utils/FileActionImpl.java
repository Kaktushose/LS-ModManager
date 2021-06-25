package com.github.kaktushose.lsmodmanager.utils;

import java.util.function.Consumer;

public class FileActionImpl<T> implements FileAction<T> {

    private Consumer<T> success;
    private Runnable error;

    public Consumer<T> getSuccessConsumer() {
        return success;
    }

    public Runnable getErrorConsumer() {
        return error;
    }

    @Override
    public FileAction<T> onSuccess(Consumer<T> success) {
        this.success = success;
        return this;
    }

    @Override
    public FileAction<T> onError(Runnable error) {
        this.error = error;
        return this;
    }
}
