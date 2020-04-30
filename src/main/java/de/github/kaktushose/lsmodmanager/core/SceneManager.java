package de.github.kaktushose.lsmodmanager.core;

import de.github.kaktushose.lsmodmanager.ui.controller.Controller;
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

public class SceneManager {

    private App app;
    private Stage primaryStage;
    private Logger logger;

    public SceneManager(App app, Stage primaryStage) {
        this.app = app;
        this.primaryStage = primaryStage;
        logger = LoggerFactory.getLogger(SceneManager.class);
    }

    public void showMainWindow() {
        Stage stage = loadFXML(MainController.class, "mainwindow.fxml", 900, 600);
        stage.setTitle("LS-ModManager");
        stage.setResizable(false);
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.show();
    }

    public void showSettings() {
        Stage stage = loadFXML(SettingsController.class, "settings.fxml", 640, 440);
        stage.setTitle("Einstellungen");
        stage.setResizable(false);
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private Stage loadFXML(Class<?> controllerClass, String file, int width, int height) {
        Stage stage = new Stage();

        final Parent root;
        final Controller controller;

        try {
            controller = (Controller) controllerClass.getConstructor(App.class, Stage.class).newInstance(app, stage);
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/" + file));
            loader.setController(controller);
            root = loader.load();
        } catch (Exception e) { // bad practice, but this shit can throw like thousands different exceptions
            CloseEvent closeEvent = new CloseEvent(e, 1);
            closeEvent.perform();
            return stage;
        }
        stage.setScene(new Scene(root, width, height));
        controller.afterInitialization();
        return stage;
    }

}
