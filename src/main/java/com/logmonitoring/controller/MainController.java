package com.logmonitoring.controller;

import com.logmonitoring.model.LogEntry;
import com.logmonitoring.service.LogDataService;
import com.logmonitoring.service.LogParser;
import com.logmonitoring.util.LogTimestampFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        ObservableList<LogEntry> serviceLogs = LogDataService.getInstance().getAllLogs();
        if (serviceLogs == null || serviceLogs.isEmpty()) {
            List<LogEntry> sample = new ArrayList<>();
            sample.add(new LogEntry(LocalDateTime.now().minusHours(2), "INFO", "web-server", "User login successful", "admin", "192.168.1.100"));
            sample.add(new LogEntry(LocalDateTime.now().minusHours(1), "WARN", "database", "Connection pool nearly full", "system", "10.0.0.1"));
            sample.add(new LogEntry(LocalDateTime.now().minusMinutes(45), "ERROR", "api-service", "Failed to process request", "user123", "192.168.1.50"));
            sample.add(new LogEntry(LocalDateTime.now().minusMinutes(30), "INFO", "web-server", "Page loaded successfully", "guest", "192.168.1.200"));
            sample.add(new LogEntry(LocalDateTime.now().minusMinutes(15), "ERROR", "auth-service", "Authentication failed", "user456", "192.168.1.75"));
            sample.add(new LogEntry(LocalDateTime.now().minusMinutes(10), "INFO", "web-server", "File uploaded", "admin", "192.168.1.100"));
            sample.add(new LogEntry(LocalDateTime.now().minusMinutes(5), "WARN", "database", "Slow query detected", "system", "10.0.0.1"));
            LogDataService.getInstance().setAllLogs(sample);
        }
        tableLogs.setItems(LogDataService.getInstance().getAllLogs());
    }

    @FXML
    private void onOpenLogFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Log File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Log Files", "*.log", "*.txt"));

        Stage stage = (Stage) tableLogs.getScene().getWindow();
        java.io.File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                LogParser parser = new LogParser();
                List<LogEntry> parsedLogs = parser.parseLogFile(file);
                LogDataService.getInstance().setAllLogs(parsedLogs);
                tableLogs.setItems(LogDataService.getInstance().getAllLogs());
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to read log file");
                alert.setContentText("Could not read file: " + file.getName() + "\n" + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onUserManagement() { }

    @FXML
    private void onLogout() { }

    @FXML
    private void onExit() {
        Stage stage = (Stage) tableLogs.getScene().getWindow();
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
