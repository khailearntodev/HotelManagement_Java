package com.example.hotelmanagement.Views;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BookingInAdvanceInvoiceController {
    @FXML private VBox bookingInAdvanceInvoiceVBox;

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) bookingInAdvanceInvoiceVBox.getScene().getWindow();
        stage.close();
    }
}
