package com.logmonitoring.controller;

import com.logmonitoring.application.MainApplication;
import com.logmonitoring.model.LogEntry;
import com.logmonitoring.service.FilterService;
import com.logmonitoring.util.LogTimestampFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;

/**
 * Controller for Reports (Flagged Logs). Back and Logout do not clear logs.
 * Add Note, Review Saved Notes, Export are stubs until later steps.
 */
public class LogDetailsController {

    @FXML
    private TableView<LogEntry> tableFlaggedLogs;
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
    private TableColumn<LogEntry, String> colNote;
    @FXML
    private TableColumn<LogEntry, String> colStatus;

    @FXML
    private TextArea txtNote;
    @FXML
    private ComboBox<String> comboExportFormat;

    private ObservableList<LogEntry> flaggedLogs;

    @FXML
    private void initialize() {
        LogTimestampFormatter timestampFormatter = new LogTimestampFormatter();
        colTimestamp.setCellValueFactory(cellData -> {
            LocalDateTime t = cellData.getValue().getTimestamp();
            String formatted = t != null ? timestampFormatter.format(t) : "";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("source"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("user"));
        colSrcIp.setCellValueFactory(new PropertyValueFactory<>("srcIp"));
        colNote.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(""));
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(""));

        comboExportFormat.setItems(FXCollections.observableArrayList("Text", "JSON", "CSV"));
        comboExportFormat.setValue("Text");

        flaggedLogs = FXCollections.observableArrayList();
        tableFlaggedLogs.setItems(flaggedLogs);
    }

    /**
     * Refreshes data when window is shown. Loads filtered logs from FilterService.
     */
    public void refreshData() {
        FilterService.getInstance().applyFilters();
        flaggedLogs = FilterService.getInstance().getFilteredLogs();
        if (flaggedLogs == null) {
            flaggedLogs = FXCollections.observableArrayList();
        }
        tableFlaggedLogs.setItems(flaggedLogs);
        tableFlaggedLogs.refresh();
    }

    @FXML
    private void onAddNote() {
        // Stub until STEP 20
    }

    @FXML
    private void onReviewSavedNotes() {
        // Stub until STEP 20
    }

    @FXML
    private void onExportReport() {
        // Stub until STEP 21
    }

    @FXML
    private void onBack() {
        try {
            MainApplication.getInstance().showMainView();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to return to main window");
            alert.showAndWait();
        }
    }

    @FXML
    private void onLogout() {
        try {
            MainApplication.getInstance().showLoginView();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to logout");
            alert.showAndWait();
        }
    }
}
