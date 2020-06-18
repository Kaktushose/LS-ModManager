package de.github.kaktushose.lsmodmanager.core;

import de.github.kaktushose.lsmodmanager.json.config.Config;
import de.github.kaktushose.lsmodmanager.json.config.ConfigFile;
import de.github.kaktushose.lsmodmanager.ui.Dialogs;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class App extends Application {

    private final ConfigFile configFile;
    private final SceneManager sceneManager;
    private final ModpackManager modpackManager;
    private final SavegameInspector savegameInspector;
    private final Logger logger;
    private Config config;
    private long startTime;

    public App() {
        System.setProperty("lsmm.log", System.getenv("AppData") + "\\LS-ModManager");
        logger = LoggerFactory.getLogger(App.class);
        configFile = new ConfigFile();
        sceneManager = new SceneManager(this);
        modpackManager = new ModpackManager(this);
        savegameInspector = new SavegameInspector(this);
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("An unexptected error has occurred! Details:", e);
            Dialogs.displayException(e);
        });
    }

    void preStart() {
        startTime = System.currentTimeMillis();
        logger.info("Starting app...");
        config = configFile.loadConfig();
        modpackManager.indexModpacks();
        savegameInspector.indexSavegames();
    }

    @Override
    public void start(Stage primaryStage) {
        preStart();
        start();
        postStart();
    }

    void start() {
        sceneManager.showMainWindow();
    }

    void postStart() {
        if (getLsPath().isEmpty()) {
            for (int i = 19; i != 11; i-=2) {
                String path = System.getProperty("user.home") + "\\Documents\\My Games\\FarmingSimulator20" + i;
                if (new File(path).exists()) {
                    setLsPath(path);
                    if (getModpackPath().isEmpty()) {
                        setModpackPath(path);
                    }
                    break;
                }
            }
            if (getLsPath().isEmpty()) {
                Dialogs.displayWarnMessage("Warnung!",
                        "Der LS-ModManager konnte keinen LS-Ordner finden. " +
                                "Bitte gehe in die Einstellungen und wähle den LS-Ordner manuell aus.");
            }
        }
        logger.info(String.format("Successfully started app! Took %d ms", System.currentTimeMillis() - startTime));
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public ModpackManager getModpackManager() {
        return modpackManager;
    }

    public SavegameInspector getSavegameInspector() {
        return savegameInspector;
    }

    //Getter and Setter for config
    public String getLsPath() {
        return config.getLsPath();
    }

    public void setLsPath(String lsPath) {
        logger.debug(String.format("Value \"lsPath\" updated. Old value \"%s\" new value \"%s\"", config.getLsPath(), lsPath));
        config.setLsPath(lsPath);
        configFile.saveConfig(config);
    }

    public String getModpackPath() {
        return config.getModpackPath();
    }

    public void setModpackPath(String modpacksPath) {
        logger.debug(String.format("Value \"modpackPath\" updated. Old value \"%s\" new value \"%s\"", config.getModpackPath(), modpacksPath));

        config.setModpackPath(modpacksPath);
        configFile.saveConfig(config);
    }

    public int getLoadedModpackId() {
        return config.getLoadedModpack();
    }

    public void setLoadedModpackId(int loadedModpack) {
        logger.debug(String.format("Value \"loadedModpack\" updated. Old value \"%s\" new value \"%s\"", config.getLoadedModpack(), loadedModpack));
        config.setLoadedModpack(loadedModpack);
        configFile.saveConfig(config);
    }

}
