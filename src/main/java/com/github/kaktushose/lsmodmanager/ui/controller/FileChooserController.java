package com.github.kaktushose.lsmodmanager.ui.controller;

import com.github.kaktushose.lsmodmanager.ui.App;
import com.github.kaktushose.lsmodmanager.ui.components.SelectableFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;

public class FileChooserController extends Controller {

    private final ObservableList<SelectableFile> selectableFileObservableList;
    @FXML
    public TableView<SelectableFile> tableView;
    @FXML
    public TableColumn<SelectableFile, String> nameColumn;
    @FXML
    public TableColumn<SelectableFile, Button> buttonColumn;
    private Map<String, File> fileCache;
    private final Map<String, File> selectedFiles;

    public FileChooserController(App app, Stage stage) {
        super(app, stage);
        this.selectedFiles = new HashMap<>();
        selectableFileObservableList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        buttonColumn.setCellValueFactory(new PropertyValueFactory<>("button"));
        buttonColumn.setEditable(true);
        fileCache = new HashMap<>();
    }

    @Override
    public void afterInitialization() {

    }

    @Override
    public void onCloseRequest() {
        onClose();
    }

    public void setFiles(Collection<File> selectedFiles) {
        selectedFiles.forEach(file -> this.selectedFiles.putIfAbsent(file.getName(), file));
        this.selectedFiles.values().forEach(this::addItemToTableView);
        selectedFiles.forEach(file -> fileCache.putIfAbsent(file.getName(), file));
    }

    @FXML
    public void onAddFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Mods auswählen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mods", "*.zip"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        if (selectedFiles != null) {
            selectedFiles.forEach(file -> {
                if (!fileCache.containsKey(file.getName())) {
                    fileCache.put(file.getName(), file);
                    addItemToTableView(file);
                }
            });
        }
    }

    @FXML
    public void onClose() {
        save();
        stage.close();
    }

    public List<File> getSelectedFiles() {
        selectedFiles.values().forEach(file -> log.debug("File selected: " + file));
        List<File> files = new ArrayList<>(selectedFiles.values());
        Collections.sort(files);
        log.debug("File chooser window closed");
        return files;
    }

    private void save() {
        selectedFiles.clear();
        fileCache.values().forEach(file -> selectedFiles.putIfAbsent(file.getName(), file));
        selectableFileObservableList.forEach(selectableFile -> {
            if (selectableFile.isDelete()) {
                selectedFiles.remove(selectableFile.getFile().getName());
            }
        });
    }

    private void addItemToTableView(File file) {
        if (!file.getName().endsWith(".zip")) return;
        selectableFileObservableList.add(new SelectableFile(file));
        tableView.setItems(selectableFileObservableList);
    }

}
