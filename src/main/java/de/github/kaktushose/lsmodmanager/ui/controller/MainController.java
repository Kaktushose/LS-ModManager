package de.github.kaktushose.lsmodmanager.ui.controller;

import de.github.kaktushose.lsmodmanager.core.App;
import de.github.kaktushose.lsmodmanager.core.ModpackManager;
import de.github.kaktushose.lsmodmanager.core.SceneManager;
import de.github.kaktushose.lsmodmanager.ui.Dialogs;
import de.github.kaktushose.lsmodmanager.util.CloseEvent;
import de.github.kaktushose.lsmodmanager.util.Modpack;
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
    private final ModpackManager modpackManager;
    @FXML
    public ComboBox<String> modpackComboBox;
    @FXML
    public ListView<String> modpackListView;
    @FXML
    public Label modpackName;
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
        updateData();
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
        loadedModpack = modpackManager.getModpack(modpackComboBox.getValue());
        modpackManager.loadModpack(loadedModpack);
        modpackName.setText(loadedModpack.getName());
    }

    @FXML
    public void onAbout() {
        openURL("https://gadarol.de/board/index.php?thread/4102-ls19-modmanager-diy-java-projekt/");
        throw new NullPointerException();
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
                modpackListView.getItems().add(file.getName()) ;
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
