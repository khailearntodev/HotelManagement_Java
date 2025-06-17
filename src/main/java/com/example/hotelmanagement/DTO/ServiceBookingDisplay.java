package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.Models.Servicebooking;
import javafx.beans.property.*;
        import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import com.example.hotelmanagement.Models.Servicebooking;


public class ServiceBookingDisplay {
    public static final String STATUS_PENDING = "Chưa xử lý";
    public static final String STATUS_PROCESSED = "Đã xử lý";
    public static final String STATUS_CANCELLED = "Đã hủy";

    private final Servicebooking originalBooking;

    private final IntegerProperty serviceBookingId;
    private final StringProperty serviceName;
    private final IntegerProperty quantity;
    private final ObjectProperty<LocalDate> bookingDate;
    private final StringProperty status;
    private final BooleanProperty isDeleted = new SimpleBooleanProperty();
    private final BooleanProperty canProcess;
    private final BooleanProperty canCancel;

    public ServiceBookingDisplay(Servicebooking booking) {
        this.originalBooking = booking;
        this.serviceBookingId = new SimpleIntegerProperty(booking.getId() != null ? booking.getId() : 0); // Lấy ID
        if (booking.getServiceID() != null && booking.getServiceID().getServiceName() != null) {
            this.serviceName = new SimpleStringProperty(booking.getServiceID().getServiceName());
        } else if (booking.getServiceID() != null) {
            this.serviceName = new SimpleStringProperty("Dịch vụ ID: " + booking.getServiceID().getId());
        }
        else {
            this.serviceName = new SimpleStringProperty("Lỗi: Dịch vụ không xác định");
        }

        this.quantity = new SimpleIntegerProperty(booking.getQuantity());
        this.bookingDate = new SimpleObjectProperty<>(
                booking.getBookingDate() != null ? booking.getBookingDate().atZone(ZoneId.systemDefault()).toLocalDate() : null
        );
        this.status = new SimpleStringProperty(booking.getStatus());

        if (booking.getServiceID().getIsDeleted() &&
                (STATUS_PENDING.equals(booking.getStatus()) || STATUS_CANCELLED.equals(booking.getStatus()))) {
            this.isDeleted.set(true);
        }

        this.canProcess = new SimpleBooleanProperty();
        this.canCancel = new SimpleBooleanProperty();

        this.status.addListener((obs, oldStatus, newStatus) -> updateButtonStates(newStatus));
        updateButtonStates(this.status.get());
    }

    private void updateButtonStates(String currentStatus) {
        canProcess.set(STATUS_PENDING.equals(currentStatus));
        canCancel.set(STATUS_PENDING.equals(currentStatus));
    }

    // Getters for JavaFX Properties
    public IntegerProperty serviceBookingIdProperty() { return serviceBookingId; }
    public StringProperty serviceNameProperty() { return serviceName; }
    public IntegerProperty quantityProperty() { return quantity; }
    public ObjectProperty<LocalDate> bookingDateProperty() { return bookingDate; }
    public StringProperty statusProperty() { return status; }
    public BooleanProperty canProcessProperty() { return canProcess; }
    public BooleanProperty canCancelProperty() { return canCancel; }

    // Getters for values
    public int getServiceBookingId() { return serviceBookingId.get(); }
    public String getServiceName() { return serviceName.get(); }
    public int getQuantity() { return quantity.get(); }
    public LocalDate getBookingDate() { return bookingDate.get(); }
    public String getStatus() { return status.get(); }
    public boolean isCanProcess() { return canProcess.get(); }
    public boolean isCanCancel() { return canCancel.get(); }

    public Servicebooking getOriginalBooking() {
        return originalBooking;
    }

    public void setStatus(String newStatus) {
        this.status.set(newStatus);
        // Cập nhật lại originalBooking nếu muốn trạng thái đồng bộ ngay lập tức
         this.originalBooking.setStatus(newStatus);
    }
    public boolean isDeleted() {
        return isDeleted.get();
    }
    public BooleanProperty isDeletedProperty() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted.set(isDeleted);
    }
}