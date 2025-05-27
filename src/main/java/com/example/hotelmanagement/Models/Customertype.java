package com.example.hotelmanagement.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "CUSTOMERTYPE")
public class Customertype {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerTypeID", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "TypeName", nullable = false, length = 30)
    private String typeName;

    @Column(name = "BonusCharge", nullable = false, precision = 20, scale = 3)
    private BigDecimal bonusCharge;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "customerTypeID")
    private Set<Customer> customers = new LinkedHashSet<>();

}