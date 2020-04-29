package de.github.kaktushose.lsmodmanager.core;

import de.github.kaktushose.lsmodmanager.core.config.Config;
import de.github.kaktushose.lsmodmanager.core.config.ConfigFile;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private final ConfigFile configFile;
    private final Config config;

    public App(ConfigFile configFile) {
        this.configFile = configFile;
        config = configFile.loadConfig();
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println(config.getLoadedModpack());
        primaryStage.show();
    }



}
