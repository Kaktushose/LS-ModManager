package com.github.kaktushose.lsmodmanager.utils;

public class FileActionImpl implements FileAction {

    private Runnable success;

    public Runnable getSuccessConsumer() {
        return success;
    }

    @Override
    public FileAction onSuccess(Runnable success) {
        this.success = success;
        return this;
    }
}
