package de.github.kaktushose.lsmodmanager.core;

import javafx.application.Application;
import javafx.stage.Stage;

public class Bootstrapper extends Application {

    @Override
    public void start(Stage primaryStage) {
        App app = new App(primaryStage);
        app.preStart();
        app.start();
        app.postStart();
    }
}
