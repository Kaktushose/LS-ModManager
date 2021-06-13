package com.github.kaktushose.lsmodmanager.json.config;

public class Config {

    private String lsPath;
    private int loadedModpack;
    private String modpackPath;

    Config() {
        lsPath = "";
        loadedModpack = -1;
        modpackPath = lsPath;
    }

    public String getLsPath() {
        return lsPath;
    }

    public void setLsPath(String lsPath) {
        this.lsPath = lsPath;
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
