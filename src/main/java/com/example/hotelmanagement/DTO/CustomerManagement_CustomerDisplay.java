package com.example.hotelmanagement.DTO;
import java.time.Instant;

public class CustomerManagement_CustomerDisplay {
    private int customerId;
    private String customerName;
    private String customerType;
    private Instant birthday;
    private String identityNumber;
    private String phoneNumber;
    private String status;
    private String address;
    private boolean gender;
    public CustomerManagement_CustomerDisplay(int customerId, String customerName,
                                              String customerType, Instant birthday, String identityNumber, String phoneNumber,
                                              String status, String address, boolean gender
                           )
    {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerType = customerType;
        this.birthday = birthday;
        this.identityNumber = identityNumber;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.address = address;
        this.gender = gender;
    }
    public int getCustomerId() {
        return customerId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public String getCustomerType() {
        return customerType;
    }
    public Instant getBirthday() {
        return birthday;
    }
    public String getIdentityNumber() {
        return identityNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getStatus() {
        return status;
    }
    public String getAddress() {
        return address;}
    public boolean getGender()
    {
        return gender;
    }

}
