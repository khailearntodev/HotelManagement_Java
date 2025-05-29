package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DTO.ReservationRoomDisplay;
import com.example.hotelmanagement.Models.Customer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class BookingViewModel {
    @Getter
    private ReservationRoomDisplay room;
    @Setter
    private ReservationViewModel parent;
    @Getter
    private ObservableList<Customer> customerList;

    public BookingViewModel(ReservationRoomDisplay room) {
        this.room = room;
        this.customerList = FXCollections.observableArrayList();
    }

    @Getter
    @Setter
    private ObjectProperty<LocalDate> checkOutDate = new SimpleObjectProperty<>();

}
