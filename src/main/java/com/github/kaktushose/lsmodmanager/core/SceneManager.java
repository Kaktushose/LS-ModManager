package com.github.kaktushose.lsmodmanager.core;

import com.github.kaktushose.lsmodmanager.ui.controller.*;
import com.github.kaktushose.lsmodmanager.util.SceneLoader;
import de.github.kaktushose.lsmodmanager.ui.controller.*;
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

    private final App app;
    private final Logger logger;
    private final SceneLoader sceneLoader;
    private MainController mainController;

    public SceneManager(App app) {
        this.app = app;
        logger = LoggerFactory.getLogger(SceneManager.class);
        sceneLoader = new SceneLoader(app);
    }

    public void showMainWindow() {
        logger.debug("Showing main window");
        sceneLoader.loadFXML(MainController.class, "mainwindow.fxml", 900, 600);
        Stage stage = sceneLoader.getStage();
        mainController = (MainController) sceneLoader.getController();
        stage.setTitle("LS-ModManager");
        stage.setResizable(false);
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.show();
    }

    public void updateMainWindowData() {
        mainController.updateData();
    }

    public void showSettings() {
        logger.debug("Showing settings window");
        sceneLoader.loadFXML(SettingsController.class, "settings.fxml", 640, 380);
        Stage stage = sceneLoader.getStage();
        stage.setTitle("Einstellungen");
        stage.setResizable(false);
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void showModpackCreate() {
        logger.debug("Showing modpack create window");
        sceneLoader.loadFXML(ModpackCreateController.class, "modpackcreate.fxml", 640, 285);
        Stage stage = sceneLoader.getStage();
        stage.setTitle("Erstellen");
        stage.setResizable(false);
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void showModpackEdit() {
        logger.debug("Showing modpack edit window");
        sceneLoader.loadFXML(ModpackEditController.class, "modpackedit.fxml", 640, 446);
        Stage stage = sceneLoader.getStage();
        stage.setTitle("Bearbeiten");
        stage.setResizable(false);
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public List<File> showFileChooser() {
        return showFileChooser(Collections.emptyList());
    }

    public List<File> showFileChooser(Collection<File> selectedFiles) {
        logger.debug("Showing file chooser window");
        sceneLoader.loadFXML(FileChooserController.class, "filechooser.fxml", 687, 750);
        FileChooserController controller = (FileChooserController) sceneLoader.getController();
        controller.setFiles(selectedFiles);
        Stage stage = sceneLoader.getStage();
        stage.setTitle("Dateien auswählen");
        stage.setResizable(false);
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return controller.getSelectedFiles();
    }

}
