package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Models.Roomtype;
import com.example.hotelmanagement.ViewModels.ReservationViewModel;
import javafx.beans.property.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SelectRoom_RoomDisplay {
    @Getter
    private final Reservation reservation;
    private final IntegerProperty roomNumber;
    private final StringProperty roomTypeName;
    private final ObjectProperty<BigDecimal> price;

    public SelectRoom_RoomDisplay(Reservation reservation) {
        this.reservation = reservation;
        this.roomNumber = new SimpleIntegerProperty(reservation.getRoomID().getRoomNumber());
        this.price = new SimpleObjectProperty<>(reservation.getPrice());
        this.roomTypeName = new SimpleStringProperty(reservation.getRoomID().getRoomTypeID().getTypeName());
    }

    public IntegerProperty roomNumberProperty() { return roomNumber; }
    public StringProperty roomTypeNameProperty() { return roomTypeName; }
    public int getRoomNumber() { return roomNumber.get(); }
    public BigDecimal getPrice() {return price.get(); }
    public String getRoomTypeName() {return roomTypeName.get();}
    public BigDecimal getRoomTypePrice() {return price.get();}
    public ObjectProperty<BigDecimal> roomTypePriceProperty() {return price;}
}