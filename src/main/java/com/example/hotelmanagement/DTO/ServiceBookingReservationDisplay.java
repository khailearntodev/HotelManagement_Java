package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.Models.Service;
import javafx.beans.property.*;
import lombok.Getter;

import java.math.BigDecimal;

public class ServiceBookingReservationDisplay {
    @Getter
    private Service service;
    private final StringProperty serviceName = new SimpleStringProperty();
    private final BooleanProperty isSelected = new SimpleBooleanProperty(false);
    private final IntegerProperty quantity = new SimpleIntegerProperty(0);
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();

    public ServiceBookingReservationDisplay(Service service) {
        this.service = service;
        this.serviceName.set(service.getServiceName());
        this.price.set(service.getPrice());
    }

    public boolean isSelected() { return isSelected.get(); }
    public void setSelected(boolean selected) { isSelected.set(selected); }
    public BooleanProperty selectedProperty() { return isSelected; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int qty) { quantity.set(qty); }
    public IntegerProperty quantityProperty() { return quantity; }

    public String getServiceName() { return serviceName.get(); }
    public void setServiceName(String serviceName) { this.serviceName.set(serviceName); }
    public StringProperty serviceNameProperty() { return serviceName; }

    public BigDecimal getPrice() { return price.get(); }
    public void setPrice(BigDecimal price) { this.price.set(price); }
    public ObjectProperty<BigDecimal> priceProperty() { return price; }
}
