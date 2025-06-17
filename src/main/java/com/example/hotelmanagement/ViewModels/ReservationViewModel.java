package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DAO.RoomTypeDAO;
import com.example.hotelmanagement.DTO.Reservation_RoomDisplay;
import com.example.hotelmanagement.Models.Prebooking;
import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Models.Roomtype;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

public class ReservationViewModel {
    @Getter
    private final ObservableList<Reservation_RoomDisplay> masterRooms = FXCollections.observableArrayList();
    @Getter
    @Setter
    private FilteredList<Reservation_RoomDisplay> rooms = new FilteredList<>(masterRooms, p -> true);
    @Getter
    private final ObservableList<Roomtype> roomTypes = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<Integer> roomStatus = FXCollections.observableArrayList();

    public void loadFromModel() {
        List<Room> allRoom = new RoomDAO().getAll();
        RoomDAO roomDAO = new RoomDAO();
        LocalDate today = LocalDate.now();

        for (Room room : allRoom) {
            Prebooking preBooking = room.getPrebookings().stream()
                    .filter(e -> e.getReservationID() == null)
                    .filter(e -> Objects.equals(e.getRoomID().getId(), room.getId()))
                    .filter(e -> !e.getIsDeleted())
                    .filter(e -> {
                        LocalDate checkIn = e.getCheckInDate().atZone(ZoneOffset.UTC).toLocalDate();
                        LocalDate checkOut = e.getCheckOutDate().atZone(ZoneOffset.UTC).toLocalDate();
                        return !checkIn.isAfter(today) && !checkOut.isBefore(today);
                    })

                    .min(Comparator.comparing(Prebooking::getCheckInDate))
                    .orElse(null);

            if (preBooking != null) {
                System.out.println(preBooking.getBookingCode());
                LocalDate checkOutDate = preBooking.getCheckOutDate()
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate();

                if (!today.isAfter(checkOutDate)) {
                    room.setStatus(3);
                } else {
                    room.setStatus(1);
                }
                roomDAO.update(room);
            }
        }

        masterRooms.clear();
        var dao = new RoomDAO();
        var roomList = dao.getAll();
        for (Room room : roomList) {
            masterRooms.add(new Reservation_RoomDisplay(room));
        }
        var roomtypeList = new RoomTypeDAO().getAll();
        Roomtype allRoomType = new Roomtype();
        allRoomType.setId(-1);
        allRoomType.setTypeName("Tất cả");
        allRoomType.setBasePrice(BigDecimal.ZERO);
        allRoomType.setMaxOccupancy(0);
        allRoomType.setIsDeleted(false);

        roomTypes.add(allRoomType);
        roomTypes.addAll(roomtypeList);

        roomStatus.addAll(Arrays.asList(0, 1, 2, 3));
    }
}
