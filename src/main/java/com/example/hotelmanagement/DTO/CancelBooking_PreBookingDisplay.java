package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.Models.Prebooking;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CancelBooking_PreBookingDisplay {
    private StringProperty customerName;
    private IntegerProperty roomNumber;
    private StringProperty start_endDate;
    private StringProperty deposit;
    private StringProperty roomType;

    public CancelBooking_PreBookingDisplay(Prebooking prebooking) {
        customerName = new SimpleStringProperty(prebooking.getCustomerName());
        roomNumber = new SimpleIntegerProperty(prebooking.getRoomID().getRoomNumber());
        LocalDate checkInDate = prebooking.getCheckInDate().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate checkOutDate = prebooking.getCheckOutDate().atZone(ZoneOffset.UTC).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String checkInDateStr = checkInDate.format(formatter);
        String checkOutDateStr = checkOutDate.format(formatter);
        start_endDate = new SimpleStringProperty(checkInDateStr + " - " + checkOutDateStr);
        BigDecimal dps = prebooking.getPrice();
        NumberFormat vndFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formatted = vndFormat.format(dps);
        deposit = new SimpleStringProperty(formatted + " VNĐ");
        roomType = new SimpleStringProperty(prebooking.getRoomID().getRoomTypeID().getTypeName());
    }

    public String getCustomerName() {return customerName.get();}
    public StringProperty customerNameProperty() {return customerName;}
    public int getRoomNumber() {return roomNumber.get();}
    public IntegerProperty roomNumberProperty() {return roomNumber;}
    public String getStart_endDate() {return start_endDate.get();}public StringProperty start_endDateProperty() {return start_endDate;}
    public String getDeposit() {return deposit.get();}
    public StringProperty depositProperty() {return deposit;}
    public String getRoomType() {return roomType.get();}
    public StringProperty roomTypeProperty() {return roomType;}
    public void setCustomerName(String customerName) {this.customerName.set(customerName);}
    public void setRoomNumber(int roomNumber) {this.roomNumber.set(roomNumber);}
    public void setStart_endDate(String start_endDate) {this.start_endDate.set(start_endDate);}
    public void setDeposit(String deposit) {this.deposit.set(deposit);}
    public void setRoomType(String roomType) {this.roomType.set(roomType);}
}
