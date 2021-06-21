package com.github.kaktushose.lsmodmanager.ui;

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
import java.util.Collections;
import java.util.List;

public class SceneManager {

    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);
    private final SceneLoader sceneLoader;
    private MainController mainController;

    public SceneManager(App app) {
        sceneLoader = new SceneLoader(app);
    }

    public void showMainWindow() {
        sceneLoader.loadFXML(MainController.class, "mainwindow.fxml", 900, 600);
        mainController = (MainController) sceneLoader.getController();
        Stage stage = applyStyle(sceneLoader.getStage(), "LS-ModManager");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }

    public void updateMainWindowData() {
        mainController.updateData();
    }

    public void showSettings() {
        sceneLoader.loadFXML(SettingsController.class, "settings.fxml", 640, 380);
        applyStyle(sceneLoader.getStage(), "Einstellungen").showAndWait();
    }

    public void showModpackCreate() {
        sceneLoader.loadFXML(ModpackCreateController.class, "modpackcreate.fxml", 640, 285);
        applyStyle(sceneLoader.getStage(), "Erstellen").showAndWait();
    }

    public void showModpackEdit() {
        sceneLoader.loadFXML(ModpackEditController.class, "modpackedit.fxml", 640, 446);
        applyStyle(sceneLoader.getStage(), "Bearbeiten").showAndWait();
    }

    public List<File> showFileChooser() {
        return showFileChooser(Collections.emptyList());
    }

    public List<File> showFileChooser(Collection<File> selectedFiles) {
        sceneLoader.loadFXML(FileChooserController.class, "filechooser.fxml", 687, 750);
        FileChooserController controller = (FileChooserController) sceneLoader.getController();
        controller.setFiles(selectedFiles);
        applyStyle(sceneLoader.getStage(), "Dateien auswählen").showAndWait();
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
