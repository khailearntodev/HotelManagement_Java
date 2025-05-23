package com.example.hotelmanagement.Views;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class BookingNoteController implements Initializable {
    @FXML private VBox bookingNoteVBox;

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) bookingNoteVBox.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> bookingNoteVBox.requestFocus());
    }
}
