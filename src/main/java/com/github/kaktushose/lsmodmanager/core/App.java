package com.github.kaktushose.lsmodmanager.core;

import com.github.kaktushose.lsmodmanager.services.ModpackService;
import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.ui.Dialogs;
import com.github.kaktushose.lsmodmanager.util.Constants;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends Application {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private final SettingsService settingsService;
    private final SceneManager sceneManager;
    private final SavegameInspector savegameInspector;

    public App() {
        System.out.println(Constants.LOGGING_PATH);
        settingsService = new SettingsService();

        sceneManager = new SceneManager(this);
        savegameInspector = new SavegameInspector(settingsService);

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
        savegameInspector.indexSavegames();
        sceneManager.showMainWindow();

        log.info(String.format("Successfully started app! Took %d ms", System.currentTimeMillis() - startTime));
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public ModpackService getModpackManager() {
        return new ModpackService();
    }

    public SavegameInspector getSavegameInspector() {
        return savegameInspector;
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }
}
