package com.github.kaktushose.lsmodmanager.exceptions;

public class FileOperationException extends RuntimeException {

    public FileOperationException(String error, Throwable t) {
        super(error, t);
    }
}
