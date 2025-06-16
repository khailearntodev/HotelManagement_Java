package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.ServiceBookingDAO;
import com.example.hotelmanagement.DAO.ServiceDAO;
// import com.example.hotelmanagement.DAO.ReservationDAO; // Nếu bạn cần load Reservation từ đây
import com.example.hotelmanagement.DTO.ServiceBookingDisplay;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Models.Service;
import com.example.hotelmanagement.Models.Servicebooking;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoomServiceViewModel {
    private final StringProperty roomIdText = new SimpleStringProperty("N/A");
    private final ObjectProperty<Service> selectedService = new SimpleObjectProperty<>();
    private final IntegerProperty quantity = new SimpleIntegerProperty(1);
    private final ObservableList<Service> availableServices = FXCollections.observableArrayList();

    private final ObservableList<ServiceBookingDisplay> roomServiceBookings = FXCollections.observableArrayList();
    private final ObjectProperty<ServiceBookingDisplay> selectedBookingItem = new SimpleObjectProperty<>();
    
    private final ServiceBookingDAO serviceBookingDAO;
    private final ServiceDAO serviceDAO;

    private Reservation currentReservation;

    public RoomServiceViewModel() {
        this.serviceBookingDAO = new ServiceBookingDAO();
        this.serviceDAO = new ServiceDAO();
        loadAvailableServices();
    }

    public void loadInitialData(Reservation reservation) {
        this.currentReservation = reservation;
        if (this.currentReservation != null) {
            if (this.currentReservation.getRoomID() != null && this.currentReservation.getRoomID().getRoomNumber() != null) {
                this.roomIdText.set("Phòng: " + this.currentReservation.getRoomID().getRoomNumber());
            } else if (this.currentReservation.getRoomID() != null) {
                this.roomIdText.set("Phòng ID: " + this.currentReservation.getRoomID().getId() + " (Res ID: " + this.currentReservation.getId() + ")");
            }
            else {
                this.roomIdText.set("Đặt phòng ID: " + this.currentReservation.getId() + " (Không có phòng cụ thể)");
            }
            loadRoomServiceBookings();
        } else {
            this.roomIdText.set("N/A - Không có đặt phòng");
            roomServiceBookings.clear();
        }
    }

    private void loadAvailableServices() {
        List<Service> services = serviceDAO.getAll();
        if (services != null && !services.isEmpty()) {
            availableServices.setAll(services);
        } else {
            availableServices.clear();
            System.err.println("Không tải được danh sách dịch vụ khả dụng.");
        }
    }

    public void loadRoomServiceBookings() {
        if (currentReservation == null || currentReservation.getId() == null) {
            roomServiceBookings.clear();
            return;
        }
        List<Servicebooking> bookings = serviceBookingDAO.findByReservationId(currentReservation.getId());
        if (bookings != null) {
            roomServiceBookings.setAll(
                    bookings.stream()
                            .map(ServiceBookingDisplay::new)
                            .collect(Collectors.toList())
            );
        } else {
            roomServiceBookings.clear();
            System.err.println("Không tải được danh sách dịch vụ cho đặt phòng: " + currentReservation.getId());
        }
    }

    public void addNewService() {
        if (selectedService.get() == null || quantity.get() <= 0 || currentReservation == null) {
            System.out.println("Thông tin không hợp lệ để thêm dịch vụ.");
            return;
        }

        Servicebooking newBooking = new Servicebooking();
        newBooking.setReservationID(currentReservation);
        newBooking.setServiceID(selectedService.get());
        newBooking.setQuantity(quantity.get());
        newBooking.setBookingDate(Instant.now());
        newBooking.setStatus(ServiceBookingDisplay.STATUS_PENDING);
        newBooking.setIsDeleted(false);

        Servicebooking savedBooking = serviceBookingDAO.saveAndReturn(newBooking);
        if (savedBooking != null && savedBooking.getId() != null) {
            roomServiceBookings.add(new ServiceBookingDisplay(savedBooking));
            clearForm();
        } else {
            System.err.println("Lỗi khi thêm dịch vụ mới qua DAO.");
        }
    }

    public void processSelectedBooking() {
        ServiceBookingDisplay selected = selectedBookingItem.get();
        if (selected != null && selected.isCanProcess()) {
            Servicebooking bookingToUpdate = selected.getOriginalBooking();
            bookingToUpdate.setStatus(ServiceBookingDisplay.STATUS_PROCESSED);

            if (serviceBookingDAO.update(bookingToUpdate)) {
                selected.setStatus(ServiceBookingDisplay.STATUS_PROCESSED);
                // tvRoomServices.refresh();
            } else {
                System.err.println("Lỗi khi xử lý dịch vụ qua DAO.");
                bookingToUpdate.setStatus(ServiceBookingDisplay.STATUS_PENDING);
            }
        }
    }

    public void cancelSelectedBooking() {
        ServiceBookingDisplay selected = selectedBookingItem.get();
        if (selected != null && selected.isCanCancel()) {
            Servicebooking bookingToUpdate = selected.getOriginalBooking();
            bookingToUpdate.setStatus(ServiceBookingDisplay.STATUS_CANCELLED);

            if (serviceBookingDAO.update(bookingToUpdate)) {
                selected.setStatus(ServiceBookingDisplay.STATUS_CANCELLED);
                // tvRoomServices.refresh();
            } else {
                System.err.println("Lỗi khi hủy dịch vụ qua DAO.");
                bookingToUpdate.setStatus(ServiceBookingDisplay.STATUS_PENDING);
            }
        }
    }

    public void clearForm() {
        selectedService.set(null);
        quantity.set(1);
    }

    public StringProperty roomIdTextProperty() { return roomIdText; }
    public ObjectProperty<Service> selectedServiceProperty() { return selectedService; }
    public IntegerProperty quantityProperty() { return quantity; }
    public ObservableList<Service> getAvailableServices() { return availableServices; }
    public ObservableList<ServiceBookingDisplay> getRoomServiceBookings() { return roomServiceBookings; }
    public ObjectProperty<ServiceBookingDisplay> selectedBookingItemProperty() { return selectedBookingItem; }
}