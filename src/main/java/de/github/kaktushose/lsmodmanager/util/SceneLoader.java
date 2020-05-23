package de.github.kaktushose.lsmodmanager.util;

import de.github.kaktushose.lsmodmanager.core.App;
import de.github.kaktushose.lsmodmanager.ui.controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneLoader {

    private final App app;
    private Stage stage;
    private Controller controller;

    public SceneLoader(App app) {
        this.app = app;
    }

    public void loadFXML(Class<?> controllerClass, String file, int width, int height) {
        Stage stage = new Stage();
        Controller controller;
        Parent root;
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
