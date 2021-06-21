package com.github.kaktushose.lsmodmanager.services.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Settings {

    private final List<Locale> availableLanguages;
    private String fsPath;
    private int loadedModpack;
    private String modpackPath;
    private int lastModpackId;
    private List<Modpack> modpacks;
    private Locale language;

    public Settings() {
        fsPath = "";
        loadedModpack = -1;
        modpackPath = fsPath;
        lastModpackId = -1;
        modpacks = new ArrayList<>();
        language = Locale.ENGLISH;
        availableLanguages = new ArrayList<>() {{
            add(Locale.ENGLISH);
            add(Locale.GERMAN);
        }};
    }

    public String getFsPath() {
        return fsPath;
    }

    public void setFsPath(String fsPath) {
        this.fsPath = fsPath;
    }

    public int getLoadedModpack() {
        return loadedModpack;
    }

    public void setLoadedModpack(int loadedModpack) {
        this.loadedModpack = loadedModpack;
    }

    public String getModpackPath() {
        return modpackPath;
    }

    public void setModpackPath(String modpacksPath) {
        this.modpackPath = modpacksPath;
    }

    public int getLastModpackId() {
        return lastModpackId;
    }

    public void setLastModpackId(int lastModpackId) {
        this.lastModpackId = lastModpackId;
    }

    public List<Modpack> getModpacks() {
        return modpacks;
    }

    public void setModpacks(List<Modpack> modpacks) {
        this.modpacks = modpacks;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public List<Locale> getAvailableLanguages() {
        return availableLanguages;
    }
}
