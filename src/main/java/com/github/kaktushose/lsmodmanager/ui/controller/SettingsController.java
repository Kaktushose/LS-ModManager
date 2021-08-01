package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.ui.App;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import com.github.kaktushose.lsmodmanager.utils.Checks;
import com.github.kaktushose.lsmodmanager.utils.Constants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class SettingsController extends Controller {

    private final SettingsService settingsService;
    private final Map<String, Locale> locales;
    @FXML
    public TextField textFieldFsPath;
    @FXML
    public TextField textFieldModpackPath;
    @FXML
    public ComboBox<String> languageComboBox;
    private Locale locale;
    private boolean languageChanged;
    private boolean fsPathChanged;
    private boolean modpacksPathChanged;
    private boolean unsaved;
    private boolean canReload;
    private ResourceBundle bundle;

    public SettingsController(App app, Stage stage) {
        super(app, stage);
        this.settingsService = app.getSettingsService();
        locales = new HashMap<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        languageChanged = false;
        fsPathChanged = false;
        bundle = resources;
        textFieldFsPath.setText(settingsService.getFsPath());
        textFieldModpackPath.setText(settingsService.getModpackPath());
        settingsService.getAvailableLanguages().forEach(locale -> {
            String name = locale.getDisplayName(locale);
            locales.put(name, locale);
            languageComboBox.getItems().add(name);
        });
        locale = settingsService.getLanguage();
        languageComboBox.getSelectionModel().select(locale.getDisplayName(locale));
    }

    @Override
    public void onCloseRequest() {
        onClose();
    }

    @FXML
    public void onFsPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(Constants.MY_GAMES));
        directoryChooser.setTitle(bundle.getString("settings.filechooser.title"));
        File path = directoryChooser.showDialog(stage);
        if (path == null) return;
        textFieldFsPath.setText(path.getAbsolutePath());
        fsPathChanged = !settingsService.getFsPath().equals(path.getAbsolutePath());
        unsaved = unsaved || fsPathChanged;
    }

    @FXML
    public void onModpackPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String oldPath = settingsService.getModpackPath();
        String toOpen = oldPath.contains("\\") ? oldPath.substring(0, oldPath.lastIndexOf("\\")) : oldPath;
        if (!Checks.isBlank(toOpen)) {
            directoryChooser.setInitialDirectory(new File(toOpen));
        }
        directoryChooser.setTitle(bundle.getString("settings.filechooser.title"));

        File path = directoryChooser.showDialog(stage);
        if (path == null) {
            return;
        }
        if (Checks.isModsFolder(path.toString())) {
            Alerts.displayErrorMessage(bundle.getString("settings.error.title"), bundle.getString("settings.error.mods"));
            return;
        }
        if (!Checks.isEmptyDirectory(path.toString())) {
            Alerts.displayErrorMessage(bundle.getString("settings.error.title"), bundle.getString("settings.error.empty"));
            return;
        }
        if (Checks.isSubDirectory(oldPath, path.toString())) {
            Alerts.displayErrorMessage(bundle.getString("settings.error.title"), bundle.getString("settings.error.sub"));
            return;
        }
        textFieldModpackPath.setText(path.getAbsolutePath());
        modpacksPathChanged = !settingsService.getModpackPath().equals(path.getAbsolutePath());
        unsaved = unsaved || modpacksPathChanged;
    }

    @FXML
    public void onLanguageSelect() {
        locale = locales.get(languageComboBox.getSelectionModel().getSelectedItem());
        languageChanged = !locale.equals(settingsService.getLanguage());
        unsaved = unsaved || languageChanged;
    }

    @FXML
    public boolean onSave() {
        if (modpacksPathChanged) {
            if (!app.getDiskSpaceChecker().checkMoving(settingsService.getModpackPath(), textFieldFsPath.getText())) {
                return false;
            }
            ProgressIndicatorController controller = app.getSceneManager().getProgressIndicatorController();
            controller.showEndless();
            app.getThreadFactory().newThread(() -> {
                app.getModpackService().moveModpackFolder(Path.of(textFieldModpackPath.getText()));
                settingsService.setModpackPath(textFieldModpackPath.getText());
                Platform.runLater(controller::close);
            }).start();
        }

        settingsService.setFsPath(textFieldFsPath.getText());
        settingsService.setLanguage(locale);
        canReload = true;
        unsaved = false;
        return true;
    }

    @FXML
    public void onClose() {
        boolean exit = true;

        if (unsaved) {
            int result = Alerts.displaySaveOptions(bundle.getString("settings.save.title"), bundle.getString("settings.save.message"));
            switch (result) {
                case 0:
                    exit = onSave();
                    break;
                case 1:
                    break;
                default:
                    return;
            }
        }
        if (!exit) {
            return;
        }

        stage.close();
        log.debug("settings window closed");

        if (canReload) {
            if (fsPathChanged) {
                app.getSavegameService().indexSavegames();
                app.getSceneManager().reloadMainWindow();
            }
            if (languageChanged) {
                app.getSceneManager().reloadMainWindow();
            }
        }
    }
}
