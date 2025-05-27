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
@Table(name = "ROOM")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoomID", nullable = false)
    private Integer id;

    @Column(name = "RoomNumber", nullable = false)
    private Integer roomNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoomTypeID", nullable = false)
    private com.example.hotelmanagement.Models.Roomtype roomTypeID;

    @Nationalized
    @Lob
    @Column(name = "Note")
    private String note;

    @ColumnDefault("0")
    @Column(name = "Status", nullable = false)
    private Integer status;

    @ColumnDefault("0")
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;

    @ColumnDefault("0")
    @Column(name = "CleaningStatus", nullable = false)
    private Integer cleaningStatus;

    @OneToMany(mappedBy = "roomID")
    private Set<Prebooking> prebookings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "roomID")
    private Set<Reservation> reservations = new LinkedHashSet<>();

}