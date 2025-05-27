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
@Table(name = "INVOICE")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InvoiceID", nullable = false)
    private Integer id;

    @ColumnDefault("sysdatetime()")
    @Column(name = "IssueDate", nullable = false)
    private Instant issueDate;

    @Column(name = "InvoiceType", nullable = false)
    private Integer invoiceType;

    @Column(name = "TotalAmount", nullable = false, precision = 20)
    private BigDecimal totalAmount;

    @Nationalized
    @Column(name = "CustomerName", nullable = false, length = 100)
    private String customerName;

    @Nationalized
    @Lob
    @Column(name = "CustomerAddres", nullable = false)
    private String customerAddres;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EmployeeID")
    private Employee employeeID;

    @Nationalized
    @ColumnDefault("N'Đã thanh toán'")
    @Column(name = "PaymentStatus", nullable = false, length = 20)
    private String paymentStatus;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "invoiceID")
    private Set<com.example.hotelmanagement.Models.Prebooking> prebookings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "invoiceID")
    private Set<com.example.hotelmanagement.Models.Reservation> reservations = new LinkedHashSet<>();

}