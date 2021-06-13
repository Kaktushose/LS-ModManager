package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.core.App;
import com.github.kaktushose.lsmodmanager.ui.Dialogs;
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

    @FXML
    public TextField textFieldName;
    private boolean unsaved;
    private List<File> files;

    public ModpackCreateController(App app, Stage stage) {
        super(app, stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                unsaved = true;
            }
        });
        files = new ArrayList<>();
    }

    @Override
    public void afterInitialization() {

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
        if (textFieldName.getText().isEmpty()) {
            Dialogs.displayErrorMessage("Fehler", "Name des Modpacks darf nicht leer sein!");
            return false;
        }
        String name = textFieldName.getText();

        if (app.getModpackManager().modpackExists(name)) {
            Dialogs.displayErrorMessage("Fehler", "Es existiert bereits ein Modpack mit dem Namen \"" + name + "\".\nBitte wähle einen anderen Namen aus.");
            return false;
        }
        app.getModpackManager().createModpack(name, files);
        Dialogs.displayInfoMessage("Erfolg", "Das Modpack wurde erfolgreich erstellt!");
        unsaved = false;
        onClose();
        app.getSceneManager().updateMainWindowData();
        return true;
    }

    @FXML
    public void onClose() {
        if (unsaved) {
            switch (Dialogs.displaySaveOptions("Speichern?", "Das Modpack wurde noch nicht erstellt.\nTrotzdem schließen?")) {
                case 0:
                    if (onSave()) {
                        stage.close();
                    }
                    return;
                case 1:
                    stage.close();
            }
        } else stage.close();
        logger.debug("modpack create window closed");
    }
}
