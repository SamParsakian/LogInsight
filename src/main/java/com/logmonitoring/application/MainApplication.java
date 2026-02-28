package com.logmonitoring.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("SysLogAnalyser");
        Scene scene = new Scene(new StackPane(label), 400, 200);
        stage.setTitle("SysLogAnalyser");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
