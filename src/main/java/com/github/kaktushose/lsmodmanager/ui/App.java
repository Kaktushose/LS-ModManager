package com.github.kaktushose.lsmodmanager.ui;

import com.github.kaktushose.lsmodmanager.services.ModpackService;
import com.github.kaktushose.lsmodmanager.services.SavegameService;
import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends Application {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private final SettingsService settingsService;
    private final ModpackService modpackService;
    private final SavegameService savegameService;
    private final SceneManager sceneManager;

    public App() {
        settingsService = new SettingsService();
        settingsService.loadSettings();
        modpackService = new ModpackService(settingsService);
        savegameService = new SavegameService(settingsService);
        sceneManager = new SceneManager(this);
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting app...");

        long startTime = System.currentTimeMillis();

        Thread.setDefaultUncaughtExceptionHandler(((t, e) -> sceneManager.onException(e)));

        settingsService.loadSettings();
        savegameService.indexSavegames();
        modpackService.indexModpacks();
        sceneManager.showMainWindow();

        if (!settingsService.findFsPath()) {
            Alerts.displayWarnMessage("Warnung!",
                    "Der LS-ModManager konnte keinen LS-Ordner finden. " +
                            "Bitte gehe in die Einstellungen und wähle den LS-Ordner manuell aus.");
        }

        log.info(String.format("Successfully started app! Took %d ms", System.currentTimeMillis() - startTime));
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public ModpackService getModpackService() {
        return modpackService;
    }

    public SavegameService getSavegameService() {
        return savegameService;
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }
}
