package de.github.kaktushose.lsmodmanager.ui.controller;

import de.github.kaktushose.lsmodmanager.core.App;
import de.github.kaktushose.lsmodmanager.core.ModpackManager;
import de.github.kaktushose.lsmodmanager.ui.Dialogs;
import de.github.kaktushose.lsmodmanager.util.Modpack;
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

    private final ModpackManager modpackManager;
    @FXML
    public TextField textFieldName;
    @FXML
    public ComboBox<String> modpackComboBox;
    @FXML
    public Button buttonAdd, buttonDelete, buttonSave;
    private boolean unsaved;
    private Modpack modpack;
    private List<File> files;

    public ModpackEditController(App app, Stage stage) {
        super(app, stage);
        modpackManager = app.getModpackManager();
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
        modpackManager.getModpacks().keySet().stream().sorted().forEach(s -> modpackComboBox.getItems().add(s));
    }

    @FXML
    public void onModpackSelect() {
        if (modpackComboBox.getSelectionModel().isEmpty()) {
            return;
        }
        modpack = modpackManager.getModpacks().get(modpackComboBox.getValue());
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
        if (Dialogs.displayConfirmDialog("Löschen?", "Möchtest du das Modpack\"" + modpack.getName() + "\" wirklich löschen?")) {
            modpackManager.deleteModpack(modpack);
        }
        resetUI();
        unsaved = false;
    }

    @FXML
    public boolean onSave() {
        // store modified modpack
        Modpack updatedModpack = new Modpack(modpack);
        String name = textFieldName.getText();
        if (!name.equals(modpack.getName()) && modpackManager.modpackExists(name)) {
            Dialogs.displayErrorMessage("Fehler", "Es existiert bereits ein Modpack mit dem Namen \"" +
                    textFieldName.getText() +
                    "\".\nBitte wähle einen anderen Namen aus.");
            return false;
        }
        updatedModpack.setName(textFieldName.getText());
        updatedModpack.updateMods(files);
        modpackManager.setModpack(modpack, updatedModpack);

        resetUI();
        app.getSceneManager().updateMainWindowData();
        unsaved = false;
        return true;
    }

    @FXML
    public void onClose() {
        if (unsaved) {
            switch (Dialogs.displaySaveOptions("Speichern?", "Einige Änderungen wurden noch nicht gespeichert.\nTrotzdem schließen?")) {
                case 0:
                    if (onSave()) {
                        stage.close();
                    }
                    return;
                case 1:
                    stage.close();
            }
        } else stage.close();
        logger.debug("modpack edit window closed");
    }

    private void resetUI() {
        // reload items of combo box because names might have changed
        modpackComboBox.getItems().clear();
        modpackComboBox.getSelectionModel().clearSelection();
        modpackManager.getModpacks().keySet().forEach(s -> modpackComboBox.getItems().add(s));

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
