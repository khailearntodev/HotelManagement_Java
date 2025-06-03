package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.PrebookingDAO;
import com.example.hotelmanagement.DAO.ReservationDAO;
import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DAO.RoomTypeDAO;
import com.example.hotelmanagement.DTO.RoomReservationDisplay;
import com.example.hotelmanagement.Models.Prebooking;
import com.example.hotelmanagement.Models.Room;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class BookingInAdvanceViewModel {
    @Setter
    ReservationViewModel parent;
    @Setter
    @Getter
    private LocalDate checkInDate;
    @Setter
    @Getter
    private LocalDate checkOutDate;
    @Getter
    private ObservableList<RoomReservationDisplay> roomReservationDisplays = FXCollections.observableArrayList();
    @Getter
    @Setter
    VBox selectedItem = null;

    public void findRoom() {
        roomReservationDisplays.clear();
        List<Integer> reservedRoomIds = new PrebookingDAO().getAll().stream()
                .filter(r -> !r.getIsDeleted())
                .filter(r ->
                        ( !r.getCheckInDate().isBefore(checkInDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) && !r.getCheckOutDate().isAfter(checkOutDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) ) ||
                                ( !r.getCheckOutDate().isBefore(checkInDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) && !r.getCheckOutDate().isAfter(checkOutDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) ) ||
                                ( !r.getCheckInDate().isAfter(checkInDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) && !r.getCheckOutDate().isBefore(checkOutDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) )
                )
                .map(p -> p.getRoomID().getId())
                .toList();

        List<Integer> rentedRoomIds = new ReservationDAO().getAll().stream()
                .filter(r -> !r.getIsDeleted())
                .filter(r ->
                        (!r.getCheckInDate().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(checkInDate) && !r.getCheckInDate().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(checkOutDate)) ||
                                (!r.getCheckOutDate().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(checkInDate) && !r.getCheckOutDate().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(checkOutDate)) ||
                                (!r.getCheckInDate().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(checkInDate) && !r.getCheckOutDate().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(checkOutDate))
                )
                .map(p -> p.getRoomID().getId())
                .toList();
        HashSet<Integer>unavailableRoomIds = new HashSet<>();
        unavailableRoomIds.addAll(reservedRoomIds);
        unavailableRoomIds.addAll(rentedRoomIds);
        List<Room> availableRooms = new RoomDAO().getAll().stream()
                .filter(r -> !r.getIsDeleted())
                .filter(r -> !unavailableRoomIds.contains(r.getId()))
                .toList();
        for (Room room : availableRooms) {
            roomReservationDisplays.add(new RoomReservationDisplay(room));
        }
    }
}
