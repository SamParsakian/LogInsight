package com.logmonitoring.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the login view.
 * Handlers are stubs until DB and navigation are wired.
 */
public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    private void onLogin() {
        lblError.setVisible(false);
        lblError.setText("");
        // TODO: validate and navigate to main
    }

    @FXML
    private void onExit() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }
}
