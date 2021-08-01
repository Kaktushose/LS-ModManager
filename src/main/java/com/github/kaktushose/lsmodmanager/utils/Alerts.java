package com.github.kaktushose.lsmodmanager.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.control.ButtonBar.ButtonData;

public class Alerts {

    public static boolean displayCloseOptions(String title, String message) {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.Bundle", Locale.getDefault());
        ButtonType close = new ButtonType(bundle.getString("alerts.button.exit"), ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(bundle.getString("alerts.button.cancel"), ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert
                (Alert.AlertType.CONFIRMATION,
                        message,
                        close,
                        cancel);
        alert.setHeaderText(null);
        alert.setTitle(title);
        applyStyle(alert);
        Optional<ButtonType> optional = alert.showAndWait();
        return optional.orElse(cancel) == close;
    }

    public static int displaySaveOptions(String title, String message) {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.Bundle", Locale.getDefault());
        ButtonType save = new ButtonType(bundle.getString("alerts.button.save"), ButtonData.YES);
        ButtonType notSave = new ButtonType(bundle.getString("alerts.button.notsave"), ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(bundle.getString("alerts.button.cancel"), ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert
                (Alert.AlertType.CONFIRMATION,
                        message,
                        save,
                        notSave,
                        cancel);
        alert.setHeaderText(null);
        alert.setTitle(title);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setMinWidth(450);
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        applyStyle(alert);
        Optional<ButtonType> optional = alert.showAndWait();

        if (optional.orElseThrow().getButtonData().equals(ButtonData.YES)) {
            return 0;
        } else if (optional.get().getButtonData().equals(ButtonData.OK_DONE)) {
            return 1;
        } else {
            return 2;
        }
    }

    public static boolean displayConfirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyStyle(alert);
        Optional<ButtonType> optional = alert.showAndWait();
        return optional.orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public static void displayInfoMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        applyStyle(alert);
        alert.showAndWait();
    }

    public static void displayErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        applyStyle(alert);
        alert.showAndWait();
    }

    public static void displayWarnMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        applyStyle(alert);
        alert.showAndWait();
    }

    public static void displayException(Throwable throwable) {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.Bundle", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("alerts.crash.title"));
        alert.setHeaderText(bundle.getString("alerts.crash.text"));
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        alert.setContentText(throwable.getMessage());

        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        applyStyle(alert);
        alert.showAndWait();
    }

    private static void applyStyle(Alert alert) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("img/LogoT.png"));
        stage.getScene().getStylesheets().add("fxml/material-fx-v0_3.css");
    }
}
