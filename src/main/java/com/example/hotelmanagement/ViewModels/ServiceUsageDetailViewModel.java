package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.Models.Servicebooking;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ServiceUsageDetailViewModel {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty serviceName;
    private final SimpleIntegerProperty quantity;
    private final SimpleStringProperty date;
    private final SimpleStringProperty formattedPrice;
    private final SimpleStringProperty formattedTotal;
    private final BigDecimal totalPrice;

    public ServiceUsageDetailViewModel(Servicebooking servicebooking) {
        this.id = new SimpleIntegerProperty(servicebooking.getId());
        this.serviceName = new SimpleStringProperty(servicebooking.getServiceID().getServiceName());
        this.quantity = new SimpleIntegerProperty(servicebooking.getQuantity());
        Instant bookingInstant = servicebooking.getBookingDate();
        LocalDateTime bookingLocalDateTime = null;
        if (bookingInstant != null) {
            bookingLocalDateTime = LocalDateTime.ofInstant(bookingInstant, ZoneId.systemDefault());
        }
        this.date = new SimpleStringProperty(formatDateTime(bookingLocalDateTime));
        BigDecimal price = servicebooking.getServiceprice();
        this.formattedPrice = new SimpleStringProperty(formatCurrency(price));
        BigDecimal total = price.multiply(BigDecimal.valueOf(servicebooking.getQuantity()));
        this.totalPrice = total;
        this.formattedTotal = new SimpleStringProperty(formatCurrency(total));
    }
    public SimpleIntegerProperty IdProperty() {
        return id;
    }

    public SimpleStringProperty serviceNameProperty() {
        return serviceName;
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public SimpleStringProperty formattedPriceProperty() {
        return formattedPrice;
    }

    public SimpleStringProperty formattedTotalProperty() {
        return formattedTotal;
    }
    //endregion

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    private String formatCurrency(BigDecimal amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormatter.format(amount);
    }
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }
}