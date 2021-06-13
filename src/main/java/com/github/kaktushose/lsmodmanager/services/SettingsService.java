package com.github.kaktushose.lsmodmanager.services;

import com.github.kaktushose.lsmodmanager.services.model.Settings;
import com.github.kaktushose.lsmodmanager.util.Constants;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);
    private final Gson gson;
    private final File settingsFile;
    private Settings settings;

    public SettingsService() {
        gson = new Gson();
        settingsFile = new File(Constants.SETTINGS_PATH);
    }

    public void loadSettings() {
        try {
            if (!settingsFile.exists()) {
                new File(Constants.APP_PATH).mkdir();
                settingsFile.createNewFile();
                settings = new Settings();
                saveSettings();
                log.warn("No settings file found! Created a new one!");
                return;
            }

            JsonReader jsonReader = new JsonReader(new FileReader(settingsFile));
            settings = gson.fromJson(jsonReader, Settings.class);
            jsonReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("There was an error loading the settings file!", e);
        }
    }

    private void saveSettings() {
        try (FileWriter fileWriter = new FileWriter(settingsFile)) {
            gson.toJson(settings, fileWriter);
        } catch (IOException e) {
            throw new RuntimeException("There was an error saving the settings file!", e);
        }
    }

    public boolean findFsPath() {
        if (!getFsPath().isEmpty()) {
            return true;
        }

        for (int i = 19; i != 11; i -= 2) {
            String path = Constants.MY_GAMES + "\\FarmingSimulator20" + i;
            if (Files.isDirectory(Path.of(path))) {
                setFsPath(path);
                return true;
            }
        }
        return false;
    }

    public String getFsPath() {
        return settings.getFsPath();
    }

    public void setFsPath(String fsPath) {
        log.debug("Value \"fsPath\" updated. Old value \"{}\" new value \"{}\"", settings.getFsPath(), fsPath);
        settings.setFsPath(fsPath);
        saveSettings();
    }

    public String getModpackPath() {
        return settings.getModpackPath();
    }

    public void setModpackPath(String modpacksPath) {
        log.debug("Value \"modpackPath\" updated. Old value \"{}\" new value \"{}\"", settings.getModpackPath(), modpacksPath);
        settings.setModpackPath(modpacksPath);
        saveSettings();
    }

    public int getLoadedModpackId() {
        return settings.getLoadedModpack();
    }

    public void setLoadedModpackId(int loadedModpack) {
        log.debug("Value \"loadedModpack\" updated. Old value \"{}\" new value \"{}\"", settings.getLoadedModpack(), loadedModpack);
        settings.setLoadedModpack(loadedModpack);
        saveSettings();
    }

}
