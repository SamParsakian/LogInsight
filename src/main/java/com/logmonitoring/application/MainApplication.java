package com.logmonitoring.application;

import com.logmonitoring.service.FlaggedLogDao;
import com.logmonitoring.service.UserDao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private static MainApplication instance;
    private Stage primaryStage;

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;
        this.primaryStage = stage;
        new UserDao().createTableIfNotExists();
        new FlaggedLogDao().createTableIfNotExists();
        showLoginView();
    }

    public void showLoginView() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Log Monitoring Tool - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showMainView() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Log Monitoring Tool");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void showStatisticsView() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/statistics.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Log Monitoring Tool - Graph View");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showReportsView() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/log_details.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Log Monitoring Tool - Reports");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
