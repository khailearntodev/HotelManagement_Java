package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Customertype;
import javafx.beans.property.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class CustomerBookingDisplay {
    private final IntegerProperty ordinalNumber = new SimpleIntegerProperty();
    private final StringProperty fullName = new SimpleStringProperty("");
    private final StringProperty identityNumber = new SimpleStringProperty();
    private final StringProperty phoneNumber = new SimpleStringProperty();
    private final StringProperty customerAddress = new SimpleStringProperty();

    public CustomerBookingDisplay(Customer customer, int number) {
        this.ordinalNumber.set(number);
        this.fullName.set(customer.getFullName());
        this.identityNumber.set(customer.getIdentityNumber());
        this.phoneNumber.set(customer.getPhoneNumber());
        this.customerAddress.set(customer.getCustomerAddress());
    }

    public void setOrdinalNumber(int ordinalNumber) {this.ordinalNumber.set(ordinalNumber);}
    public int getOrdinalNumber() {return ordinalNumber.get();}
    public IntegerProperty ordinalNumberProperty() {return ordinalNumber;}
    public String getFullName() {return fullName.get();}
    public StringProperty fullNameProperty() {return fullName;}
    public void setFullName(String fullName) {this.fullName.set(fullName);}
    public String getIdentityNumber() {return identityNumber.get();}
    public StringProperty identityNumberProperty() {return identityNumber;}
    public void setIdentityNumber(String identityNumber) {this.identityNumber.set(identityNumber);}
    public String getPhoneNumber() {return phoneNumber.get();}
    public StringProperty phoneNumberProperty() {return phoneNumber;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber.set(phoneNumber);}
    public String getCustomerAddress() {return customerAddress.get();}
    public StringProperty customerAddressProperty() {return customerAddress;}
    public void setCustomerAddress(String customerAddress) {this.customerAddress.set(customerAddress);}
}
