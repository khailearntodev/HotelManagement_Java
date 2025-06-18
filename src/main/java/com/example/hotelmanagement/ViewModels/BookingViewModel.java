package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.*;
import com.example.hotelmanagement.DTO.Reservation_RoomDisplay;
import com.example.hotelmanagement.DTO.Reservation_ServiceBookingDisplay;
import com.example.hotelmanagement.Models.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class BookingViewModel {
    @Getter
    private Reservation_RoomDisplay roomDisplay;
    @Getter
    private Room room;
    @Setter
    private ReservationViewModel parent;
    @Getter
    private ObservableList<Customer> customerList;
    @Getter
    private Reservation reservation;
    @Getter
    private ObservableList<Reservation_ServiceBookingDisplay> services;

    @Getter
    @Setter
    private ObjectProperty<LocalDate> checkOutDate = new SimpleObjectProperty<>();

    @Getter
    private boolean canEdit;

    public BookingViewModel(Room room, boolean canEdit) {
        this.canEdit = canEdit;
        reservation = new Reservation();
        this.room = room;
        this.roomDisplay = new Reservation_RoomDisplay(room);
        this.customerList = FXCollections.observableArrayList();
        if (!canEdit) {
            if (checkOutDate.get() == null) {
                Prebooking preBooking = room.getPrebookings().stream()
                        .filter(e -> e.getReservationID() == null)
                        .filter(e -> Objects.equals(e.getRoomID().getId(), room.getId()))
                        .filter(e -> !e.getIsDeleted())
                        .filter(e -> {
                            LocalDate checkIn = e.getCheckInDate().atZone(ZoneOffset.UTC).toLocalDate();
                            LocalDate checkOut = e.getCheckOutDate().atZone(ZoneOffset.UTC).toLocalDate();
                            return !checkIn.isAfter(LocalDate.now()) && !checkOut.isBefore(LocalDate.now());
                        })
                        .min(Comparator.comparing(Prebooking::getCheckInDate))
                        .orElse(null);
                if (preBooking != null)
                    checkOutDate.set(preBooking.getCheckOutDate().atZone(ZoneOffset.UTC).toLocalDate());
            }
        }
    }

    public void loadService() {
        List<Service> tmp = new ServiceDAO().getAll();
        services = FXCollections.observableArrayList();
        for (Service s : tmp) {
            services.add(new Reservation_ServiceBookingDisplay(s));
        }
    }

    public void addReservation(String note)
    {
        CustomerDAO customerDAO = new CustomerDAO();
        BigDecimal maxBonus = BigDecimal.valueOf(1.0);
        for (Customer guest : customerList) {
            if (guest.getCustomerTypeID().getBonusCharge().compareTo(maxBonus) > 0) maxBonus = guest.getCustomerTypeID().getBonusCharge();
            Customer customer = customerDAO.findByIdentityNumber(guest.getIdentityNumber(), guest.getIdentityType());
            if (customerDAO.findByIdentityNumber(guest.getIdentityNumber(), guest.getIdentityType()) == null) {
                customerDAO.save(guest);
            } else {
                guest.setId(customer.getId());
                customerDAO.update(guest);
            }
        }

        EmployeeDAO employeeDAO = new EmployeeDAO();
        Employee employee = employeeDAO.findById(LoginViewModel.employeeId);
        reservation.setRoomID(room);
        reservation.setBasePrice(room.getRoomTypeID().getBasePrice());
        reservation.setEmployeeID(employee);
        reservation.setCheckInDate(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant());
        reservation.setCheckOutDate(checkOutDate.get().atStartOfDay(ZoneOffset.UTC).toInstant());
        reservation.setPrice(room.getRoomTypeID().getBasePrice().multiply(maxBonus));
        reservation.setTotal(null);
        reservation.setNote(note);

        ReservationDAO reservationDAO = new ReservationDAO();
        reservationDAO.save(reservation);
        ReservationGuestDAO reservationGuestDAO = new ReservationGuestDAO();
        for(var guest : customerList) {
            Reservationguest guestReservation = new Reservationguest();
            guestReservation.setReservationID(reservation);
            guestReservation.setCustomerID(guest);
            reservationGuestDAO.save(guestReservation);
        }
        room.setStatus(2);
        RoomDAO roomDAO = new RoomDAO();
        roomDAO.update(room);

        ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();
        for (var item : services) {
            if (item.isSelected()) {
                Servicebooking serviceBooking = new Servicebooking();
                serviceBooking.setReservationID(reservation);
                serviceBooking.setBookingDate(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant());
                serviceBooking.setQuantity(item.getQuantity());
                serviceBooking.setServiceID(item.getService());
                serviceBooking.setStatus("Chưa xử lý");
                serviceBooking.setServiceprice(item.getPrice());
                serviceBookingDAO.save(serviceBooking);
            }
        }

        Prebooking preBooking = room.getPrebookings().stream()
                .filter(e -> e.getReservationID() == null)
                .filter(e -> Objects.equals(e.getRoomID().getId(), room.getId()))
                .filter(e -> !e.getIsDeleted())
                .filter(e -> {
                    LocalDate checkIn = e.getCheckInDate()
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate();
                    return !checkIn.isAfter(LocalDate.now());
                })
                .min(Comparator.comparing(Prebooking::getCheckInDate))
                .orElse(null);
        if (preBooking != null) {
            preBooking.setReservationID(reservation);
            PrebookingDAO prebookingDAO = new PrebookingDAO();
            prebookingDAO.update(preBooking);
        }

        Reservation_RoomDisplay roomReservationDisplay = parent.getRooms().stream().filter(e -> e.getId() == room.getId()).findFirst().orElse(null);
        if (roomReservationDisplay != null) {
            roomReservationDisplay.setStatus(2);
            roomReservationDisplay.setQuantity(customerList.size());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            roomReservationDisplay.setCheckInOutDate(LocalDate.now().format(formatter)  + " - " + checkOutDate.get().format(formatter));
        }
    }
}
