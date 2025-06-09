package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DTO.Reservation_ReservationDetailDisplay;
import com.example.hotelmanagement.Models.Reservation;
import javafx.fxml.Initializable;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class ReservationFormViewModel {
    @Setter
    private ReservationViewModel parent;

    @Getter
    private Reservation_ReservationDetailDisplay reservationDetailDisplay;

    public ReservationFormViewModel(Reservation reservation) {
        reservationDetailDisplay = new Reservation_ReservationDetailDisplay(reservation);
    }
}
