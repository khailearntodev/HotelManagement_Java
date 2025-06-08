package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.PrebookingDAO;
import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DTO.CancelBooking_PreBookingDisplay;
import com.example.hotelmanagement.DTO.Reservation_RoomDisplay;
import com.example.hotelmanagement.Models.Prebooking;
import com.example.hotelmanagement.Models.Room;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

public class CancelBookingViewModel {
    @Setter
    ReservationViewModel parent;
    @Getter
    private CancelBooking_PreBookingDisplay preBookingDisplay;
    private Prebooking preBooking;
    Room room;

    public boolean findPreBooking(String code) {
        PrebookingDAO preBookingDAO = new PrebookingDAO();
        List<Prebooking> prebookingList = preBookingDAO.getAll();
        Prebooking preBooking = prebookingList.stream()
                .filter(e -> Objects.equals(e.getBookingCode(), code) && !e.getIsDeleted()).findFirst()
                .orElse(null);
        if (preBooking == null) {
            this.room = null;
            this.preBooking = null;
            this.preBookingDisplay = null;
            return false;
        } else {
            this.preBooking = preBooking;
            this.room = preBooking.getRoomID();
            this.preBookingDisplay = new CancelBooking_PreBookingDisplay(preBooking);
            return true;
        }
    }

    public void deletePrebooking() {
        preBooking.setIsDeleted(true);
        PrebookingDAO preBookingDAO = new PrebookingDAO();
        preBookingDAO.update(preBooking);
        if (room.getStatus() == 3) {
            room.setStatus(1);
            RoomDAO roomDAO = new RoomDAO();
            roomDAO.update(room);
            for (int i = 0; i < parent.getRooms().size(); i++) {
                if (parent.getMasterRooms().get(i).getId() == room.getId()) {
                    parent.getMasterRooms().set(i, new Reservation_RoomDisplay(room));
                    break;
                }
            }
            parent.setRooms(new FilteredList<>(parent.getMasterRooms(), p -> true));
        }
    }
}
