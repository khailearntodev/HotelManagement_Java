package com.example.hotelmanagement.DTO;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class Dashboard_BookingDisplay {
    private final String bookingId;
    private final String customerName;
    private final int roomNumber;
    private final String roomType;
    private final int status;
    private final Instant checkInDate;
    private final Instant checkOutDate;
    private final String sourceType;

    public Dashboard_BookingDisplay(String bookingId, String customerName, int roomNumber,
                                    String roomType, int status, Instant checkInDate,
                                    Instant checkOutDate, String sourceType) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = status;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.sourceType = sourceType;
    }

    public String getStatusText() {
        return switch (status) {
            case 1 -> "Còn trống";
            case 2 -> "Đang được thuê";
            case 3 -> "Được đặt trước";
            default -> "Không xác định";
        };
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getStatus() {
        return status;
    }

    public Instant getCheckInDate() {
        return checkInDate;
    }
    public Instant getCheckOutDate() {
        return checkOutDate;
    }
    public String getSourceType() {
        return sourceType;
    }

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());

    public String getCheckInDateString() {
        return checkInDate != null ? DATE_FORMATTER.format(checkInDate) : "";
    }

    public String getCheckOutDateString() {
        return checkOutDate != null ? DATE_FORMATTER.format(checkOutDate) : "";
    }


}
