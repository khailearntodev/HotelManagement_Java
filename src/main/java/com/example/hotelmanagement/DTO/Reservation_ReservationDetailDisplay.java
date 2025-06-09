package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.DAO.CustomerDAO;
import com.example.hotelmanagement.DAO.ReservationGuestDAO;
import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Models.Reservationguest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Reservation_ReservationDetailDisplay {
    private StringProperty roomNumber = new SimpleStringProperty();
    private StringProperty roomTypeName = new SimpleStringProperty();
    private ObjectProperty<LocalDate> checkInDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> checkOutDate = new SimpleObjectProperty<>();
    private ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    @Setter
    @Getter
    private ObservableList<Booking_CustomerDisplay> customerDisplayList;

    public String getRoomNumber() {return roomNumber.get();}
    public StringProperty roomNumberProperty() {return roomNumber;}
    public String getRoomTypeName() {return roomTypeName.get();}
    public StringProperty roomTypeNameProperty() {return roomTypeName;}
    public LocalDate getCheckInDate() {return checkInDate.get();}
    public ObjectProperty<LocalDate> checkInDateProperty() {return checkInDate;}
    public LocalDate getCheckOutDate() {return checkOutDate.get();}
    public ObjectProperty<LocalDate> checkOutDateProperty() {return checkOutDate;}
    public BigDecimal getPrice() {return price.get();}
    public ObjectProperty<BigDecimal> priceProperty() {return price;}

    public void setRoomNumber(String roomNumber) {this.roomNumber.set(roomNumber);}
    public void setRoomTypeName(String roomTypeName) {this.roomTypeName.set(roomTypeName);}
    public void setCheckInDate(LocalDate checkInDate) {this.checkInDate.set(checkInDate);}
    public void setCheckOutDate(LocalDate checkOutDate) {this.checkOutDate.set(checkOutDate);}
    public void setPrice(BigDecimal price) {this.price.set(price);}

    public Reservation_ReservationDetailDisplay(Reservation reservation) {
        roomNumber.set(reservation.getRoomID().getRoomNumber().toString());
        roomTypeName.set(reservation.getRoomID().getRoomTypeID().getTypeName());
        checkInDate.set(reservation.getCheckInDate().atZone(ZoneOffset.UTC).toLocalDate());
        checkOutDate.set(reservation.getCheckOutDate().atZone(ZoneOffset.UTC).toLocalDate());
        price.set(reservation.getPrice());
        CustomerDAO dao = new CustomerDAO();
        AtomicInteger index = new AtomicInteger(1);

        List<Booking_CustomerDisplay> customerDisplays = new ReservationGuestDAO().getAll().stream()
                .filter(e -> Objects.equals(e.getReservationID().getId(), reservation.getId()))
                .map(e -> new Booking_CustomerDisplay(dao.findById(e.getCustomerID().getId()), index.getAndIncrement()))
                .toList();
        customerDisplayList = FXCollections.observableList(customerDisplays);
    }
}
