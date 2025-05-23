package com.example.hotelmanagement.Views;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConfirmBookingCodeController {
    @FXML private VBox confirmBookingCodeVBox;

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) confirmBookingCodeVBox.getScene().getWindow();
        stage.close();
    }
}
