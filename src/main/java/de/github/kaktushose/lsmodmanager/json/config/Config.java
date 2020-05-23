package de.github.kaktushose.lsmodmanager.json.config;

public class Config {

    private String lsPath;
    private int loadedModpack;
    private String modpacksPath;

    // TODO check if LS path really exists, if not search for other versions of game
    Config() {
        lsPath = System.getProperty("user.home") + "\\Documents\\My Games\\FarmingSimulator2019";
        loadedModpack = -1;
        modpacksPath = lsPath;
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

    public String getModpacksPath() {
        return modpacksPath;
    }

    public void setModpacksPath(String modpacksPath) {
        this.modpacksPath = modpacksPath;
    }
}
