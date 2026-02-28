package com.logmonitoring.controller;

import com.logmonitoring.model.LogEntry;
import com.logmonitoring.util.LogTimestampFormatter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for the main log viewer. Table and menu actions are stubs until later steps.
 */
public class MainController {

    @FXML
    private TableView<LogEntry> tableLogs;
    @FXML
    private TableColumn<LogEntry, String> colTimestamp;
    @FXML
    private TableColumn<LogEntry, String> colLevel;
    @FXML
    private TableColumn<LogEntry, String> colSource;
    @FXML
    private TableColumn<LogEntry, String> colMessage;
    @FXML
    private TableColumn<LogEntry, String> colUser;
    @FXML
    private TableColumn<LogEntry, String> colSrcIp;

    @FXML
    private DatePicker dateFrom;
    @FXML
    private DatePicker dateTo;
    @FXML
    private TextField timeFrom;
    @FXML
    private TextField timeTo;
    @FXML
    private ComboBox<String> comboLevel;
    @FXML
    private TextField txtSource;
    @FXML
    private TextField txtUser;
    @FXML
    private TextField txtIp;

    @FXML
    private void initialize() {
        comboLevel.setItems(FXCollections.observableArrayList(
                "None", "INFO", "WARN", "ERROR", "DEBUG", "CRITICAL", "ALERT", "NOTICE", "TRACE"));

        LogTimestampFormatter timestampFormatter = new LogTimestampFormatter();
        colTimestamp.setCellValueFactory(cellData -> {
            var t = cellData.getValue().getTimestamp();
            String formatted = t != null ? timestampFormatter.format(t) : "";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("source"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("user"));
        colSrcIp.setCellValueFactory(new PropertyValueFactory<>("srcIp"));
    }

    @FXML
    private void onOpenLogFile() { }

    @FXML
    private void onUserManagement() { }

    @FXML
    private void onLogout() { }

    @FXML
    private void onExit() {
        javafx.stage.Stage stage = (javafx.stage.Stage) tableLogs.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onAbout() { }

    @FXML
    private void onApplyFilter() { }

    @FXML
    private void onClearFilter() { }

    @FXML
    private void onGraphView() { }

    @FXML
    private void onReports() { }
}
