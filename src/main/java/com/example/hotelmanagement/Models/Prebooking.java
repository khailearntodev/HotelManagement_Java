package com.example.hotelmanagement.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "PREBOOKING")
public class Prebooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "BookingCode", nullable = false, length = 8)
    private String bookingCode;

    @Nationalized
    @Column(name = "CustomerName", nullable = false, length = 50)
    private String customerName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InvoiceID", nullable = false)
    private Invoice invoiceID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReservationID")
    private com.example.hotelmanagement.Models.Reservation reservationID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoomID", nullable = false)
    private com.example.hotelmanagement.Models.Room roomID;

    @Nationalized
    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @ColumnDefault("sysdatetime()")
    @Column(name = "CheckInDate", nullable = false)
    private Instant checkInDate;

    @ColumnDefault("sysdatetime()")
    @Column(name = "CheckOutDate", nullable = false)
    private Instant checkOutDate;

    @ColumnDefault("sysdatetime()")
    @Column(name = "BookingDate", nullable = false)
    private Instant bookingDate;

    @Column(name = "Price", precision = 20)
    private BigDecimal price;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

}