package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.Models.Room;
import javafx.beans.property.*;

import java.math.BigDecimal;

public class RoomReservationDisplay {
    private final IntegerProperty id;
    private final IntegerProperty roomNumber;
    private final StringProperty note;
    private final IntegerProperty status;
    private final BooleanProperty isDeleted;
    private final IntegerProperty cleaningStatus;
    private final StringProperty roomTypeName;
    private final ObjectProperty<BigDecimal> roomTypePrice;

    public RoomReservationDisplay(Room room) {
        this.id = new SimpleIntegerProperty(room.getId());
        this.roomNumber = new SimpleIntegerProperty(room.getRoomNumber());
        this.note = new SimpleStringProperty(room.getNote());
        this.status = new SimpleIntegerProperty(room.getStatus());
        this.isDeleted = new SimpleBooleanProperty(room.getIsDeleted());
        this.cleaningStatus = new SimpleIntegerProperty(room.getCleaningStatus());
        this.roomTypeName = new SimpleStringProperty(room.getRoomTypeID().getTypeName());
        this.roomTypePrice = new SimpleObjectProperty<>(room.getRoomTypeID().getBasePrice());
    }

    public IntegerProperty idProperty() { return id; }
    public IntegerProperty roomNumberProperty() { return roomNumber; }
    public StringProperty noteProperty() { return note; }
    public IntegerProperty statusProperty() { return status; }
    public BooleanProperty isDeletedProperty() { return isDeleted; }
    public IntegerProperty cleaningStatusProperty() { return cleaningStatus; }
    public StringProperty roomTypeNameProperty() { return roomTypeName; }
    public ObjectProperty<BigDecimal> roomTypePriceProperty() { return roomTypePrice; }

    public int getId() { return id.get(); }
    public int getRoomNumber() { return roomNumber.get(); }
    public String getNote() { return note.get(); }
    public int getStatus() { return status.get(); }
    public boolean getIsDeleted() { return isDeleted.get(); }
    public int getCleaningStatus() { return cleaningStatus.get(); }
    public String getRoomTypeName() { return roomTypeName.get(); }
    public BigDecimal getPrice() {return roomTypePrice.get(); }
}
