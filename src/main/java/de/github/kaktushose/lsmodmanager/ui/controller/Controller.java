package de.github.kaktushose.lsmodmanager.ui.controller;

import de.github.kaktushose.lsmodmanager.core.App;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Controller implements Initializable {

    protected App app;
    protected Stage stage;
    protected Logger logger;

    public Controller(App app, Stage stage) {
        this.app = app;
        this.stage = stage;
        logger = LoggerFactory.getLogger("de.github.kaktushose.lsmodmanager.ui.controller");
        stage.setOnCloseRequest(event -> {
            event.consume();
            onCloseRequest();
        });
    }

    // will be called after javafx initialize
    public abstract void afterInitialization();

    // will be called when stage is requested to close, can be overwritten to change behaviour
    public void onCloseRequest() {
        stage.close();
    }

}
