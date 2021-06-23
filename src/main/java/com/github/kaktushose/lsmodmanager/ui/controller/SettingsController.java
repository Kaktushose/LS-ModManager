package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.ui.App;
import com.github.kaktushose.lsmodmanager.utils.Alerts;
import com.github.kaktushose.lsmodmanager.utils.Checks;
import com.github.kaktushose.lsmodmanager.utils.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
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
    private boolean unsaved;
    private boolean languageChanged;
    private boolean reload;
    private ResourceBundle bundle;

    public SettingsController(App app, Stage stage) {
        super(app, stage);
        this.settingsService = app.getSettingsService();
        locales = new HashMap<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unsaved = false;
        languageChanged = false;
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
        unsaved = !settingsService.getFsPath().equals(path.getAbsolutePath());
    }

    @FXML
    public void onModpackPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(bundle.getString("settings.filechooser.title"));

        File path = directoryChooser.showDialog(stage);
        if (path == null) {
            return;
        }
        if (Checks.isModsFolder(path.toString())) {
            Alerts.displayErrorMessage(bundle.getString("settings.error.title"), bundle.getString("settings.error.message"));
            return;
        }

        textFieldModpackPath.setText(path.getAbsolutePath());
        unsaved = !settingsService.getModpackPath().equals(path.getAbsolutePath());
    }

    @FXML
    public void onLanguageSelect() {
        locale = locales.get(languageComboBox.getSelectionModel().getSelectedItem());
        unsaved = !locale.equals(settingsService.getLanguage());
        languageChanged = true;
    }

    @FXML
    public void onSave() {
        settingsService.setFsPath(textFieldFsPath.getText());
        settingsService.setModpackPath(textFieldModpackPath.getText());
        settingsService.setLanguage(locale);
        unsaved = false;
        reload = languageChanged;
    }

    @FXML
    public void onClose() {
        if (unsaved) {
            int result = Alerts.displaySaveOptions(bundle.getString("settings.save.title"), bundle.getString("settings.save.message"));
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
        if (reload) {
            app.getSceneManager().reloadMainWindow();
        }
    }
}
