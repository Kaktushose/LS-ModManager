package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.core.App;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Controller implements Initializable {

    protected App app;
    protected Stage stage;
    protected static final Logger log = LoggerFactory.getLogger(Controller.class);

    public Controller(App app, Stage stage) {
        this.app = app;
        this.stage = stage;
        stage.setOnCloseRequest(event -> {
            event.consume();
            onCloseRequest();
        });
    }

    public void afterInitialization() {
    }

    public void onCloseRequest() {
        stage.close();
    }
}
