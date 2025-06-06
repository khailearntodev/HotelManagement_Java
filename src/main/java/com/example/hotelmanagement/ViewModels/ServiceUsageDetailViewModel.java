package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.Models.Servicebooking;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ServiceUsageDetailViewModel {
    private  final IntegerProperty Id = new SimpleIntegerProperty();
    private final StringProperty serviceName = new SimpleStringProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty formattedPrice = new SimpleStringProperty();
    private final StringProperty formattedTotal = new SimpleStringProperty();

    public ServiceUsageDetailViewModel(Servicebooking sb) {
        BigDecimal price = sb.getServiceID().getPrice();
        int qty = sb.getQuantity();
        BigDecimal total = price.multiply(BigDecimal.valueOf(qty));

        Id.set(sb.getId());
        serviceName.set(sb.getServiceID().getServiceName());
        quantity.set(qty);
        date.set(sb.getBookingDate().toString());
        formattedPrice.set(formatCurrency(price));
        formattedTotal.set(formatCurrency(total));
    }

    private String formatCurrency(BigDecimal value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(value);
    }

    public IntegerProperty IdProperty() { return Id; }
    public StringProperty serviceNameProperty() { return serviceName; }
    public IntegerProperty quantityProperty() { return quantity; }
    public StringProperty dateProperty() { return date; }
    public StringProperty formattedPriceProperty() { return formattedPrice; }
    public StringProperty formattedTotalProperty() { return formattedTotal; }
}
