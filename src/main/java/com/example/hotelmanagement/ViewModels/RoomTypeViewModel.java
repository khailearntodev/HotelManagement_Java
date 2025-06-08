package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.RoomTypeDAO;
import com.example.hotelmanagement.Models.Roomtype;
import javafx.beans.property.*;
import java.math.BigDecimal;
import java.util.List;

public class RoomTypeViewModel {
    private final IntegerProperty id;
    private final StringProperty typeName;
    private final ObjectProperty<BigDecimal> basePrice;
    private final IntegerProperty maxOccupancy;
    private final StringProperty description;
    private final StringProperty image;
    private final BooleanProperty isDeleted;
    private RoomTypeDAO roomTypeDAO;
    public RoomTypeViewModel(Roomtype roomtype) {
        this.id = new SimpleIntegerProperty(roomtype.getId());
        this.typeName = new SimpleStringProperty(roomtype.getTypeName());
        this.basePrice = new SimpleObjectProperty<>(roomtype.getBasePrice());
        this.maxOccupancy = new SimpleIntegerProperty(roomtype.getMaxOccupancy());
        this.description = new SimpleStringProperty(roomtype.getDescription());
        this.image = new SimpleStringProperty(roomtype.getImage());
        this.isDeleted = new SimpleBooleanProperty(roomtype.getIsDeleted());
    }


    public boolean addNewRoomType(Roomtype roomType){
        return roomTypeDAO.save(roomType);
    }
    public List<Roomtype> getAllRoomTypes(){
        return roomTypeDAO.getAll();
    }
    public Roomtype findById(int id){
        return roomTypeDAO.findById(id);
    }
    public boolean updateRoomType(Roomtype roomtype){
        return roomTypeDAO.update(roomtype);
    }
    public boolean removeRoomType(int id){
        return roomTypeDAO.softDelete(id);
    }
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty typeNameProperty() {
        return typeName;
    }

    public ObjectProperty<BigDecimal> basePriceProperty() {
        return basePrice;
    }

    public IntegerProperty maxOccupancyProperty() {
        return maxOccupancy;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty imageProperty() {
        return image;
    }

    public BooleanProperty isDeletedProperty() {
        return isDeleted;
    }

    // --- Simple Getters (for convenience, not for binding) ---

    public Integer getId() {
        return id.get();
    }

    public String getTypeName() {
        return typeName.get();
    }

    public BigDecimal getBasePrice() {
        return basePrice.get();
    }

    public Integer getMaxOccupancy() {
        return maxOccupancy.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getImage() {
        return image.get();
    }

    public Boolean getIsDeleted() {
        return isDeleted.get();
    }


}
