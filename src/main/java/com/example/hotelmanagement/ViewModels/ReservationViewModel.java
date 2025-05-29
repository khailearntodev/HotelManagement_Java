package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DTO.ReservationRoomDisplay;
import com.example.hotelmanagement.Models.Room;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;

public class ReservationViewModel {
    private final ObservableList<ReservationRoomDisplay> rooms = FXCollections.observableArrayList();

    public ObservableList<ReservationRoomDisplay> getRooms() {
        return rooms;
    }

    public void loadFromModel() {
        rooms.clear();
        var dao = new RoomDAO();
        var roomList = dao.getAll();
        for (Room room : roomList) {
            rooms.add(new ReservationRoomDisplay(room));
        }
    }


}
