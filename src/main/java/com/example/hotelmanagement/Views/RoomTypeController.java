package com.example.hotelmanagement.Views;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RoomTypeController implements Initializable {
    public ImageView roomTypeImageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void handleAddRoomType(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddRoomType.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Views/MainWindow.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 1360, 820);

            scene.getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
            System.out.println(getClass().getResource("/CSS/style.css"));
            Stage stage = new Stage();
            stage.setTitle("Add Room Type");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace(); // Or throw a Deadpool-style panic attack
        }
    }
}
