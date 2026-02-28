package com.logmonitoring.controller;

import com.logmonitoring.application.MainApplication;
import com.logmonitoring.service.UserDao;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * Controller for the login view.
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

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please fill in both username and password");
            lblError.setVisible(true);
            return;
        }

        UserDao userDao = new UserDao();
        userDao.createTableIfNotExists();
        if (!userDao.validateCredentials(username, password)) {
            lblError.setText("Invalid username or password");
            lblError.setVisible(true);
            return;
        }

        try {
            MainApplication.getInstance().showMainView();
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Error loading main window: " + e.getMessage());
            lblError.setVisible(true);
        }
    }

    @FXML
    private void onExit() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }
}
