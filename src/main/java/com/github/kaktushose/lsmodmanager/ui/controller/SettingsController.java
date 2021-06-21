package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.ui.App;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import com.github.kaktushose.lsmodmanager.utils.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends Controller {

    private final SettingsService settingsService;
    @FXML
    public TextField textFieldFsPath;
    @FXML
    public TextField textFieldModpackPath;
    @FXML
    public ComboBox<String> languageComboBox;
    private boolean unsaved;

    public SettingsController(App app, Stage stage) {
        super(app, stage);
        this.settingsService = app.getSettingsService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unsaved = false;
        textFieldFsPath.setText(settingsService.getFsPath());
        textFieldModpackPath.setText(settingsService.getModpackPath());
        languageComboBox.getItems().add("Deutsch");
        languageComboBox.getSelectionModel().select(0);
    }

    @Override
    public void onCloseRequest() {
        onClose();
    }

    @FXML
    public void onFsPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(Constants.MY_GAMES));
        directoryChooser.setTitle("Pfad auswählen");
        System.out.println(Constants.MY_GAMES);
        File path = directoryChooser.showDialog(stage);
        if (path == null) return;
        textFieldFsPath.setText(path.getAbsolutePath());
        unsaved = !settingsService.getFsPath().equals(path.getAbsolutePath());
    }

    @FXML
    public void onModpackPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Pfad auswählen");
        File path = directoryChooser.showDialog(stage);
        if (path == null) return;
        textFieldModpackPath.setText(path.getAbsolutePath());
        unsaved = !settingsService.getModpackPath().equals(path.getAbsolutePath());
    }

    @FXML
    public void onLanguageSelect() {

    }

    @FXML
    public void onSave() {
        settingsService.setFsPath(textFieldFsPath.getText());
        settingsService.setModpackPath(textFieldModpackPath.getText());
        unsaved = false;
    }

    @FXML
    public void onClose() {
        if (unsaved) {
            int result = Alerts.displaySaveOptions("Speichern?", "Einige Änderungen wurden noch nicht gespeichert.\r\nEinstellungen trotzdem verlassen?");
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
        stage.close();
        log.debug("settings window closed");
    }
}
