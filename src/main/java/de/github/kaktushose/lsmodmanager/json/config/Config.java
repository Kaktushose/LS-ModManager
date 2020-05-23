package de.github.kaktushose.lsmodmanager.json.config;

public class Config {

    private String lsPath;
    private int loadedModpack;
    private String modpackPath;

    // TODO check if LS path really exists, if not search for other versions of game
    Config() {
        lsPath = System.getProperty("user.home") + "\\Documents\\My Games\\FarmingSimulator2019";
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
