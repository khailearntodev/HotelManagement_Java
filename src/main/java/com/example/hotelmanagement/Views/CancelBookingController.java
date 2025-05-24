package com.example.hotelmanagement.Views;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CancelBookingController {
    @FXML private VBox customerInforVBox;
    @FXML private VBox cancelBookingVBox;


    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) cancelBookingVBox.getScene().getWindow();
        stage.close();
    }

    public void showInfo(MouseEvent mouseEvent) {
        customerInforVBox.setVisible(true);
        customerInforVBox.setManaged(true);Stage stage = (Stage) cancelBookingVBox.getScene().getWindow();
        stage.sizeToScene();
    }
}
