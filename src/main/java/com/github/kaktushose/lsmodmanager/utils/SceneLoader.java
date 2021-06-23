package com.github.kaktushose.lsmodmanager.utils;

import com.github.kaktushose.lsmodmanager.ui.App;
import com.github.kaktushose.lsmodmanager.ui.controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

public class SceneLoader {

    private static final Logger log = LoggerFactory.getLogger(SceneLoader.class);
    private final App app;
    private Stage stage;
    private Controller controller;

    public SceneLoader(App app) {
        this.app = app;
    }

    public void loadFXML(Class<?> controllerClass, String path, int width, int height, ResourceBundle bundle) {
        Stage stage = new Stage();
        Controller controller;
        Parent root;
        try {
            log.debug("Attempting to load fxml \"{}\"", path);
            controller = (Controller) controllerClass.getConstructor(App.class, Stage.class).newInstance(app, stage);
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/" + path), bundle);
            loader.setController(controller);
            root = loader.load();
            stage.setScene(new Scene(root, width, height));
            controller.afterInitialization();

            log.debug("Successfully initialized controller \"{}\"", controller.getClass().getCanonicalName());
        } catch (Exception e) {
            log.error("LS-ModManager has crashed! Stacktrace:", e);
            System.exit(1);
            return;
        }
        this.stage = stage;
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

    public Stage getStage() {
        return stage;
    }
}
