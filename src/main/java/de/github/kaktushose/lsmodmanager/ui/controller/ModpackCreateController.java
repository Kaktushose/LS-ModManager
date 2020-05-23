package de.github.kaktushose.lsmodmanager.ui.controller;

import de.github.kaktushose.lsmodmanager.core.App;
import de.github.kaktushose.lsmodmanager.core.SceneManager;
import de.github.kaktushose.lsmodmanager.ui.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;

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
        unsaved = false;
        String name = textFieldName.getText();
        List<String> filter = Arrays.asList("\\", "/", ":", "*", "?", "\"", "<", ">", "|");


        if (filter.stream().anyMatch(name::contains)) {
            Dialogs.displayErrorMessage("Fehler", "Der Name darf keines der folgenden Zeichen enthalten:\n \\ / : * ? \" < > |");
            return false;
        }

        Dialogs.displayInfoMessage("Erfolg", "Das Modpack wurde erfolgreich erstellt!");
        onClose();
        return true;
    }

    @FXML
    public void onClose() {
        if (unsaved) {
            switch (Dialogs.displaySaveOptions("Speichern?", "Das Modpack wurde noch nicht erstellt.\r\nTrotzdem schließen?")) {
                case 0:
                    if (onSave()) {
                        stage.close();
                    }
                    return;
                case 1:
                    stage.close();
            }
        } else stage.close();
    }
}
