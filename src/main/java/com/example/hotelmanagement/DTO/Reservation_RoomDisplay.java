package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Models.Roomtype;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Reservation_RoomDisplay {
    private final IntegerProperty id;
    private final IntegerProperty roomNumber;
    private final StringProperty note;
    private final IntegerProperty status;
    private final IntegerProperty quantity;
    private final BooleanProperty isDeleted;
    private final IntegerProperty cleaningStatus;
    private final ObjectProperty<Roomtype> roomType;
    private final StringProperty roomTypeName;
    private final ObjectProperty<BigDecimal> roomPrice;
    private final StringProperty checkInOutDate;

    public Reservation_RoomDisplay(Room room) {
        this.id = new SimpleIntegerProperty(room.getId());
        this.roomNumber = new SimpleIntegerProperty(room.getRoomNumber());
        this.note = new SimpleStringProperty(room.getNote());
        this.status = new SimpleIntegerProperty(room.getStatus());
        this.isDeleted = new SimpleBooleanProperty(room.getIsDeleted());
        this.cleaningStatus = new SimpleIntegerProperty(room.getCleaningStatus());
        this.roomType = new SimpleObjectProperty<>(room.getRoomTypeID());
        this.roomTypeName = new SimpleStringProperty(room.getRoomTypeID().getTypeName());

        Reservation reservation = room.getReservations().stream().filter(e -> e.getInvoiceID() == null).findFirst().orElse(null);
        if (reservation == null) {
            this.roomPrice = new SimpleObjectProperty<>(room.getRoomTypeID().getBasePrice());
            this.quantity = new SimpleIntegerProperty(0);
            this.checkInOutDate = new SimpleStringProperty("");
        } else {
            this.roomPrice = new SimpleObjectProperty<>(reservation.getPrice());
            int quantity = reservation.getReservationguests().size();
            this.quantity = new SimpleIntegerProperty(quantity);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            this.checkInOutDate = new SimpleStringProperty(reservation.getCheckInDate().atZone(ZoneOffset.UTC).toLocalDate().format(formatter) + " - " + reservation.getCheckOutDate().atZone(ZoneOffset.UTC).toLocalDate().format(formatter));
        }
    }

    public IntegerProperty idProperty() { return id; }
    public IntegerProperty roomNumberProperty() { return roomNumber; }
    public StringProperty noteProperty() { return note; }
    public IntegerProperty statusProperty() { return status; }
    public IntegerProperty quantityProperty() { return quantity; }
    public BooleanProperty isDeletedProperty() { return isDeleted; }
    public IntegerProperty cleaningStatusProperty() { return cleaningStatus; }
    public ObjectProperty<Roomtype> roomTypeProperty() { return roomType; }
    public ObjectProperty<BigDecimal> roomPriceProperty() { return roomPrice; }
    public StringProperty checkInOutDateProperty() { return checkInOutDate; }
    public StringProperty roomTypeNameProperty() { return roomTypeName; }

    public int getId() { return id.get(); }
    public int getRoomNumber() { return roomNumber.get(); }
    public String getNote() { return note.get(); }
    public int getStatus() { return status.get(); }

    public int getQuantity() { return quantity.get(); }
    public boolean getIsDeleted() { return isDeleted.get(); }
    public int getCleaningStatus() { return cleaningStatus.get(); }
    public Roomtype getRoomType() { return roomType.get(); }
    public BigDecimal getPrice() {return roomPrice.get(); }
    public String getCheckInOutDate() { return checkInOutDate.get(); }

    public void setStatus(int status) {this.status.set(status);}
    public void setQuantity(int quantity) {this.quantity.set(quantity);}
    public void setCheckInOutDate(String checkInOutDate) {this.checkInOutDate.set(checkInOutDate);}
}
