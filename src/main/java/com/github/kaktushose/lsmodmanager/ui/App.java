package com.github.kaktushose.lsmodmanager.ui;

import com.github.kaktushose.lsmodmanager.services.ModpackService;
import com.github.kaktushose.lsmodmanager.services.SavegameService;
import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.services.VersionService;
import com.github.kaktushose.lsmodmanager.ui.components.DiskSpaceChecker;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import com.github.kaktushose.lsmodmanager.utils.WorkerThreadFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private final SettingsService settingsService;
    private final ModpackService modpackService;
    private final SavegameService savegameService;
    private final SceneManager sceneManager;
    private final DiskSpaceChecker diskSpaceChecker;
    private final VersionService versionService;
    private final WorkerThreadFactory threadFactory;
    private final boolean firstStart;

    public App() {
        settingsService = new SettingsService();
        firstStart = settingsService.loadSettings();
        modpackService = new ModpackService(this);
        savegameService = new SavegameService(settingsService);
        sceneManager = new SceneManager(this);
        diskSpaceChecker = new DiskSpaceChecker(settingsService);
        versionService = new VersionService(settingsService);
        threadFactory = new WorkerThreadFactory("LSMM-Worker");
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting app...");
        long startTime = System.currentTimeMillis();

        Thread.setDefaultUncaughtExceptionHandler(((t, e) -> sceneManager.onException(e)));

        ResourceBundle bundle = settingsService.getResourceBundle();
        Locale.setDefault(settingsService.getLanguage());
        savegameService.indexSavegames();
        modpackService.indexModpacks();
        sceneManager.showMainWindow();

        if (!settingsService.findFsPath()) {
            Alerts.displayWarnMessage(bundle.getString("alerts.folder.title"), bundle.getString("alerts.folder.text"));
        }

        if (firstStart) {
            Alerts.displayInfoMessage(bundle.getString("alerts.welcome.title"), bundle.getString("alerts.welcome.text"));
        }

        threadFactory.newThread(() -> {
            versionService.retrieveLatestVersion();
            if (versionService.isNewVersionAvailable()) {
                Platform.runLater(() -> {
                    if (Alerts.displayConfirmDialog(bundle.getString("alerts.version.title"), bundle.getString("alerts.version.message"))) {
                        openURL(bundle.getString("alerts.version.url"));
                    }
                });
            }
        }).start();
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

    public DiskSpaceChecker getDiskSpaceChecker() {
        return diskSpaceChecker;
    }

    public WorkerThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void openURL(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                log.error("Failed to open the url: " + url, e);
            }
        } else {
            log.warn("Failed to open link. Desktop is not supported");
        }
    }

}
