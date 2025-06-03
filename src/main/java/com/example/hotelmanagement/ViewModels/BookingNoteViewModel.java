package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.ReservationDAO;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Models.Room;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class BookingNoteViewModel {
    Room room;
    @Setter
    ReservationViewModel parent;
    @Getter
    private StringProperty bookingNote = new SimpleStringProperty();
    private Reservation reservation;

    public BookingNoteViewModel(Room room) {
        this.room = room;
        reservation = new ReservationDAO().getAll().stream().filter(e -> e.getInvoiceID() == null && Objects.equals(room.getId(), e.getRoomID().getId())).findFirst().get();
        bookingNote.set(reservation.getNote());
    }

    public void save() {
        reservation.setNote(bookingNote.get());
        new ReservationDAO().update(reservation);
    }
}
