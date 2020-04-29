package de.github.kaktushose.lsmodmanager.core;

import de.github.kaktushose.lsmodmanager.core.config.Config;
import de.github.kaktushose.lsmodmanager.core.config.ConfigFile;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private Logger logger;
    private Stage stage;
    private final ConfigFile configFile;
    private Config config;
    private SceneManager sceneManager;
    private long startTime;

    App(Stage stage) {
        logger = LoggerFactory.getLogger(App.class);
        this.stage = stage;
        configFile = new ConfigFile();
        sceneManager = new SceneManager(this, stage);
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

    //Getter and Setter for config
    public String getLsPath() {
        return config.getLsPath();
    }

    public void setLsPath(String lsPath) {
        config.setLsPath(lsPath);
        configFile.saveConfig(config);
    }

    public String getLoadedModpack() {
        return config.getLoadedModpack();
    }

    public void setLoadedModpack(String loadedModpack) {
        config.setLoadedModpack(loadedModpack);
        configFile.saveConfig(config);
    }

}
