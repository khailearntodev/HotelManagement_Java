package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Models.Roomtype; // Import the Roomtype model
import javafx.beans.property.*;
import java.math.BigDecimal;

public class RoomViewModel {
    private final IntegerProperty id;
    private final IntegerProperty roomNumber;
    private final StringProperty roomTypeName; // To display the room type name
    private final ObjectProperty<BigDecimal> roomTypeBasePrice; // To display the room type base price
    private final StringProperty note;
    private final IntegerProperty status;
    private final IntegerProperty cleaningStatus;
    private final BooleanProperty isDeleted;

    /**
     * Constructor to create a RoomViewModel from a Room entity.
     *
     * @param room The Room entity to wrap in the view model.
     */
    public RoomViewModel(Room room) {
        this.id = new SimpleIntegerProperty(room.getId());
        this.roomNumber = new SimpleIntegerProperty(room.getRoomNumber());

        // Handle the associated Roomtype
        Roomtype associatedRoomType = room.getRoomTypeID(); // Assuming getRoomTypeID() returns the Roomtype object
        this.roomTypeName = new SimpleStringProperty(
                associatedRoomType != null ? associatedRoomType.getTypeName() : "N/A"
        );
        this.roomTypeBasePrice = new SimpleObjectProperty<>(
                associatedRoomType != null ? associatedRoomType.getBasePrice() : BigDecimal.ZERO // Or null, depending on your preference
        );

        this.note = new SimpleStringProperty(room.getNote());
        this.status = new SimpleIntegerProperty(room.getStatus());
        this.cleaningStatus = new SimpleIntegerProperty(room.getCleaningStatus());
        this.isDeleted = new SimpleBooleanProperty(room.getIsDeleted());
    }

    // --- Property Getters ---

    public IntegerProperty idProperty() {
        return id;
    }

    public IntegerProperty roomNumberProperty() {
        return roomNumber;
    }

    public StringProperty roomTypeNameProperty() {
        return roomTypeName;
    }

    public ObjectProperty<BigDecimal> roomTypeBasePriceProperty() {
        return roomTypeBasePrice;
    }

    public StringProperty noteProperty() {
        return note;
    }

    public IntegerProperty statusProperty() {
        return status;
    }

    public IntegerProperty cleaningStatusProperty() {
        return cleaningStatus;
    }

    public BooleanProperty isDeletedProperty() {
        return isDeleted;
    }

    // --- Simple Getters (for convenience/MFXTableColumn) ---

    public Integer getId() {
        return id.get();
    }

    public Integer getRoomNumber() {
        return roomNumber.get();
    }

    public String getRoomTypeName() {
        return roomTypeName.get();
    }

    public BigDecimal getRoomTypeBasePrice() {
        return roomTypeBasePrice.get();
    }

    public String getNote() {
        return note.get();
    }

    public Integer getStatus() {
        return status.get();
    }

    public Integer getCleaningStatus() {
        return cleaningStatus.get();
    }

    public Boolean getIsDeleted() {
        return isDeleted.get();
    }
}
