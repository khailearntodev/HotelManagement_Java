package com.example.hotelmanagement.Services;

import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.Models.Room;

import java.util.List;

public class RoomService {
    private final RoomDAO roomDAO;
    public RoomService(){
        roomDAO = new RoomDAO();
    }
    public RoomService(RoomDAO roomDAO){
        this.roomDAO = roomDAO;
    }
    public List<Room> getAllRooms(){
        return roomDAO.getAll();
    }
    public Room getRoomById(int id){
        return roomDAO.findById(id);
    }
    public List<Room> findRoomsByStatus(int status){
        return  roomDAO.findByStatus(status);
    }
    public boolean save(Room room){
        return roomDAO.save(room);
    }
    public  boolean update(Room room){
        return  roomDAO.update(room);
    }
    public boolean delete(int id){
        return roomDAO.softDelete(id);
    }
    public List<Room> findRoomsByRoomTypeId(Integer roomTypeId) {
        return  roomDAO.findByRoomTypeId(roomTypeId);
    }
    public long countTotalRoomsByRoomTypeId(Integer roomTypeId) {
        // Add any business logic here
        return roomDAO.countTotalRoomsByRoomTypeId(roomTypeId);
    }
    public long countAvailableRoomsByRoomTypeId(Integer roomTypeId) {
        // Add any business logic here
        return roomDAO.countAvailableRoomsByRoomTypeId(roomTypeId);
    }
    public boolean isRoomNumberUnique(Integer roomNumber, Integer currentRoomId) {
        if (currentRoomId == null) {
            // For new rooms, check if any existing room has this number
            return roomDAO.findByRoomNumber(roomNumber).isEmpty();
        } else {
            // For existing rooms (updates), check if any OTHER room has this number
            return roomDAO.findByRoomNumberExcludingId(roomNumber, currentRoomId).isEmpty();
        }
    }
}
