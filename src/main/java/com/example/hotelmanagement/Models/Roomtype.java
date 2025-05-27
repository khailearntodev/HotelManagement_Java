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
@Table(name = "ROOMTYPE")
public class Roomtype {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoomTypeID", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "TypeName", nullable = false, length = 20)
    private String typeName;

    @Column(name = "BasePrice", nullable = false, precision = 20)
    private BigDecimal basePrice;

    @Column(name = "MaxOccupancy", nullable = false)
    private Integer maxOccupancy;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @Nationalized
    @Lob
    @Column(name = "Image")
    private String image;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "roomTypeID")
    private Set<RevenueReportDetail> revenueReportDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "roomTypeID")
    private Set<Room> rooms = new LinkedHashSet<>();

    @OneToMany(mappedBy = "roomTypeID")
    private Set<com.example.hotelmanagement.Models.Roomtypeamenity> roomtypeamenities = new LinkedHashSet<>();

}