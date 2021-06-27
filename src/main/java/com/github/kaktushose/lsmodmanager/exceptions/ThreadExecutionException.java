package com.github.kaktushose.lsmodmanager.exceptions;

public class ThreadExecutionException extends RuntimeException {

    public ThreadExecutionException(String error, Throwable t) {
        super(error, t);
    }
}
