package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.CustomerDAO;
import com.example.hotelmanagement.DAO.ReservationDAO;
import com.example.hotelmanagement.DAO.ReservationGuestDAO;
import com.example.hotelmanagement.DTO.RoomReservationDisplay;
import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Models.Reservationguest;
import com.example.hotelmanagement.Models.Room;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class BookingViewModel {
    @Getter
    private RoomReservationDisplay roomDisplay;
    @Getter
    private Room room;
    @Setter
    private ReservationViewModel parent;
    @Getter
    private ObservableList<Customer> customerList;
    @Getter
    private Reservation reservation;

    public BookingViewModel(Room room) {
        reservation = new Reservation();
        this.room = room;
        this.roomDisplay = new RoomReservationDisplay(room);
        this.customerList = FXCollections.observableArrayList();
    }

    @Getter
    @Setter
    private ObjectProperty<LocalDate> checkOutDate = new SimpleObjectProperty<>();

    public void addReservation()
    {
        ReservationDAO reservationDAO = new ReservationDAO();
        reservationDAO.save(reservation);
        CustomerDAO customerDAO = new CustomerDAO();
        ReservationGuestDAO reservationGuestDAO = new ReservationGuestDAO();
        for(var guest : customerList) {
            Customer customer = customerDAO.findByIdentityNumber(guest.getIdentityNumber(), guest.getIdentityType());
            if (customerDAO.findByIdentityNumber(guest.getIdentityNumber(), guest.getIdentityType()) == null) {
                customerDAO.save(guest);
            } else {
                guest.setId(customer.getId());
                customerDAO.update(guest);
            }
            Reservationguest guestReservation = new Reservationguest();
            guestReservation.setReservationID(reservation);
            guestReservation.setCustomerID(guest);
            reservationGuestDAO.save(guestReservation);
        }
    }
}
