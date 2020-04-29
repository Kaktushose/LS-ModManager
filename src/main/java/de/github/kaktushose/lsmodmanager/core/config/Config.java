package de.github.kaktushose.lsmodmanager.core.config;

public class Config {

    private String lsPath;
    private String loadedModpack;

    public Config() {
       lsPath = System.getProperty("user.home") + "\\Documents\\My Games\\Farming Simulator2019";
       loadedModpack = "";
    }

    public String getLsPath() {
        return lsPath;
    }

    public void setLsPath(String lsPath) {
        this.lsPath = lsPath;
    }

    public String getLoadedModpack() {
        return loadedModpack;
    }

    public void setLoadedModpack(String loadedModpack) {
        this.loadedModpack = loadedModpack;
    }
}
