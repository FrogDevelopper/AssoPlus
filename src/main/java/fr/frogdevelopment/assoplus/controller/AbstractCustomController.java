/*
 * Copyright (c) Frog Development 2015.
 */

package fr.frogdevelopment.assoplus.controller;

import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

abstract class AbstractCustomController implements Initializable {

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        this.initialize();
    }

    protected String getMessage(String key) {
        return resources.getString(key);
    }

    protected Window getParent(Event event) {
        Node source = (Node) (event.getSource());
        return source.getScene().getWindow();
    }

    protected abstract void initialize();


    protected void showYesNoDialog(String messageKey, Consumer onYes) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(getMessage("global.warning.title"));
        dialog.setContentText(messageKey);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.NO);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/img/shield-warning_16.png"));


        dialog.showAndWait()
                .filter(response -> response == ButtonType.YES)
                .ifPresent(onYes);
    }

//    protected void showAlert(String messageKey, Consumer onYes) {
//
//        Alert alert = new Alert(Alert.AlertType.OK);
//        alert.setTitle(getMessage("global.warning.title"));
//        alert.setContentText(messageKey);
//
//        alert.showAndWait()
//                .filter(response -> response == ButtonType.YES)
//                .ifPresent(onYes);
//    }
}
