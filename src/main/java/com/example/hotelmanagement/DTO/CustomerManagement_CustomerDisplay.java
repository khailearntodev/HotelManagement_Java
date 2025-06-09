package com.example.hotelmanagement.DTO;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CustomerManagement_CustomerDisplay {
    @Getter
    private int customerId;
    @Getter
    private String customerName;
    @Getter
    private String customerType;
    @Getter
    private Instant birthday;
    @Getter
    private String identityNumber;
    @Getter
    private String phoneNumber;
    @Getter
    private String status;
    @Getter
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

    public boolean getGender()
    {
        return gender;
    }
    public String getFormattedBirthday() {
        if (birthday == null) return "";
        LocalDate localDate = birthday.atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
