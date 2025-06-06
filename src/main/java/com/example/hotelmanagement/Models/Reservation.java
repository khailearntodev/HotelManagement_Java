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
@Table(name = "RESERVATION")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReservationID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoomID", nullable = false)
    private com.example.hotelmanagement.Models.Room roomID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InvoiceID")
    private Invoice invoiceID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EmployeeID", nullable = false)
    private Employee employeeID;

    @ColumnDefault("sysdatetime()")
    @Column(name = "CheckInDate", nullable = false)
    private Instant checkInDate;

    @ColumnDefault("sysdatetime()")
    @Column(name = "CheckOutDate", nullable = false)
    private Instant checkOutDate;

    @Nationalized
    @Lob
    @Column(name = "Note")
    private String note;

    @Column(name = "BasePrice", precision = 20)
    private BigDecimal basePrice;

    @Column(name = "Price", precision = 20)
    private BigDecimal price;

    @Column(name = "Total", precision = 20)
    private BigDecimal total;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "reservationID")
    private Set<Prebooking> prebookings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "reservationID")
    private Set<com.example.hotelmanagement.Models.Reservationguest> reservationguests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "reservationID")
    private Set<com.example.hotelmanagement.Models.Servicebooking> servicebookings = new LinkedHashSet<>();

}