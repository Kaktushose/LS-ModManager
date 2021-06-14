package com.github.kaktushose.lsmodmanager.services;

import com.github.kaktushose.lsmodmanager.services.model.Settings;
import com.github.kaktushose.lsmodmanager.util.Constants;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.kaktushose.lsmodmanager.util.Constants.SETTINGS_PATH;

public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);
    private final Gson gson;
    private Settings settings;

    public SettingsService() {
        gson = new Gson();
    }

    public void loadSettings() {
        try {
            if (!Files.exists(Path.of(SETTINGS_PATH))) {
                settings = new Settings();
                saveSettings();
                log.warn("No settings file found! Created a new one!");
                return;
            }

            JsonReader jsonReader = new JsonReader(new FileReader(SETTINGS_PATH));
            settings = gson.fromJson(jsonReader, Settings.class);
            jsonReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("There was an error loading the settings file!", e);
        }
    }

    private void saveSettings() {
        try (FileWriter fileWriter = new FileWriter(SETTINGS_PATH)) {
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
