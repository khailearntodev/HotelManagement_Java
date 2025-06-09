package com.example.hotelmanagement.DTO;

import java.time.Instant;

public class Dashboard_ActivityItem {
    private String description;
    private int roomNumber;
    private Instant timestamp;

    public Dashboard_ActivityItem(String description, int roomNumber, Instant timestamp) {
        this.description = description;
        this.roomNumber = roomNumber;
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }
    public int getRoomNumber() {
        return roomNumber;
    }
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return getFormattedTimestamp() + " - " + description + " – Phòng " + roomNumber;
    }

    public String getFormattedTimestamp() {
        return java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(java.time.ZoneId.systemDefault())
                .format(timestamp);
    }

}
