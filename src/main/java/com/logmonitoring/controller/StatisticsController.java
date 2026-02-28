package com.logmonitoring.controller;

import com.logmonitoring.application.MainApplication;
import com.logmonitoring.model.LogEntry;
import com.logmonitoring.model.LogLevelStatistic;
import com.logmonitoring.service.FilterService;
import com.logmonitoring.service.FilterState;
import com.logmonitoring.service.LogDataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for Graph View (Statistics). Shared FilterService. Logout does not clear logs.
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

    private ObservableList<LogEntry> filteredLogs;

    @FXML
    private void initialize() {
        comboLevel.setItems(FXCollections.observableArrayList(
                "None", "INFO", "WARN", "ERROR", "DEBUG", "CRITICAL", "ALERT", "NOTICE", "TRACE"));
        filteredLogs = FXCollections.observableArrayList();
        refreshData();
    }

    private void loadLogsFromService() {
        // LogDataService is the source; FilterService holds filtered list
    }

    private void loadFilterState() {
        FilterState state = FilterService.getInstance().getFilterState();
        dateFrom.setValue(state.getDateFrom());
        dateTo.setValue(state.getDateTo());
        timeFrom.setText(state.getTimeFrom() != null ? state.getTimeFrom() : "");
        timeTo.setText(state.getTimeTo() != null ? state.getTimeTo() : "");
        String level = state.getLevel();
        comboLevel.setValue(level != null && !level.isEmpty() ? level : "None");
        txtSource.setText(state.getSource() != null ? state.getSource() : "");
        txtUser.setText(state.getUser() != null ? state.getUser() : "");
        txtIp.setText(state.getIp() != null ? state.getIp() : "");
    }

    private void saveFilterState() {
        FilterState state = FilterService.getInstance().getFilterState();
        state.setDateFrom(dateFrom.getValue());
        state.setDateTo(dateTo.getValue());
        state.setTimeFrom(timeFrom.getText() != null ? timeFrom.getText().trim() : null);
        state.setTimeTo(timeTo.getText() != null ? timeTo.getText().trim() : null);
        String level = comboLevel.getValue();
        state.setLevel(level != null && "None".equals(level) ? null : level);
        state.setSource(txtSource.getText() != null ? txtSource.getText().trim() : null);
        state.setUser(txtUser.getText() != null ? txtUser.getText().trim() : null);
        state.setIp(txtIp.getText() != null ? txtIp.getText().trim() : null);
    }

    private void applyFilters() {
        saveFilterState();
        FilterService.getInstance().applyFilters();
        filteredLogs = FilterService.getInstance().getFilteredLogs();
    }

    public void refreshData() {
        loadLogsFromService();
        loadFilterState();
        applyFilters();
        updateCharts();
    }

    private void updateCharts() {
        updateLogsOverTimeChart();
        updateLevelDistributionChart();
        updateTopSourcesChart();
    }

    private void updateLogsOverTimeChart() {
        chartLogsOverTime.getData().clear();
        chartLogsOverTime.setVisible(true);

        CategoryAxis xAxis = (CategoryAxis) chartLogsOverTime.getXAxis();
        ObservableList<String> categories = FXCollections.observableArrayList();
        for (int hour = 0; hour < 24; hour++) {
            categories.add(String.format("%02d:00", hour));
        }
        xAxis.setCategories(categories);
        xAxis.setTickLabelRotation(-90);

        Map<Integer, Integer> logsByHour = new HashMap<>();
        for (LogEntry log : filteredLogs) {
            int hour = log.getTimestamp().getHour();
            logsByHour.put(hour, logsByHour.getOrDefault(hour, 0) + 1);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String selectedLevel = FilterService.getInstance().getFilterState().getLevel();
        series.setName(selectedLevel != null && !selectedLevel.isEmpty() ? selectedLevel : "Total Logs");

        for (int hour = 0; hour < 24; hour++) {
            String timeLabel = String.format("%02d:00", hour);
            series.getData().add(new XYChart.Data<>(timeLabel, logsByHour.getOrDefault(hour, 0)));
        }
        if (!series.getData().isEmpty()) {
            chartLogsOverTime.getData().add(series);
        }
    }

    private void updateLevelDistributionChart() {
        Map<String, Integer> levelCounts = new HashMap<>();
        for (LogEntry log : filteredLogs) {
            String level = log.getLevel();
            if (level != null && !level.isEmpty()) {
                levelCounts.put(level, levelCounts.getOrDefault(level, 0) + 1);
            }
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        levelCounts.forEach((level, count) -> {
            LogLevelStatistic stat = new LogLevelStatistic(level, count);
            if (stat.hasValidLevel()) {
                pieData.add(new PieChart.Data(stat.getDisplayString(), stat.count()));
            }
        });
        chartLevelDistribution.getData().clear();
        if (!pieData.isEmpty()) {
            chartLevelDistribution.setData(pieData);
        }
    }

    private void updateTopSourcesChart() {
        chartTopSources.getData().clear();
        CategoryAxis xAxis = (CategoryAxis) chartTopSources.getXAxis();
        xAxis.setTickLabelRotation(0);

        String selectedLevel = FilterService.getInstance().getFilterState().getLevel();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        ObservableList<String> categories = FXCollections.observableArrayList();

        if (selectedLevel != null && !selectedLevel.isEmpty()) {
            Map<String, Integer> sourceCounts = new HashMap<>();
            for (LogEntry log : filteredLogs) {
                String source = log.getSource();
                if (source != null && !source.isEmpty()) {
                    sourceCounts.put(source, sourceCounts.getOrDefault(source, 0) + 1);
                }
            }
            series.setName(selectedLevel);
            sourceCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> {
                        categories.add(entry.getKey());
                        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                    });
        } else {
            String[] allLevels = {"INFO", "WARN", "ERROR", "DEBUG", "CRITICAL", "ALERT", "NOTICE", "TRACE"};
            Map<String, Integer> levelCounts = new HashMap<>();
            for (LogEntry log : filteredLogs) {
                String level = log.getLevel();
                if (level != null && !level.isEmpty()) {
                    levelCounts.put(level, levelCounts.getOrDefault(level, 0) + 1);
                }
            }
            series.setName("Log Count");
            for (String level : allLevels) {
                categories.add(level);
                series.getData().add(new XYChart.Data<>(level, levelCounts.getOrDefault(level, 0)));
            }
        }

        xAxis.setCategories(categories);
        if (!series.getData().isEmpty()) {
            chartTopSources.getData().add(series);
        }
    }

    @FXML
    private void onApplyFilter() {
        saveFilterState();
        FilterService.getInstance().applyFilters();
        filteredLogs = FilterService.getInstance().getFilteredLogs();
        updateCharts();
    }

    @FXML
    private void onClearFilter() {
        dateFrom.setValue(null);
        dateTo.setValue(null);
        timeFrom.clear();
        timeTo.clear();
        comboLevel.setValue("None");
        txtSource.clear();
        txtUser.clear();
        txtIp.clear();
        FilterService.getInstance().clearFilters();
        filteredLogs = FilterService.getInstance().getFilteredLogs();
        updateCharts();
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
