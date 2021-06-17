package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.ui.App;
import com.github.kaktushose.lsmodmanager.ui.SceneManager;
import com.github.kaktushose.lsmodmanager.services.ModpackService;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends Controller {

    private final SceneManager sceneManager;
    private final ModpackService modpackService;
    @FXML
    public ComboBox<String> modpackComboBox, savegameComboBox;
    @FXML
    public ListView<String> modpackListView, savegameListView;
    @FXML
    public Label modpackName, requiredMods;
    //private Modpack loadedModpack;

    public MainController(App app, Stage stage) {
        super(app, stage);
        sceneManager = app.getSceneManager();
        modpackService = app.getModpackService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void afterInitialization() {
//        loadedModpack = modpackService.getModpackById(app.getLoadedModpackId());
//        updateData();
//        stage.getScene().setOnKeyPressed(keyEvent -> {
//            if (new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
//                sceneManager.showModpackCreate();
//            } else if (new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
//                sceneManager.showModpackEdit();
//            } else if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
//                onSettings();
//            } else if (keyEvent.getCode() == KeyCode.F1) {
//                onHelp();
//            }
//        });
    }

    public void updateData() {
//        updateListView(loadedModpack);
//        updateComboBox();
//        savegameComboBox.getItems().addAll(app.getSavegameInspector().getSavegames().stream().map(Savegame::getName).collect(Collectors.toList()));
    }

    @Override
    public void onCloseRequest() {
        onExit();
    }

    @FXML
    public void onSettings() {
        sceneManager.showSettings();
    }

    @FXML
    public void onExit() {
        if (Alerts.displayCloseOptions("Beenden?", "Möchtest du den LS-ModManager wirklich beenden?")) {
            log.info("Successfully stopped LS-ModManager.");
            System.exit(0);
        }
    }

    @FXML
    public void onModpackCreate() {
        sceneManager.showModpackCreate();
    }

    @FXML
    public void onModpackEdit() {
        sceneManager.showModpackEdit();
    }

    @FXML
    public void onModpackSelect() {
        //Modpack selected = modpackService.getModpack(modpackComboBox.getValue());
        //updateListView(selected);
    }

    public void onSavegameSelect() {
//        Savegame savegame = app.getSavegameInspector().getSavegames().get(savegameComboBox.getSelectionModel().getSelectedIndex());
//        int loaded = loadedModpack == null ? 0 : loadedModpack.getMods().size() - 1; // -1 for package info
//        requiredMods.setText(String.format("%d von %d benötigten Mods sind geladen", loaded, savegame.getMods().size()));
//        savegameListView.getSelectionModel().clearSelection();
//        savegameListView.getItems().clear();
//        savegameListView.getItems().addAll(savegame.getMods());
    }

    @FXML
    public void onModpackLoad() {
//        if (loadedModpack != null) {
//            modpackService.unloadModpack(loadedModpack);
//            loadedModpack = null;
//        }
//        if (modpackComboBox.getValue().equals("Kein Modpack")) {
//            modpackName.setText("Kein Modpack");
//            return;
//        }
//        Modpack newValue = modpackService.getModpack(modpackComboBox.getValue());
//        if (modpackService.loadModpack(newValue)) {
//            loadedModpack = newValue;
//            modpackName.setText(loadedModpack.getName());
//        }
    }

    @FXML
    public void onAbout() {
        openURL("https://gadarol.de/board/index.php?thread/4102-ls19-modmanager-diy-java-projekt/");
    }

    @FXML
    public void onHelp() {
        openURL("https://github.com/Kaktushose/ls-modmanager/wiki");
    }

    private void updateComboBox() {
//        modpackComboBox.getItems().clear();
//        modpackComboBox.getSelectionModel().clearSelection();
//        modpackComboBox.getItems().add("Kein Modpack");
//        modpackComboBox.getSelectionModel().select("Kein Modpack");
//        app.getModpackManager().getModpacks().keySet().stream().sorted().forEach(s -> modpackComboBox.getItems().add(s));
//        int id = app.getLoadedModpackId();
//        if (id < 1) {
//            return;
//        }
//        loadedModpack = modpackService.getModpackById(id);
//        String name = loadedModpack.getName();
//        modpackName.setText(name);
//        modpackComboBox.getSelectionModel().select(name);
    }

//    private void updateListView(Modpack modpack) {
//        modpackListView.getItems().clear();
//        modpackListView.getSelectionModel().clearSelection();
//        if (modpack == null) return;
//        modpack.getMods().forEach(file -> {
//            if (file.getName().endsWith("zip")) {
//                modpackListView.getItems().add(file.getName());
//            }
//        });
//    }

    private void openURL(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                log.error("Failed to open the url: " + url, e);
            }
        } else {
            log.warn("Failed to open link. Desktop is not supported");
        }
    }

}
