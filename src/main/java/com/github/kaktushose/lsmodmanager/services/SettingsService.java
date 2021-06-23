package com.github.kaktushose.lsmodmanager.services;

import com.github.kaktushose.lsmodmanager.exceptions.FileOperationException;
import com.github.kaktushose.lsmodmanager.services.model.Modpack;
import com.github.kaktushose.lsmodmanager.services.model.Settings;
import com.github.kaktushose.lsmodmanager.ui.App;
import com.github.kaktushose.lsmodmanager.utils.Checks;
import com.github.kaktushose.lsmodmanager.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.github.kaktushose.lsmodmanager.utils.Constants.SETTINGS_PATH;

public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);
    private final App app;
    private final Gson gson;
    private Settings settings;

    public SettingsService(App app) {
        gson = new Gson();
        this.app = app;
    }

    // returns true when new settings file was created, which equals to first start of app
    public boolean loadSettings() {
        try {
            if (!Files.exists(Path.of(SETTINGS_PATH))) {
                settings = new Settings();
                saveSettings();
                log.warn("No settings file found! Created a new one!");
                return true;
            }

            JsonReader jsonReader = new JsonReader(new FileReader(SETTINGS_PATH));
            settings = gson.fromJson(jsonReader, Settings.class);
            jsonReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("There was an error loading the settings file!", e);
        }
        return false;
    }

    private void saveSettings() {
        try (FileWriter fileWriter = new FileWriter(SETTINGS_PATH)) {
            gson.toJson(settings, fileWriter);
        } catch (IOException e) {
            throw new FileOperationException("There was an error saving the settings file!", e);
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
        Checks.notFile(fsPath, "fsPath");
        log.debug("Value \"fsPath\" updated. Old value \"{}\" new value \"{}\"", settings.getFsPath(), fsPath);
        settings.setFsPath(fsPath);
        saveSettings();
    }

    public String getModpackPath() {
        return settings.getModpackPath();
    }

    public void setModpackPath(String modpacksPath) {
        Checks.notFile(modpacksPath, "modpacksPath");
        Checks.notModsFolder(modpacksPath, "modpacksPath");
        log.debug("Value \"modpackPath\" updated. Old value \"{}\" new value \"{}\"", settings.getModpackPath(), modpacksPath);
        app.getModpackService().moveModpackFolder(Path.of(modpacksPath));
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

    public int getLastModpackId() {
        return settings.getLastModpackId();
    }

    public void setLastModpackId(int lastModpackId) {
        log.debug("Value \"lastModpackId\" updated. Old value \"{}\" new value \"{}\"", settings.getLastModpackId(), lastModpackId);
        settings.setLastModpackId(lastModpackId);
        saveSettings();
    }

    public List<Modpack> getModpacks() {
        return settings.getModpacks();
    }

    public void setModpacks(List<Modpack> modpacks) {
        Checks.notNull(modpacks, "modpacks");
        Collections.sort(modpacks);
        log.debug("Value \"modpacks\" updated. Old size \"{}\" new size \"{}\"", settings.getModpacks().size(), modpacks.size());
        settings.setModpacks(modpacks);
        saveSettings();
    }

    public Locale getLanguage() {
        return settings.getLanguage();
    }

    public void setLanguage(Locale language) {
        Checks.notNull(language, "language");
        log.debug("Value \"language\" updated. Old value \"{}\" new value \"{}\"", settings.getLanguage(), language);
        settings.setLanguage(language);
        Locale.setDefault(language);
        saveSettings();
    }

    public List<Locale> getAvailableLanguages() {
        return settings.getAvailableLanguages();
    }

    public ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("bundles.Bundle", getLanguage());
    }

}
