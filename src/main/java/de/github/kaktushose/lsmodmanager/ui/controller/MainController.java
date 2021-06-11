package de.github.kaktushose.lsmodmanager.ui.controller;

import de.github.kaktushose.lsmodmanager.core.App;
import de.github.kaktushose.lsmodmanager.core.ModpackManager;
import de.github.kaktushose.lsmodmanager.core.SceneManager;
import de.github.kaktushose.lsmodmanager.ui.Dialogs;
import de.github.kaktushose.lsmodmanager.util.CloseEvent;
import de.github.kaktushose.lsmodmanager.util.Modpack;
import de.github.kaktushose.lsmodmanager.util.Savegame;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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
    private final ModpackManager modpackManager;
    @FXML
    public ComboBox<String> modpackComboBox, savegameComboBox;
    @FXML
    public ListView<String> modpackListView, savegameListView;
    @FXML
    public Label modpackName, requiredMods;
    private Modpack loadedModpack;

    public MainController(App app, Stage stage) {
        super(app, stage);
        sceneManager = app.getSceneManager();
        modpackManager = app.getModpackManager();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void afterInitialization() {
        loadedModpack = modpackManager.getModpackById(app.getLoadedModpackId());
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
        savegameComboBox.getItems().addAll(app.getSavegameInspector().getSavegames().stream().map(Savegame::getName).collect(Collectors.toList()));
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
        if (Dialogs.displayCloseOptions("Beenden?", "Möchtest du den LS-ModManager wirklich beenden?")) {
            new CloseEvent("The user has closed the program", 0).perform();
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
        Modpack selected = modpackManager.getModpack(modpackComboBox.getValue());
        updateListView(selected);
    }

    public void onSavegameSelect() {
        Savegame savegame = app.getSavegameInspector().getSavegames().get(savegameComboBox.getSelectionModel().getSelectedIndex());
        int loaded = loadedModpack == null ? 0 : loadedModpack.getMods().size() - 1; // -1 for package info
        requiredMods.setText(String.format("%d von %d benötigten Mods sind geladen", loaded, savegame.getMods().size()));
        savegameListView.getSelectionModel().clearSelection();
        savegameListView.getItems().clear();
        savegameListView.getItems().addAll(savegame.getMods());
    }

    @FXML
    public void onModpackLoad() {
        if (loadedModpack != null) {
            modpackManager.unloadModpack(loadedModpack);
            loadedModpack = null;
        }
        if (modpackComboBox.getValue().equals("Kein Modpack")) {
            modpackName.setText("Kein Modpack");
            return;
        }
        Modpack newValue = modpackManager.getModpack(modpackComboBox.getValue());
        if (modpackManager.loadModpack(newValue)) {
            loadedModpack = newValue;
            modpackName.setText(loadedModpack.getName());
        }
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
        modpackComboBox.getItems().clear();
        modpackComboBox.getSelectionModel().clearSelection();
        modpackComboBox.getItems().add("Kein Modpack");
        modpackComboBox.getSelectionModel().select("Kein Modpack");
        app.getModpackManager().getModpacks().keySet().stream().sorted().forEach(s -> modpackComboBox.getItems().add(s));
        int id = app.getLoadedModpackId();
        if (id < 1) {
            return;
        }
        loadedModpack = modpackManager.getModpackById(id);
        String name = loadedModpack.getName();
        modpackName.setText(name);
        modpackComboBox.getSelectionModel().select(name);
    }

    private void updateListView(Modpack modpack) {
        modpackListView.getItems().clear();
        modpackListView.getSelectionModel().clearSelection();
        if (modpack == null) return;
        modpack.getMods().forEach(file -> {
            if (file.getName().endsWith("zip")) {
                modpackListView.getItems().add(file.getName());
            }
        });
    }

    private void openURL(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                logger.error("Failed to open the url: " + url, e);
            }
        } else {
            logger.warn("Failed to open link. Desktop is not supported");
        }
    }

}
