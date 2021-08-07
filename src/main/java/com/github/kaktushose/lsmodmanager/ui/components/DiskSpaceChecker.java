package com.github.kaktushose.lsmodmanager.ui.components;

import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.services.model.Modpack;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class DiskSpaceChecker {

    private final SettingsService settingsService;

    public DiskSpaceChecker(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public boolean checkCreation(List<File> files) {
        long size = files.stream().mapToLong(File::length).sum();
        return check(size, settingsService.getModpackPath());
    }

    public boolean checkLoading(Modpack modpack) {
        long size = modpack.getMods().stream().mapToLong(File::length).sum();
        return check(size, settingsService.getFsPath());
    }

    public boolean checkMoving(String oldDirectory, String newDirectory) {
        if (oldDirectory.isEmpty()) {
            return true;
        }
        long size = FileUtils.sizeOfDirectory(new File(oldDirectory));
        return check(size, newDirectory);
    }

    public boolean checkEditing(Modpack oldValue, Modpack newValue) {
        long size = newValue.getMods().stream().filter(file -> !oldValue.getMods().contains(file)).mapToLong(File::length).sum();
        size = size - oldValue.getMods().stream().filter(file -> !newValue.getMods().contains(file)).mapToLong(File::length).sum();

        if (!check(size, settingsService.getModpackPath())) {
            return false;
        }
        if (settingsService.getLoadedModpackId() == oldValue.getId()) {
            return check(size, settingsService.getModpackPath());
        }

        return true;
    }

    private boolean check(long size, String path) {
        File target = new File(path);
        boolean enoughSpace = (target.getUsableSpace() + size) < target.getTotalSpace();
        if (!enoughSpace) {
            String available = FileUtils.byteCountToDisplaySize(target.getUsableSpace());
            String required = FileUtils.byteCountToDisplaySize(size);
            ResourceBundle bundle = ResourceBundle.getBundle("bundles.Bundle", Locale.getDefault());
            Alerts.displayErrorMessage(bundle.getString("alerts.space.title"),
                    String.format(bundle.getString("alerts.space.message"), available, required)
            );
            return false;
        }
        return true;
    }
}
