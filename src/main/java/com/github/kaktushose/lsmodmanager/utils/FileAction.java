package com.github.kaktushose.lsmodmanager.utils;

import java.util.function.Consumer;

public interface FileAction<T> {

    FileAction<T> onSuccess(Consumer<T> success);

    FileAction<T> onError(Runnable error);

}
