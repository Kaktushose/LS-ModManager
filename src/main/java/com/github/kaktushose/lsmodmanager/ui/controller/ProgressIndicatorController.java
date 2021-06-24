package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.ui.App;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ProgressIndicatorController extends Controller {

    @FXML
    public ProgressBar progressBar;
    @FXML
    public Label labelPercent;

    public ProgressIndicatorController(App app, Stage stage) {
        super(app, stage);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @Override
    public void onCloseRequest() {
        // should not be consumed, so user cannot close window and has to wait until it closes
    }

    public void update(double percentage) {
        // we don't want 99,x to appear as 100
        if (percentage > 90) {
            percentage = Math.floor(percentage);
        }
        labelPercent.setText(String.format("%.0f%%", percentage));
        progressBar.setProgress(percentage / 100);
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
}
