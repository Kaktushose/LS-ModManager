package com.github.kaktushose.lsmodmanager.ui;

import com.github.kaktushose.lsmodmanager.services.ModpackService;
import com.github.kaktushose.lsmodmanager.services.SavegameService;
import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private final SettingsService settingsService;
    private final ModpackService modpackService;
    private final SavegameService savegameService;
    private final SceneManager sceneManager;

    public App() {
        settingsService = new SettingsService(this);
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

        Locale.setDefault(settingsService.getLanguage());
        ResourceBundle bundle = settingsService.getResourceBundle();
        if (!settingsService.findFsPath()) {
            Alerts.displayWarnMessage(bundle.getString("alerts.folder.title"), bundle.getString("alerts.folder.text"));
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
