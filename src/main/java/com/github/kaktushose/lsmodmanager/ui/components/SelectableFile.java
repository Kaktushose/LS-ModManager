package com.github.kaktushose.lsmodmanager.ui.components;

import javafx.scene.control.Button;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

public class SelectableFile {

    private final File file;
    private final String name;
    private final Button button;
    private final String keep;
    private final String remove;
    private boolean delete;

    public SelectableFile(File file) {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.Bundle", Locale.getDefault());
        keep = bundle.getString("file.button.keep");
        remove = bundle.getString("file.button.remove");
        this.file = file;
        this.name = file.getName();
        this.button = new Button(keep);
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
            button.setText(keep);
            button.setStyle("-fx-background-color: green; -fx-text-fill: white");
        } else {
            button.setText(remove);
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
