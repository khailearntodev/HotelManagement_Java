package com.example.hotelmanagement.ViewModels;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
}
