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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReservationViewModel {
    @Getter
    private final ObservableList<RoomReservationDisplay> masterRooms = FXCollections.observableArrayList();
    @Getter
    private final FilteredList<RoomReservationDisplay> rooms = new FilteredList<>(masterRooms, p -> true);
    @Getter
    private final ObservableList<Roomtype> roomTypes = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<Integer> roomStatus = FXCollections.observableArrayList();

    public void loadFromModel() {
        masterRooms.clear();
        var dao = new RoomDAO();
        var roomList = dao.getAll();
        for (Room room : roomList) {
            masterRooms.add(new RoomReservationDisplay(room));
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
