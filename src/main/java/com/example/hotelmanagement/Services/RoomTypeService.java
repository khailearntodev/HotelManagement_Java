package com.example.hotelmanagement.Services;

import com.example.hotelmanagement.DAO.RoomTypeDAO;
import com.example.hotelmanagement.Models.Roomtype;

import java.util.List;

public class RoomTypeService {
    private final RoomTypeDAO roomTypeDAO;
    public RoomTypeService(){
        roomTypeDAO = new RoomTypeDAO();
    }
    public RoomTypeService(RoomTypeDAO roomTypeDAO){
        this.roomTypeDAO = roomTypeDAO;
    }
    public List<Roomtype> getAllRoomTypes(){
        return  roomTypeDAO.getAll();
    }
    public Roomtype getRoomTypeById(int id){
        return roomTypeDAO.findById(id);
    }
    public boolean addRoomType(Roomtype room1){
        return roomTypeDAO.save(room1);
    }
    public boolean updateRoomType(Roomtype updatedRoom){
        return roomTypeDAO.update(updatedRoom);
    }
    public boolean softDeleteRoomType(int id) {
        // Add business logic for soft deletion (e.g., check dependencies)
        return roomTypeDAO.softDelete(id);
    }


}
