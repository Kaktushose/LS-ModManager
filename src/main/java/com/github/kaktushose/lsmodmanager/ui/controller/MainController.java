package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.services.ModpackService;
import com.github.kaktushose.lsmodmanager.services.SavegameService;
import com.github.kaktushose.lsmodmanager.services.model.Modpack;
import com.github.kaktushose.lsmodmanager.services.model.Savegame;
import com.github.kaktushose.lsmodmanager.ui.App;
import com.github.kaktushose.lsmodmanager.ui.SceneManager;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import com.github.kaktushose.lsmodmanager.utils.Checks;
import javafx.application.Platform;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController extends Controller {

    private final SceneManager sceneManager;
    private final ModpackService modpackService;
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
    private ResourceBundle bundle;
    private String noModpack;

    public MainController(App app, Stage stage) {
        super(app, stage);
        sceneManager = app.getSceneManager();
        modpackService = app.getModpackService();
        savegameService = app.getSavegameService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
    }

    @Override
    public void afterInitialization() {
        noModpack = bundle.getString("main.modpack.none");
        savegameComboBox.getItems().addAll(savegameService.getAll().stream().map(Savegame::getName).collect(Collectors.toList()));
        updateModpackData();
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

    public void updateModpackData() {
        loadedModpack = modpackService.getLoadedModpack();
        updateModpackListView(loadedModpack);
        updateModpackComboBox();
        onSavegameSelect();
    }

    @Override
    public void onCloseRequest() {
        onExit();
    }

    @FXML
    public void onSettings() {
        sceneManager.showSettings();
    }

    public void forceClose() {
        stage.close();
    }

    @FXML
    public void onExit() {
        if (Alerts.displayCloseOptions(bundle.getString("main.exit.title"), bundle.getString("main.exit.message"))) {
            log.info("Successfully stopped LS-ModManager.");
            System.exit(0);
        }
    }

    @FXML
    public void onModpackCreate() {
        if (!isReady()) {
            return;
        }
        sceneManager.showModpackCreate();
    }

    @FXML
    public void onModpackEdit() {
        if (!isReady()) {
            return;
        }
        sceneManager.showModpackEdit();
    }

    @FXML
    public void onModpackSelect() {
        Modpack selected = modpackService.getByName(modpackComboBox.getValue());
        updateModpackListView(selected);
    }

    @FXML
    public void onSavegameSelect() {
        Optional<Savegame> optional = savegameService.getByName(savegameComboBox.getSelectionModel().getSelectedItem());
        if (optional.isEmpty()) {
            return;
        }
        Savegame savegame = optional.get();
        long loaded = loadedModpack == null ? 0 : savegameService.getMissingModsCount(savegame, loadedModpack);
        requiredMods.setText(String.format(bundle.getString("main.required.format"), loaded, savegame.getModNames().size()));
        savegameListView.getSelectionModel().clearSelection();
        savegameListView.getItems().clear();
        savegameListView.getItems().addAll(savegame.getModNames());
    }

    @FXML
    public void onModpackLoad() {
        Modpack newValue = modpackService.getByName(modpackComboBox.getValue());

        if (!app.getDiskSpaceChecker().checkLoading(newValue)) {
            return;
        }
        if (loadedModpack != null) {
            modpackService.unload(loadedModpack.getId());
            loadedModpack = null;

        }
        if (modpackComboBox.getValue().equals(noModpack)) {
            modpackName.setText(noModpack);
            return;
        }

        modpackService.load(newValue.getId()).onSuccess(() -> {
            loadedModpack = newValue;
            Platform.runLater(() -> modpackName.setText(loadedModpack.getName()));
        });
    }

    @FXML
    public void onAbout() {
        app.openURL(bundle.getString("main.url.about"));
    }

    @FXML
    public void onHelp() {
        app.openURL(bundle.getString("main.url.help"));
    }

    private boolean isReady() {
        if (Checks.isBlank(app.getSettingsService().getModpackPath())) {
            Alerts.displayInfoMessage(bundle.getString("alerts.select.title"), bundle.getString("alerts.select.text"));
            return false;
        }
        return true;
    }

    private void updateModpackComboBox() {
        modpackComboBox.getItems().clear();
        modpackComboBox.getSelectionModel().clearSelection();

        modpackComboBox.getItems().add(noModpack);
        modpackComboBox.getSelectionModel().select(noModpack);

        modpackService.getAll().forEach(modpack -> modpackComboBox.getItems().add(modpack.getName()));

        String name = loadedModpack == null ? noModpack : loadedModpack.getName();
        modpackName.setText(name);
        modpackComboBox.getSelectionModel().select(name);
    }

    private void updateModpackListView(Modpack modpack) {
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
}
