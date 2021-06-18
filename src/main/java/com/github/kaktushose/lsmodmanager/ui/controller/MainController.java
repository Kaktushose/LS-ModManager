package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.services.ModpackService;
import com.github.kaktushose.lsmodmanager.services.SavegameService;
import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.services.model.Modpack;
import com.github.kaktushose.lsmodmanager.services.model.Savegame;
import com.github.kaktushose.lsmodmanager.ui.App;
import com.github.kaktushose.lsmodmanager.ui.SceneManager;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController extends Controller {

    private final SceneManager sceneManager;
    private final ModpackService modpackService;
    private final SettingsService settingsService;
    private final SavegameService savegameService;
    @FXML
    public ComboBox<String> modpackComboBox;
    @FXML
    public ComboBox<String> savegameComboBox;
    @FXML
    public ListView<String> modpackListView;
    @FXML
    public ListView<String> savegameListView;
    @FXML
    public Label modpackName, requiredMods;
    private Modpack loadedModpack;

    public MainController(App app, Stage stage) {
        super(app, stage);
        sceneManager = app.getSceneManager();
        modpackService = app.getModpackService();
        settingsService = app.getSettingsService();
        savegameService = app.getSavegameService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void afterInitialization() {
        loadedModpack = modpackService.getById(settingsService.getLoadedModpackId());
        savegameComboBox.getItems().addAll(savegameService.getAll().stream().map(Savegame::getName).collect(Collectors.toList()));
        updateData();
        stage.getScene().setOnKeyPressed(keyEvent -> {
            if (new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
                sceneManager.showModpackCreate();
            } else if (new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
                sceneManager.showModpackEdit();
            } else if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
                onSettings();
            } else if (keyEvent.getCode() == KeyCode.F1) {
                onHelp();
            }
        });
    }

    public void updateData() {
        updateListView(loadedModpack);
        updateComboBox();
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
        Modpack selected = modpackService.getByName(modpackComboBox.getValue());
        updateListView(selected);
    }

    public void onSavegameSelect() {
        Savegame savegame = savegameService.getAll().get(savegameComboBox.getSelectionModel().getSelectedIndex());
        int loaded = loadedModpack == null ? 0 : loadedModpack.getMods().size() - 1; // -1 for package info
        requiredMods.setText(String.format("%d von %d benötigten Mods sind geladen", loaded, savegame.getModNames().size()));
        savegameListView.getSelectionModel().clearSelection();
        savegameListView.getItems().clear();
        savegameListView.getItems().addAll(savegame.getModNames());
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
        openURL("https://github.com/Kaktushose/LS-ModManager/");
    }

    @FXML
    public void onHelp() {
        openURL("https://github.com/Kaktushose/LS-ModManager/wiki");
    }

    private void updateComboBox() {
        modpackComboBox.getItems().clear();
        modpackComboBox.getSelectionModel().clearSelection();

        modpackComboBox.getItems().add("Kein Modpack");
        modpackComboBox.getSelectionModel().select("Kein Modpack");

        modpackService.getAll().forEach(modpack -> modpackComboBox.getItems().add(modpack.getName()));

        int id = settingsService.getLoadedModpackId();
        if (id < 1) {
            return;
        }
        loadedModpack = modpackService.getById(id);
        String name = loadedModpack.getName();
        modpackName.setText(name);
        modpackComboBox.getSelectionModel().select(name);
    }

    private void updateListView(Modpack modpack) {
        modpackListView.getItems().clear();
        modpackListView.getSelectionModel().clearSelection();

        if (modpack == null) {
            return;
        }

        modpack.getMods().forEach(file -> {
            if (!file.getName().endsWith("zip")) {
                return;
            }
            modpackListView.getItems().add(file.getName());
        });
    }

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
