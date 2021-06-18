package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.services.ModpackService;
import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.services.model.Modpack;
import com.github.kaktushose.lsmodmanager.ui.App;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import com.github.kaktushose.lsmodmanager.utils.Checks;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ModpackEditController extends Controller {

    private final ModpackService modpackService;
    private final SettingsService settingsService;
    @FXML
    public TextField textFieldName;
    @FXML
    public ComboBox<String> modpackComboBox;
    @FXML
    public Button buttonAdd;
    @FXML
    public Button buttonDelete;
    @FXML
    public Button buttonSave;
    private boolean unsaved;
    private Modpack modpack;
    private List<File> files;

    public ModpackEditController(App app, Stage stage) {
        super(app, stage);
        modpackService = app.getModpackService();
        settingsService = app.getSettingsService();
        files = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                unsaved = true;
                modpackComboBox.setDisable(true);
            }
            if (newValue.equals(modpack.getName())) {
                unsaved = false;
                modpackComboBox.setDisable(false);
            }
        });
        setButtonDisable(true);
        textFieldName.setDisable(true);
    }

    @Override
    public void afterInitialization() {
        modpackService.getAll().forEach(modpack -> modpackComboBox.getItems().add(modpack.getName()));
    }

    @FXML
    public void onModpackSelect() {
        if (modpackComboBox.getSelectionModel().isEmpty()) {
            return;
        }
        modpack = modpackService.getByName(modpackComboBox.getValue());
        files.clear();
        files.addAll(modpack.getMods());
        textFieldName.setText(modpack.getName());
        textFieldName.setDisable(false);
        setButtonDisable(false);
    }

    @FXML
    public void onModAdd() {
        files = (app.getSceneManager().showFileChooser(files));
        unsaved = !modpack.getMods().containsAll(files);
        modpackComboBox.setDisable(unsaved);
    }

    @FXML
    public void onDelete() {
        if (modpack.getId() == settingsService.getLoadedModpackId()) {
            if (!Alerts.displayConfirmDialog("Löschen?", "Das Modpack \"" + modpack.getName() + "\" ist aktuell geladen. Möchtest du es trotzdem löschen?")) {
                return;
            }
        } else if (!Alerts.displayConfirmDialog("Löschen?", "Möchtest du das Modpack \"" + modpack.getName() + "\" wirklich löschen?")) {
            return;
        }
//        modpackService.unloadCurrentModpack();
//        modpackService.deleteModpack(modpack);
        app.getSceneManager().updateMainWindowData();
        resetUI();
        unsaved = false;
    }

    @FXML
    public boolean onSave() {
        //modpackService.unloadCurrentModpack();

        Modpack updatedModpack = modpack.copy();
        String name = textFieldName.getText();

        if (Checks.isBlank(name)) {
            Alerts.displayErrorMessage("Fehler", "Name des Modpacks darf nicht leer sein!");
            return false;
        }

        updatedModpack.setName(textFieldName.getText());
        updatedModpack.setMods(files);
        modpackService.updateModpack(modpack.getId(), updatedModpack);

        resetUI();
        app.getSceneManager().updateMainWindowData();
        //modpackService.loadCurrentModpack();
        unsaved = false;
        return true;
    }

    @FXML
    public void onClose() {
        if (unsaved) {
            int result = Alerts.displaySaveOptions("Speichern?", "Das Modpack wurde noch nicht erstellt.\nTrotzdem schließen?");
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
        app.getSceneManager().updateMainWindowData();
        stage.close();
        log.debug("modpack edit window closed");
    }

    private void resetUI() {
        // reload items of combo box because names might have changed
        modpackComboBox.getItems().clear();
        modpackComboBox.getSelectionModel().clearSelection();
        modpackService.getAll().forEach(modpack -> modpackComboBox.getItems().add(modpack.getName()));

        // change ui to default
        setButtonDisable(true);
        textFieldName.setDisable(true);
        textFieldName.setText("");

        // must be set after text, because text change will trigger change listener
        modpackComboBox.setDisable(false);
    }

    private void setButtonDisable(boolean value) {
        buttonAdd.setDisable(value);
        buttonDelete.setDisable(value);
        buttonSave.setDisable(value);
    }
}
