package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DTO.RoomReservationDisplay;
import com.example.hotelmanagement.Models.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReservationViewModel {
    private final ObservableList<RoomReservationDisplay> rooms = FXCollections.observableArrayList();

    public ObservableList<RoomReservationDisplay> getRooms() {
        return rooms;
    }

    public void loadFromModel() {
        rooms.clear();
        var dao = new RoomDAO();
        var roomList = dao.getAll();
        for (Room room : roomList) {
            rooms.add(new RoomReservationDisplay(room));
        }
    }
}
