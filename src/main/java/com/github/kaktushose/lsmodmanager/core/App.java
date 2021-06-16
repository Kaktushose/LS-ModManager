package com.github.kaktushose.lsmodmanager.core;

import com.github.kaktushose.lsmodmanager.services.ModpackService;
import com.github.kaktushose.lsmodmanager.services.SavegameService;
import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.ui.Dialogs;
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

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            log.error("An unexpected error has occurred! Details:", e);
            Dialogs.displayException(e);
        });

    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting app...");

        long startTime = System.currentTimeMillis();

        settingsService.loadSettings();
        if (!settingsService.findFsPath()) {
            Dialogs.displayWarnMessage("Warnung!",
                    "Der LS-ModManager konnte keinen LS-Ordner finden. " +
                            "Bitte gehe in die Einstellungen und wähle den LS-Ordner manuell aus.");
        }
        savegameService.indexSavegames();
        sceneManager.showMainWindow();

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
