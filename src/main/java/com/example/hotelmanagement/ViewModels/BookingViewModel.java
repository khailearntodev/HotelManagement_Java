package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.*;
import com.example.hotelmanagement.DTO.RoomReservationDisplay;
import com.example.hotelmanagement.DTO.ServiceBookingReservationDisplay;
import com.example.hotelmanagement.Models.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingViewModel {
    @Getter
    private RoomReservationDisplay roomDisplay;
    @Getter
    private Room room;
    @Setter
    private ReservationViewModel parent;
    @Getter
    private ObservableList<Customer> customerList;
    @Getter
    private Reservation reservation;
    @Getter
    private ObservableList<ServiceBookingReservationDisplay> services;

    @Getter
    @Setter
    private ObjectProperty<LocalDate> checkOutDate = new SimpleObjectProperty<>();

    public BookingViewModel(Room room) {
        reservation = new Reservation();
        this.room = room;
        this.roomDisplay = new RoomReservationDisplay(room);
        this.customerList = FXCollections.observableArrayList();
    }

    public void loadService() {
        List<Service> tmp = new ServiceDAO().getAll();
        services = FXCollections.observableArrayList();
        for (Service s : tmp) {
            services.add(new ServiceBookingReservationDisplay(s));
        }
    }

    public void addReservation(String note)
    {
        //
        Employee employee = new Employee();
        EmployeeDAO employeeDAO = new EmployeeDAO();
        employee = employeeDAO.findById(2);
        //
        reservation.setRoomID(room);
        reservation.setBasePrice(room.getRoomTypeID().getBasePrice());
        reservation.setEmployeeID(employee);
        reservation.setCheckInDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        reservation.setCheckOutDate(checkOutDate.get().atStartOfDay(ZoneId.systemDefault()).toInstant());
        //
        reservation.setPrice(room.getRoomTypeID().getBasePrice());
        reservation.setTotal(room.getRoomTypeID().getBasePrice());
        //
        reservation.setNote(note);

        ReservationDAO reservationDAO = new ReservationDAO();
        reservationDAO.save(reservation);
        CustomerDAO customerDAO = new CustomerDAO();
        ReservationGuestDAO reservationGuestDAO = new ReservationGuestDAO();
        for(var guest : customerList) {
            Customer customer = customerDAO.findByIdentityNumber(guest.getIdentityNumber(), guest.getIdentityType());
            if (customerDAO.findByIdentityNumber(guest.getIdentityNumber(), guest.getIdentityType()) == null) {
                customerDAO.save(guest);
            } else {
                guest.setId(customer.getId());
                customerDAO.update(guest);
            }
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
                serviceBooking.setBookingDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
                serviceBooking.setQuantity(item.getQuantity());
                serviceBooking.setServiceID(item.getService());
                serviceBooking.setStatus("Chưa xử lý");
                serviceBookingDAO.save(serviceBooking);
            }
        }

        RoomReservationDisplay roomReservationDisplay = parent.getRooms().stream().filter(e -> e.getId() == room.getId()).findFirst().orElse(null);
        if (roomReservationDisplay != null) {
            roomReservationDisplay.setStatus(2);
            roomReservationDisplay.setQuantity(customerList.size());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            roomReservationDisplay.setCheckInOutDate(LocalDate.now().format(formatter)  + " - " + checkOutDate.get().format(formatter));
        }
    }
}
