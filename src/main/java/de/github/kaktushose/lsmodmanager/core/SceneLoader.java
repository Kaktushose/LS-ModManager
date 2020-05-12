package de.github.kaktushose.lsmodmanager.core;

import de.github.kaktushose.lsmodmanager.ui.controller.Controller;
import de.github.kaktushose.lsmodmanager.util.CloseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneLoader {

    private App app;
    private Stage stage;
    private Parent root;
    private Controller controller;

    SceneLoader(App app) {
        stage = new Stage();
        this.app = app;
    }

    public void loadFXML(Class<?> controllerClass, String file, int width, int height) {
        try {
            controller = (Controller) controllerClass.getConstructor(App.class, Stage.class).newInstance(app, stage);
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/" + file));
            loader.setController(controller);
            root = loader.load();
            stage.setScene(new Scene(root, width, height));
            controller.afterInitialization();
        } catch (Exception e) { // bad practice, but this shit can throw like thousands different exceptions
            CloseEvent closeEvent = new CloseEvent(e, 1);
            closeEvent.perform();
        }
    }

    public Controller getController() {
        return controller;
    }

    public Stage getStage() {
        return stage;
    }
}
