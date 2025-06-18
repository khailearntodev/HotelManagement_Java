package com.example.hotelmanagement.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hpsf.Decimal;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "SERVICEBOOKING")
public class Servicebooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ServiceBookingID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ReservationID", nullable = false)
    private Reservation reservationID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ServiceID", nullable = false)
    private Service serviceID;

    @Column(name = "ServicePrice", nullable = false, precision = 20)
    private BigDecimal serviceprice;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @ColumnDefault("sysdatetime()")
    @Column(name = "BookingDate", nullable = false)
    private Instant bookingDate;

    @Nationalized
    @ColumnDefault("N'Chưa xử lý'")
    @Column(name = "Status", nullable = false, length = 20)
    private String status;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

}