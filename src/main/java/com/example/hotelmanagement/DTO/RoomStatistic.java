package com.example.hotelmanagement.DTO;

public class RoomStatistic {
    private String typeName;
    private long count;

    public RoomStatistic() {
    }

    public RoomStatistic(String typeName, long count) {
        this.typeName = typeName;
        this.count = count;
    }

    public String getTypeName() {
        return typeName;
    }

    public long getCount() {
        return count;
    }
}
