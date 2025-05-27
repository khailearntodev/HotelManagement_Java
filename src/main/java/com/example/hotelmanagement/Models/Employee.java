package com.example.hotelmanagement.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "EMPLOYEE")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmployeeID", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "FullName", nullable = false, length = 50)
    private String fullName;

    @Column(name = "IdentityNumber", nullable = false, length = 12)
    private String identityNumber;

    @Nationalized
    @Column(name = "PhoneNumber", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "StartingDate")
    private Instant startingDate;

    @Nationalized
    @Column(name = "Email", length = 100)
    private String email;

    @Nationalized
    @Column(name = "ContractType", nullable = false, length = 50)
    private String contractType;

    @Column(name = "ContractDate", nullable = false)
    private Instant contractDate;

    @Nationalized
    @Column(name = "Address", nullable = false, length = 500)
    private String address;

    @Column(name = "SalaryRate", nullable = false, precision = 20)
    private BigDecimal salaryRate;

    @Nationalized
    @Lob
    @Column(name = "Avatar")
    private String avatar;

    @Column(name = "DateOfBirth", nullable = false)
    private Instant dateOfBirth;

    @Column(name = "Gender", nullable = false)
    private Boolean gender = false;

    @Nationalized
    @Column(name = "\"Position\"", nullable = false, length = 50)
    private String position;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "employeeID")
    private Set<com.example.hotelmanagement.Models.Invoice> invoices = new LinkedHashSet<>();

    @OneToMany(mappedBy = "employeeID")
    private Set<com.example.hotelmanagement.Models.Reservation> reservations = new LinkedHashSet<>();

    @OneToMany(mappedBy = "employeeID")
    private Set<com.example.hotelmanagement.Models.Useraccount> useraccounts = new LinkedHashSet<>();

}