package com.example.hotelmanagement.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "REVENUE_REPORT")
public class RevenueReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportID", nullable = false)
    private Integer id;

    @Column(name = "\"Month\"", nullable = false)
    private Integer month;

    @Column(name = "\"Year\"", nullable = false)
    private Integer year;

    @Column(name = "TotalRental", nullable = false, precision = 20)
    private BigDecimal totalRental;

    @Column(name = "TotalService", nullable = false, precision = 20)
    private BigDecimal totalService;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "reportID")
    private Set<com.example.hotelmanagement.Models.RevenueReportDetail> revenueReportDetails = new LinkedHashSet<>();

}