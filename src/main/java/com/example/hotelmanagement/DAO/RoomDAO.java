package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class RoomDAO {

    // Lấy danh sách tất cả phòng chưa xóa
    public List<Room> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Room WHERE isDeleted = false", Room.class).list();
        }
    }

    // Tìm phòng theo ID
    public Room findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Room.class, id);
        }
    }

    // Thêm phòng mới
    public boolean save(Room room) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(room);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật thông tin phòng
    public boolean update(Room room) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(room);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm phòng (soft delete)
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Room room = session.get(Room.class, id);
            if (room != null) {
                room.setIsDeleted(true);
                session.update(room);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Tìm phòng theo trạng thái (ví dụ status hoặc cleaningStatus)
    public List<Room> findByStatus(int status) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Room WHERE status = :status AND isDeleted = false", Room.class)
                    .setParameter("status", status)
                    .list();
        }
    }
    public List<Room> findByRoomTypeId (int roomTypeId){
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Room r JOIN FETCH r.roomTypeID WHERE r.roomTypeID.id = :roomTypeId AND r.isDeleted = false", Room.class)
                    .setParameter("roomTypeId", roomTypeId)
                    .list();
        }
    }
    public long countTotalRoomsByRoomTypeId(Integer roomTypeId) {
        try (Session session = HibernateUtils.getSession()) {
            // No JOIN FETCH needed here as we are only counting, not returning Room objects
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(r) FROM Room r WHERE r.roomTypeID.id = :roomTypeId AND r.isDeleted = false", Long.class);
            query.setParameter("roomTypeId", roomTypeId);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("Error counting total rooms for RoomType ID " + roomTypeId + ": " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    public long countAvailableRoomsByRoomTypeId(Integer roomTypeId) {
        try (Session session = HibernateUtils.getSession()) {
            // No JOIN FETCH needed here as we are only counting, not returning Room objects
            // Assuming 'status' 0 means available and 'cleaningStatus' 0 means clean.
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(r) FROM Room r WHERE r.roomTypeID.id = :roomTypeId AND r.isDeleted = false AND r.status = 0 AND r.cleaningStatus = 0", Long.class);
            query.setParameter("roomTypeId", roomTypeId);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("Error counting available rooms for RoomType ID " + roomTypeId + ": " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
