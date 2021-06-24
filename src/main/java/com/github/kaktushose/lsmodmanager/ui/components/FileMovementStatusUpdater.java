package com.github.kaktushose.lsmodmanager.ui.components;

import com.github.kaktushose.lsmodmanager.exceptions.FileOperationException;
import com.github.kaktushose.lsmodmanager.ui.controller.ProgressIndicatorController;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Path;

public class FileMovementStatusUpdater {

    private static final long checkInterval = 1000;
    private final ProgressIndicatorController controller;

    public FileMovementStatusUpdater(ProgressIndicatorController controller) {
        this.controller = controller;
    }

    public void monitor(Path directory, long desiredSize) {
        new Thread(() -> {
            synchronized (this) {
                File file = directory.toFile();
                try {
                    Platform.runLater(controller::show);
                    double progress;
                    do {
                        double currentSize = FileUtils.sizeOfDirectory(file);
                        final double percentage = 100 * currentSize / desiredSize;
                        Platform.runLater(() -> controller.update(percentage));
                        progress = percentage;
                        wait(checkInterval);
                    } while (progress < 100);
                } catch (InterruptedException e) {
                    throw new FileOperationException("File monitoring got interrupted!", e);
                }
            }
        }).start();
    }

    public void stop() {
        Platform.runLater(controller::close);
    }
}
