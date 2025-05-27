package com.example.hotelmanagement.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "AMENITY")
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AmenityID", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "AmenityName", nullable = false, length = 100)
    private String amenityName;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "amenityID")
    private Set<com.example.hotelmanagement.Models.Roomtypeamenity> roomtypeamenities = new LinkedHashSet<>();

}