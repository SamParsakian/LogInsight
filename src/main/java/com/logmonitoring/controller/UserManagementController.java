package com.logmonitoring.controller;

import com.logmonitoring.service.UserDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controller for the User Management window (separate Stage).
 */
public class UserManagementController {

    @FXML
    private TableView<UserDao.UserRecord> tableUsers;
    @FXML
    private TableColumn<UserDao.UserRecord, String> colUsername;
    @FXML
    private TableColumn<UserDao.UserRecord, String> colPassword;
    @FXML
    private TableColumn<UserDao.UserRecord, String> colCreatedAt;

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnClear;

    private UserDao userDao;
    private UserDao.UserRecord selectedUser;

    @FXML
    private void initialize() {
        userDao = new UserDao();
        userDao.createTableIfNotExists();

        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        loadUsers();

        tableUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedUser = newVal;
                txtUsername.setText(newVal.getUsername());
                txtPassword.setText(newVal.getPassword());
                btnAdd.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            } else {
                selectedUser = null;
                btnAdd.setDisable(false);
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
            }
        });

        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void loadUsers() {
        ObservableList<UserDao.UserRecord> users = FXCollections.observableArrayList(userDao.getAllUsers());
        tableUsers.setItems(users);
    }

    @FXML
    private void onAdd() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in both username and password");
            return;
        }
        if (userDao.usernameExists(username, null)) {
            showAlert(Alert.AlertType.WARNING, "Error", "Username already exists. Please choose a different username.");
            return;
        }
        if (userDao.addUser(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully");
            loadUsers();
            onClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user");
        }
    }

    @FXML
    private void onUpdate() {
        if (selectedUser == null) return;
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in both username and password");
            return;
        }
        if (userDao.usernameExists(username, selectedUser.getId())) {
            showAlert(Alert.AlertType.WARNING, "Error", "Username already exists. Please choose a different username.");
            return;
        }
        if (userDao.updateUser(selectedUser.getId(), username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully");
            loadUsers();
            onClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user");
        }
    }

    @FXML
    private void onDelete() {
        if (selectedUser == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete User");
        confirm.setContentText("Are you sure you want to delete user: " + selectedUser.getUsername() + "?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (userDao.deleteUser(selectedUser.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully");
                loadUsers();
                onClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user");
            }
        }
    }

    @FXML
    private void onClear() {
        txtUsername.clear();
        txtPassword.clear();
        tableUsers.getSelectionModel().clearSelection();
        selectedUser = null;
        btnAdd.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
    }

    @FXML
    private void onClose() {
        ((Stage) tableUsers.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setContentText(content);
        a.showAndWait();
    }
}
