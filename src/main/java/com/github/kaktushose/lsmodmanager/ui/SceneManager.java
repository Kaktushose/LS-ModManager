package com.github.kaktushose.lsmodmanager.ui;

import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.ui.controller.*;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import com.github.kaktushose.lsmodmanager.utils.SceneLoader;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class SceneManager {

    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);
    private final SceneLoader sceneLoader;
    private final SettingsService settingsService;
    private MainController mainController;

    public SceneManager(App app) {
        sceneLoader = new SceneLoader(app);
        settingsService = app.getSettingsService();
    }

    public void showMainWindow() {
        ResourceBundle bundle = settingsService.getResourceBundle();
        sceneLoader.loadFXML(MainController.class, "mainwindow.fxml", 900, 600, bundle);
        mainController = (MainController) sceneLoader.getController();
        Stage stage = applyStyle(sceneLoader.getStage(), bundle.getString("main.window.title"));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }

    public void updateMainWindowData() {
        mainController.updateData();
    }

    public void reloadMainWindow() {
        mainController.forceClose();
        showMainWindow();
    }

    public void showSettings() {
        ResourceBundle bundle = settingsService.getResourceBundle();
        sceneLoader.loadFXML(SettingsController.class, "settings.fxml", 644, 472, bundle);
        applyStyle(sceneLoader.getStage(), bundle.getString("settings.window.title")).showAndWait();
    }

    public void showModpackCreate() {
        ResourceBundle bundle = settingsService.getResourceBundle();
        sceneLoader.loadFXML(ModpackCreateController.class, "modpackcreate.fxml", 640, 285, bundle);
        applyStyle(sceneLoader.getStage(), bundle.getString("create.window.title")).showAndWait();
    }

    public void showModpackEdit() {
        ResourceBundle bundle = settingsService.getResourceBundle();
        sceneLoader.loadFXML(ModpackEditController.class, "modpackedit.fxml", 640, 446, bundle);
        applyStyle(sceneLoader.getStage(), bundle.getString("edit.window.title")).showAndWait();
    }

    public List<File> showFileChooser(Collection<File> selectedFiles) {
        ResourceBundle bundle = settingsService.getResourceBundle();
        sceneLoader.loadFXML(FileChooserController.class, "filechooser.fxml", 687, 750, bundle);
        FileChooserController controller = (FileChooserController) sceneLoader.getController();
        controller.setFiles(selectedFiles);
        applyStyle(sceneLoader.getStage(), bundle.getString("chooser.window.title")).showAndWait();
        return controller.getSelectedFiles();
    }

    public void onException(Throwable throwable) {
        log.error("LS-ModManager has crashed! Stacktrace:", throwable);
        Alerts.displayException(throwable);
        System.exit(1);
    }

    private Stage applyStyle(Stage stage, String title) {
        stage.setTitle(title);
        stage.setResizable(false);
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }
}
