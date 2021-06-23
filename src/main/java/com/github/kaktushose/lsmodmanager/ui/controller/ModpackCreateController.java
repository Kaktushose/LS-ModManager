package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.services.ModpackService;
import com.github.kaktushose.lsmodmanager.ui.App;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import com.github.kaktushose.lsmodmanager.utils.Checks;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class ModpackCreateController extends Controller {

    private final ModpackService modpackService;
    @FXML
    public TextField textFieldName;
    private boolean unsaved;
    private List<File> files;
    private ResourceBundle bundle;

    public ModpackCreateController(App app, Stage stage) {
        super(app, stage);
        modpackService = app.getModpackService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                unsaved = true;
            }
        });
        files = new ArrayList<>();
        bundle = resources;
    }

    @Override
    public void onCloseRequest() {
        onClose();
    }

    @FXML
    public void onModSelect() {
        List<File> newFiles = app.getSceneManager().showFileChooser(files);
        // sorting both lists before comparing
        Collections.sort(newFiles);
        Collections.sort(files);
        unsaved = !newFiles.equals(files);
        files = newFiles;
    }

    // boolean indicates whether saving was successful or not
    @FXML
    public boolean onSave() {
        String name = textFieldName.getText();

        if (Checks.isBlank(name)) {
            Alerts.displayErrorMessage(bundle.getString("create.error.title"), bundle.getString("create.error.message"));
            return false;
        }

        modpackService.create(name, files);
        Alerts.displayInfoMessage(bundle.getString("create.success.title"), bundle.getString("create.success.message"));
        resetData();
        return true;
    }

    @FXML
    public void onClose() {
        if (unsaved) {
            int result = Alerts.displaySaveOptions(bundle.getString("create.save.title"), bundle.getString("create.save.message"));
            switch (result) {
                case 0:
                    onSave();
                    break;
                case 1:
                    break;
                default:
                    return;
            }
        }
        app.getSceneManager().updateModpackData();
        stage.close();
        log.debug("modpack create window closed");
    }

    private void resetData() {
        textFieldName.setText("");
        files = new ArrayList<>();
        unsaved = false;
    }
}
