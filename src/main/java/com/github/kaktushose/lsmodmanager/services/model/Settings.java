package com.github.kaktushose.lsmodmanager.services.model;

public class Settings {

    private String fsPath;
    private int loadedModpack;
    private String modpackPath;

    public Settings() {
        fsPath = "";
        loadedModpack = -1;
        modpackPath = fsPath;
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
}
