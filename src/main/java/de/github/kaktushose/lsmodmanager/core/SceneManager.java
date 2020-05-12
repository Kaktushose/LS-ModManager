package de.github.kaktushose.lsmodmanager.core;

import de.github.kaktushose.lsmodmanager.ui.controller.Controller;
import de.github.kaktushose.lsmodmanager.ui.controller.FileChooserController;
import de.github.kaktushose.lsmodmanager.ui.controller.MainController;
import de.github.kaktushose.lsmodmanager.ui.controller.SettingsController;
import de.github.kaktushose.lsmodmanager.util.CloseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class SceneManager {

    private App app;
    private Stage primaryStage;
    private Logger logger;
    private SceneLoader sceneLoader;

    public SceneManager(App app, Stage primaryStage) {
        this.app = app;
        this.primaryStage = primaryStage;
        logger = LoggerFactory.getLogger(SceneManager.class);
        sceneLoader = new SceneLoader(app);
    }

    public void showMainWindow() {
        sceneLoader.loadFXML(MainController.class, "mainwindow.fxml", 900, 600);
        Stage stage = sceneLoader.getStage();
        stage.setTitle("LS-ModManager");
        stage.setResizable(false);
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.show();
    }

    public void showSettings() {
        sceneLoader.loadFXML(MainController.class, "settings.fxml", 640, 440);
        Stage stage = sceneLoader.getStage();
        stage.setTitle("Einstellungen");
        stage.setResizable(false);
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public List<String> showFileChooser() {
        sceneLoader.loadFXML(MainController.class, "filechooser.fxml", 687, 750);
        FileChooserController controller = (FileChooserController) sceneLoader.getController();
        Stage stage = sceneLoader.getStage();
        stage.setTitle("Dateien auswählen");
        stage.setResizable(false);
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return Collections.emptyList();
    }
}
