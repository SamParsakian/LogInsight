package com.logmonitoring.controller;

import com.logmonitoring.application.MainApplication;
import com.logmonitoring.model.LogEntry;
import com.logmonitoring.service.FilterService;
import com.logmonitoring.service.FlaggedLogDao;
import com.logmonitoring.util.LogTimestampFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Reports (Flagged Logs). Back and Logout do not clear logs.
 * Main table does NOT auto-load DB notes (quirk); notes only in Review Saved Notes dialog.
 */
public class LogDetailsController implements Refreshable {

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
    private Map<LogEntry, String> logNotes;
    private Map<LogEntry, Boolean> logReviewStatus;
    private FlaggedLogDao flaggedLogDao;

    @FXML
    private void initialize() {
        logNotes = new HashMap<>();
        logReviewStatus = new HashMap<>();
        flaggedLogDao = new FlaggedLogDao();
        flaggedLogDao.createTableIfNotExists();

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
        colNote.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(logNotes.getOrDefault(cellData.getValue(), "")));
        colStatus.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(logReviewStatus.getOrDefault(cellData.getValue(), false) ? "For Review" : ""));

        comboExportFormat.setItems(FXCollections.observableArrayList("Text", "JSON", "CSV"));
        comboExportFormat.setValue("Text");

        flaggedLogs = FXCollections.observableArrayList();
        tableFlaggedLogs.setItems(flaggedLogs);

        tableFlaggedLogs.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtNote.setText(logNotes.getOrDefault(newVal, ""));
            }
        });
    }

    @Override
    public void refreshData() {
        FilterService.getInstance().applyFilters();
        loadFlaggedLogs();
        tableFlaggedLogs.setItems(flaggedLogs);
        tableFlaggedLogs.refresh();
    }

    private void loadFlaggedLogs() {
        flaggedLogs = FXCollections.observableArrayList();
        ObservableList<LogEntry> filtered = FilterService.getInstance().getFilteredLogs();
        if (filtered != null) {
            flaggedLogs.addAll(filtered);
        }
    }

    @FXML
    private void onAddNote() {
        LogEntry selected = tableFlaggedLogs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setContentText("Please select a log entry first");
            alert.showAndWait();
            return;
        }
        String note = txtNote.getText() != null ? txtNote.getText().trim() : "";
        boolean reviewStatus = false;

        logNotes.put(selected, note);
        logReviewStatus.put(selected, reviewStatus);
        flaggedLogDao.saveOrUpdateFlaggedLog(selected, note, reviewStatus);

        tableFlaggedLogs.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Note Saved");
        alert.setContentText("Note has been saved to the selected log entry");
        alert.showAndWait();
    }

    @FXML
    private void onReviewSavedNotes() {
        List<FlaggedLogDao.FlaggedLogRecord> savedRecords = flaggedLogDao.loadAllFlaggedLogs();
        if (savedRecords.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Saved Notes");
            alert.setContentText("No saved notes found in database.");
            alert.showAndWait();
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Saved Notes Review");
        dialog.setHeaderText("Found " + savedRecords.size() + " saved note(s)");

        TableView<FlaggedLogDao.FlaggedLogRecord> savedTable = new TableView<>();

        TableColumn<FlaggedLogDao.FlaggedLogRecord, String> colTs = new TableColumn<>("Timestamp");
        colTs.setCellValueFactory(cellData -> {
            LocalDateTime t = cellData.getValue().getLogEntry().getTimestamp();
            return new javafx.beans.property.SimpleStringProperty(t != null ? t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
        });
        TableColumn<FlaggedLogDao.FlaggedLogRecord, String> colLev = new TableColumn<>("Level");
        colLev.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLogEntry().getLevel()));
        TableColumn<FlaggedLogDao.FlaggedLogRecord, String> colSrc = new TableColumn<>("Source");
        colSrc.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLogEntry().getSource()));
        TableColumn<FlaggedLogDao.FlaggedLogRecord, String> colMsg = new TableColumn<>("Message");
        colMsg.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLogEntry().getMessage()));
        TableColumn<FlaggedLogDao.FlaggedLogRecord, String> colN = new TableColumn<>("Note");
        colN.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNote() != null ? cellData.getValue().getNote() : ""));
        TableColumn<FlaggedLogDao.FlaggedLogRecord, String> colSt = new TableColumn<>("Status");
        colSt.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().isReviewStatus() ? "For Review" : ""));

        savedTable.getColumns().add(colTs);
        savedTable.getColumns().add(colLev);
        savedTable.getColumns().add(colSrc);
        savedTable.getColumns().add(colMsg);
        savedTable.getColumns().add(colN);
        savedTable.getColumns().add(colSt);
        savedTable.setItems(FXCollections.observableArrayList(savedRecords));
        savedTable.setPrefWidth(800);
        savedTable.setPrefHeight(400);

        dialog.getDialogPane().setContent(savedTable);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    private void onExportReport() {
        String format = comboExportFormat.getValue();
        if (format == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Report");

        String extension;
        String formatName;
        switch (format) {
            case "Text":
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
                extension = ".txt";
                formatName = "Text";
                break;
            case "JSON":
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
                extension = ".json";
                formatName = "JSON";
                break;
            case "CSV":
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
                extension = ".csv";
                formatName = "CSV";
                break;
            default:
                return;
        }

        fileChooser.setInitialFileName("log_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + extension);
        Stage stage = (Stage) tableFlaggedLogs.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                switch (format) {
                    case "Text":
                        exportAsText(writer);
                        break;
                    case "JSON":
                        exportAsJSON(writer);
                        break;
                    case "CSV":
                        exportAsCSV(writer);
                        break;
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(formatName + " Exported");
                alert.setContentText(formatName + " exported successfully to: " + file.getName());
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Failed to export " + formatName + ": " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private static final DateTimeFormatter EXPORT_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private void exportAsText(FileWriter writer) throws IOException {
        writer.write("Log Monitoring Tool - Report\n");
        writer.write("Generated: " + LocalDateTime.now().format(EXPORT_DT) + "\n");
        writer.write("=".repeat(50) + "\n\n");
        for (LogEntry log : flaggedLogs) {
            writer.write("Timestamp: " + (log.getTimestamp() != null ? log.getTimestamp().format(EXPORT_DT) : "") + "\n");
            writer.write("Level: " + nullToEmpty(log.getLevel()) + "\n");
            writer.write("Source: " + nullToEmpty(log.getSource()) + "\n");
            writer.write("Message: " + nullToEmpty(log.getMessage()) + "\n");
            writer.write("User: " + nullToEmpty(log.getUser()) + "\n");
            writer.write("IP: " + nullToEmpty(log.getSrcIp()) + "\n");
            String note = logNotes.getOrDefault(log, "");
            if (!note.isEmpty()) writer.write("Note: " + note + "\n");
            if (logReviewStatus.getOrDefault(log, false)) writer.write("Status: Marked for Review\n");
            writer.write("-".repeat(50) + "\n");
        }
    }

    private void exportAsJSON(FileWriter writer) throws IOException {
        writer.write("{\n  \"report\": {\n");
        writer.write("    \"generated\": \"" + LocalDateTime.now().format(EXPORT_DT) + "\",\n");
        writer.write("    \"entries\": [\n");
        boolean first = true;
        for (LogEntry log : flaggedLogs) {
            if (!first) writer.write(",\n");
            first = false;
            writer.write("      {\n");
            writer.write("        \"timestamp\": \"" + (log.getTimestamp() != null ? log.getTimestamp().format(EXPORT_DT) : "") + "\",\n");
            writer.write("        \"level\": \"" + escapeJson(nullToEmpty(log.getLevel())) + "\",\n");
            writer.write("        \"source\": \"" + escapeJson(nullToEmpty(log.getSource())) + "\",\n");
            writer.write("        \"message\": \"" + escapeJson(nullToEmpty(log.getMessage())) + "\",\n");
            writer.write("        \"user\": \"" + escapeJson(nullToEmpty(log.getUser())) + "\",\n");
            writer.write("        \"srcIp\": \"" + escapeJson(nullToEmpty(log.getSrcIp())) + "\",\n");
            writer.write("        \"note\": \"" + escapeJson(logNotes.getOrDefault(log, "")) + "\",\n");
            writer.write("        \"status\": \"" + (logReviewStatus.getOrDefault(log, false) ? "For Review" : "") + "\"\n");
            writer.write("      }");
        }
        writer.write("\n    ]\n  }\n}\n");
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void exportAsCSV(FileWriter writer) throws IOException {
        writer.write("Timestamp,Level,Source,Message,User,Source IP,Note,Status\n");
        for (LogEntry log : flaggedLogs) {
            writer.write("\"" + (log.getTimestamp() != null ? log.getTimestamp().format(EXPORT_DT) : "") + "\",");
            writer.write("\"" + csvQuoted(nullToEmpty(log.getLevel())) + "\",");
            writer.write("\"" + csvQuoted(nullToEmpty(log.getSource())) + "\",");
            writer.write("\"" + csvQuoted(nullToEmpty(log.getMessage())) + "\",");
            writer.write("\"" + csvQuoted(nullToEmpty(log.getUser())) + "\",");
            writer.write("\"" + csvQuoted(nullToEmpty(log.getSrcIp())) + "\",");
            writer.write("\"" + csvQuoted(logNotes.getOrDefault(log, "")) + "\",");
            writer.write(logReviewStatus.getOrDefault(log, false) ? "\"For Review\"" : "\"\"");
            writer.write("\n");
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String csvQuoted(String s) {
        return (s == null ? "" : s).replace("\"", "\"\"");
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
