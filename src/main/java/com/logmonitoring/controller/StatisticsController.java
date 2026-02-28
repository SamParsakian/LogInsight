package com.logmonitoring.controller;

import com.logmonitoring.application.MainApplication;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

/**
 * Controller for Graph View (Statistics). Back and Logout do not clear logs.
 */
public class StatisticsController {

    @FXML
    private LineChart<String, Number> chartLogsOverTime;
    @FXML
    private PieChart chartLevelDistribution;
    @FXML
    private BarChart<String, Number> chartTopSources;

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
        refreshData();
    }

    /**
     * Refreshes data when window is shown. Stub until STEP 17 wires charts.
     */
    public void refreshData() {
        // Will load filter state and update charts in STEP 17
    }

    @FXML
    private void onApplyFilter() { }

    @FXML
    private void onClearFilter() { }

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
