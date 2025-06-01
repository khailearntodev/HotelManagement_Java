package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DAO.RoomTypeDAO;
import com.example.hotelmanagement.DTO.RoomReservationDisplay;
import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Models.Roomtype;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

public class ReservationViewModel {
    @Getter
    private final ObservableList<RoomReservationDisplay> masterRooms = FXCollections.observableArrayList();
    @Getter
    private final FilteredList<RoomReservationDisplay> rooms = new FilteredList<>(masterRooms, p -> true);
    @Getter
    private final ObservableList<Roomtype> roomTypes = FXCollections.observableArrayList();

    public void loadFromModel() {
        masterRooms.clear();
        var dao = new RoomDAO();
        var roomList = dao.getAll();
        for (Room room : roomList) {
            masterRooms.add(new RoomReservationDisplay(room));
        }
        var roomtypeList = new RoomTypeDAO().getAll();
        roomTypes.addAll(roomtypeList);
    }
}
