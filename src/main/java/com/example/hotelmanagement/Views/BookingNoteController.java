package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DAO.ReservationDAO;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.ViewModels.BookingNoteViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class BookingNoteController implements Initializable {
    @FXML private TextArea noteTextField;
    @FXML private VBox bookingNoteVBox;
    BookingNoteViewModel viewModel;

    public void setViewModel(BookingNoteViewModel viewModel) {
        this.viewModel = viewModel;
        Binding();
    }

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) bookingNoteVBox.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> bookingNoteVBox.requestFocus());
    }

    private void Binding() {
        noteTextField.textProperty().bindBidirectional(viewModel.getBookingNote());
    }

    public void handleSaveNote(MouseEvent mouseEvent) {
        viewModel.save();
    }
}
