package com.example.hotelmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Views/StatisticView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1060, 660);

//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Views/MainWindow.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 1360, 820);

        scene.getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
        System.out.println(getClass().getResource("/CSS/style.css"));

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("LOGIN");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}