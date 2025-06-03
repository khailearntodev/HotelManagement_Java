package com.example.hotelmanagement.DTO;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PreBookingInvoiceDisplay {
    private IntegerProperty roomNumber = new SimpleIntegerProperty();
    private StringProperty roomTypeName = new SimpleStringProperty();
    private ObjectProperty<LocalDateTime> invoiceDate = new SimpleObjectProperty<>();
    private ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private ObjectProperty<BigDecimal> totalPrice = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty phoneNumber = new SimpleStringProperty();
    private StringProperty address = new SimpleStringProperty();

    public int getRoomNumber() {return roomNumber.get();}
    public IntegerProperty roomNumberProperty() {return roomNumber;}
    public void setRoomNumber(int roomNumber) {this.roomNumber.set(roomNumber);}
    public String getRoomTypeName() {return roomTypeName.get();}
    public StringProperty roomTypeNameProperty() {return roomTypeName;}
    public void setRoomTypeName(String roomTypeName) {this.roomTypeName.set(roomTypeName);}
    public LocalDateTime getInvoiceDate() {return invoiceDate.get();}
    public ObjectProperty<LocalDateTime> invoiceDateProperty() {return invoiceDate;}
    public void setInvoiceDate(LocalDateTime invoiceDate) {this.invoiceDate.set(invoiceDate);}
    public BigDecimal getPrice() {return price.get();}
    public ObjectProperty<BigDecimal> priceProperty() {return price;}
    public void setPrice(BigDecimal price) {this.price.set(price);}
    public LocalDate getStartDate() {return startDate.get();}
    public ObjectProperty<LocalDate> startDateProperty() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate.set(startDate);}
    public LocalDate getEndDate() {return endDate.get();}
    public ObjectProperty<LocalDate> endDateProperty() {return endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate.set(endDate);}
    public BigDecimal getTotalPrice() {return totalPrice.get();}
    public ObjectProperty<BigDecimal> totalPriceProperty() {return totalPrice;}
    public void setTotalPrice(BigDecimal totalPrice) {this.totalPrice.set(totalPrice);}
    public String getName() {return name.get();}
    public StringProperty nameProperty() {return name;}
    public void setName(String name) {this.name.set(name);}
    public String getPhoneNumber() {return phoneNumber.get();}
    public StringProperty phoneNumberProperty() {return phoneNumber;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber.set(phoneNumber);}
    public String getAddress() {return address.get();}
    public StringProperty addressProperty() {return address;}
    public void setAddress(String addreas) {this.address.set(addreas);}
}
