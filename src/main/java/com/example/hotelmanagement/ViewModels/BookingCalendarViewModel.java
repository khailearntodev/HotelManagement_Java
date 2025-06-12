package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.PrebookingDAO;
import com.example.hotelmanagement.Models.Room;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BookingCalendarViewModel {
    @Getter
    private ObjectProperty<LocalDate> checkOutDate = new SimpleObjectProperty<>();
    public void setCheckOutDate(LocalDate date) {
        this.checkOutDate.set(date);
        if (parent != null) {
            parent.setCheckOutDate(this.checkOutDate);
        }
    }

    @Setter
    @Getter
    private BookingViewModel parent;

    @Getter
    @Setter
    private Set<LocalDate> unavailableDates = new HashSet<>();

    public BookingCalendarViewModel(Room room) {
        var allPrebookings = new PrebookingDAO().getAll().stream()
                .filter(p -> !p.getIsDeleted())
                .filter(p -> Objects.equals(p.getRoomID().getId(), room.getId()))
                .filter(p -> !p.getCheckInDate().atZone(ZoneOffset.UTC).toLocalDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());

        Set<LocalDate> result = new HashSet<>();

        for (var booking : allPrebookings) {
            LocalDate start = booking.getCheckInDate().atZone(ZoneOffset.UTC).toLocalDate();
            LocalDate end = booking.getCheckOutDate().atZone(ZoneOffset.UTC).toLocalDate();

            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                result.add(date);
            }
        }
        this.unavailableDates = result;
    }
}
