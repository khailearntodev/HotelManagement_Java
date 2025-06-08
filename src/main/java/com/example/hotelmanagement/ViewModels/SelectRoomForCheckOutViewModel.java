package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.PrebookingDAO;
import com.example.hotelmanagement.DAO.ReservationDAO;
import com.example.hotelmanagement.DTO.SelectRoom_RoomDisplay;
import com.example.hotelmanagement.Models.Prebooking;
import com.example.hotelmanagement.Models.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class SelectRoomForCheckOutViewModel {
    @Setter
    ReservationViewModel parent;
    @Getter
    private ObservableList<SelectRoom_RoomDisplay> rooms = FXCollections.observableArrayList();
    @Getter
    private List<Reservation> selectedRooms = new ArrayList<>();

    public SelectRoomForCheckOutViewModel() {
        List<Reservation> reservationList = new ReservationDAO().getAll().stream().filter(e -> e.getInvoiceID() == null).toList();
        for (var room : reservationList) {
            rooms.add(new SelectRoom_RoomDisplay(room));
        }
    }
}
