package com.example.hotelmanagement.Views;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class AddRoomTypeController implements Initializable {
    public ImageView roomTypeImageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roomTypeImageView.setImage(new Image(getClass().getResource("/Images/phong.jpg").toExternalForm()));
    }
}
