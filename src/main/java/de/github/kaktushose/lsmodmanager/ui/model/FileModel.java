package de.github.kaktushose.lsmodmanager.ui.model;

import javafx.scene.control.Button;

import java.io.File;

public class FileModel {

    private final String name;
    private final File file;
    private final Button button;
    private boolean delete;

    public FileModel(File file) {
        this.file = file;
        this.name = file.getName();
        this.button = new Button("behalten");
        delete = false;
        button.setStyle("-fx-background-color: green; -fx-text-fill: white");
        button.setOnAction(event -> {
            event.consume();
            setDelete(!delete);
        });
    }

    public String getName() {
        return name;
    }

    public Button getButton() {
        return button;
    }

    public File getFile() {
        return file;
    }

    public boolean isDelete() {
        return delete;
    }

    private void setDelete(boolean delete) {
        this.delete = delete;
        if (!delete) {
            button.setText("behalten");
            button.setStyle("-fx-background-color: green; -fx-text-fill: white");
        } else {
            button.setText("entfernen");
            button.setStyle("-fx-background-color: red; -fx-text-fill: white");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileModel)) return false;
        FileModel model = (FileModel) obj;
        return model.getName().equals(name);
    }
}
