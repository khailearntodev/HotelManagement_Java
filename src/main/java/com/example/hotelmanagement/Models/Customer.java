package com.example.hotelmanagement.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "CUSTOMER", uniqueConstraints = {
        @UniqueConstraint(name = "UQ__CUSTOMER__6354A73F54C4EF7E", columnNames = {"IdentityNumber"})
})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerID", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "FullName", nullable = false, length = 50)
    private String fullName;

    @Column(name = "DateOfBirth", nullable = false)
    private Instant dateOfBirth;

    @Column(name = "IdentityNumber", nullable = false, length = 12)
    private String identityNumber;

    @Column(name = "Gender", nullable = false)
    private Boolean gender = false;

    @Nationalized
    @ColumnDefault("'CCCD'")
    @Column(name = "IdentityType", nullable = false, length = 10)
    private String identityType;

    @Nationalized
    @Column(name = "PhoneNumber", nullable = false, length = 20)
    private String phoneNumber;

    @Nationalized
    @Lob
    @Column(name = "CustomerAddress", nullable = false)
    private String customerAddress;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CustomerTypeID", nullable = false)
    private com.example.hotelmanagement.Models.Customertype customerTypeID;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "customerID")
    private Set<com.example.hotelmanagement.Models.Reservationguest> reservationguests = new LinkedHashSet<>();

}