package com.example.hotelmanagement.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "REVENUE_REPORT_DETAIL")
public class RevenueReportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportDetailID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ReportID", nullable = false)
    private RevenueReport reportID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoomTypeID", nullable = false)
    private com.example.hotelmanagement.Models.Roomtype roomTypeID;

    @Column(name = "TotalReservation", nullable = false)
    private Integer totalReservation;

    @Column(name = "Revenue", nullable = false, precision = 20)
    private BigDecimal revenue;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

}