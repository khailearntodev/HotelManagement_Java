package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.Models.Prebooking;
import com.example.hotelmanagement.Models.Room;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Objects;

public class ConfirmBookingCodeViewModel {
    @Setter
    @Getter
    ReservationViewModel parent;
    @Getter
    private Room room;
    @Getter
    private Prebooking prebooking;

    public ConfirmBookingCodeViewModel(Room room) {
        this.room = room;
        Prebooking preBooking = room.getPrebookings().stream()
                .filter(e -> e.getReservationID() == null)
                .filter(e -> Objects.equals(e.getRoomID().getId(), room.getId()))
                .filter(e -> !e.getIsDeleted())
                .filter(e -> {
                    LocalDate checkIn = e.getCheckInDate().atZone(ZoneOffset.UTC).toLocalDate();
                    LocalDate checkOut = e.getCheckOutDate().atZone(ZoneOffset.UTC).toLocalDate();
                    return !checkIn.isAfter(LocalDate.now()) && !checkOut.isBefore(LocalDate.now());
                })
                .min(Comparator.comparing(Prebooking::getCheckInDate))
                .orElse(null);
        this.prebooking = preBooking;
    }

    public boolean hasActivePrebooking(String code) {
        return prebooking.getBookingCode().equals(code);
    }
}