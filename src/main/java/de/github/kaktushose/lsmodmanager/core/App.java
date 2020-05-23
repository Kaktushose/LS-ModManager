package de.github.kaktushose.lsmodmanager.core;

import de.github.kaktushose.lsmodmanager.json.config.Config;
import de.github.kaktushose.lsmodmanager.json.config.ConfigFile;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private final Stage stage;
    private final ConfigFile configFile;
    private final SceneManager sceneManager;
    private final ModpackManager modpackManager;
    private final Logger logger;
    private Config config;
    private long startTime;

    App(Stage stage) {
        System.setProperty("lsmm.log", System.getenv("AppData") + "\\LS-ModManager");
        logger = LoggerFactory.getLogger(App.class);
        this.stage = stage;
        configFile = new ConfigFile();
        sceneManager = new SceneManager(this, stage);
        modpackManager = new ModpackManager(this);
    }

    void preStart() {
        startTime = System.currentTimeMillis();
        logger.info("Starting app...");
        config = configFile.loadConfig();
        logger.debug("Loaded config");
    }

    void start() {
        sceneManager.showMainWindow();
    }

    void postStart() {
        logger.info(String.format("Successfully started app! Took %d ms", System.currentTimeMillis() - startTime));
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public ModpackManager getModpackManager() {
        return modpackManager;
    }

    //Getter and Setter for config
    public String getLsPath() {
        return config.getLsPath();
    }

    public void setLsPath(String lsPath) {
        config.setLsPath(lsPath);
        configFile.saveConfig(config);
    }

    public String getModpacksPath() {
        return config.getModpacksPath();
    }

    public void setModpacksPath(String modpacksPath) {
        config.setModpacksPath(modpacksPath);
        configFile.saveConfig(config);
    }

    public int getLoadedModpack() {
        return config.getLoadedModpack();
    }

    public void setLoadedModpack(int loadedModpack) {
        config.setLoadedModpack(loadedModpack);
        configFile.saveConfig(config);
    }
}
