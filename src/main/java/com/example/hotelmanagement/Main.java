package com.example.hotelmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
//        Scene scene = new Scene(fxmlLoader.load(), 10360, 820);

        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/CustomerManagementView.fxml"));
        //Parent root = loader.load();

        //Scene scene = new Scene(root, 1060, 660);
        scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
        System.out.println(getClass().getResource("/CSS/reservation-style.css"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}