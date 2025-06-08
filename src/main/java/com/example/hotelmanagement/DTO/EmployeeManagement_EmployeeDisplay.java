package com.example.hotelmanagement.DTO;

import java.math.BigDecimal;
import java.time.Instant;

public class EmployeeManagement_EmployeeDisplay {
    private int employeeId;
    private String employeeName;
    private Instant birthday;
    private String identityNumber;
    private String phoneNumber;
    private String address;
    private boolean gender;
    private Instant startingDate;
    private String email;
    private String contractType;
    private Instant contractDate;
    private BigDecimal salaryRate;
    private String avatar;
    private String position;

    public EmployeeManagement_EmployeeDisplay(int employeeId, String employeeName,
                                              Instant birthday, String identityNumber,
                                              String phoneNumber, String address,
                                              boolean gender, Instant startingDate,
                                              String email, String contractType,
                                              Instant contractDate, BigDecimal salaryRate,
                                              String avatar, String position) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.birthday = birthday;
        this.identityNumber = identityNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.startingDate = startingDate;
        this.email = email;
        this.contractType = contractType;
        this.contractDate = contractDate;
        this.salaryRate = salaryRate;
        this.avatar = avatar;
        this.position = position;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
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

    public String getAddress() {
        return address;
    }

    public boolean isGender() {
        return gender;
    }

    public Instant getStartingDate() {
        return startingDate;
    }

    public String getEmail() {
        return email;
    }

    public String getContractType() {
        return contractType;
    }

    public Instant getContractDate() {
        return contractDate;
    }

    public BigDecimal getSalaryRate() {
        return salaryRate;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getPosition() {
        return position;
    }
}
