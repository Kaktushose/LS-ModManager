package com.github.kaktushose.lsmodmanager.ui.components;

import javafx.scene.control.Button;

import java.io.File;

public class SelectableFile {

    private final String name;
    private final File file;
    private final Button button;
    private boolean delete;

    public SelectableFile(File file) {
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
        if (!(obj instanceof SelectableFile)) return false;
        SelectableFile model = (SelectableFile) obj;
        return model.getName().equals(name);
    }
}
