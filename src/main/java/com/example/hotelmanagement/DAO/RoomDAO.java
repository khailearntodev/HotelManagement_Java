package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RoomDAO {

    // Lấy danh sách tất cả phòng chưa xóa
    public List<Room> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                    "SELECT DISTINCT r FROM Room r " +
                            "LEFT JOIN FETCH r.roomTypeID " +
                            "LEFT JOIN FETCH r.prebookings pb " +
                            "LEFT JOIN FETCH r.reservations res " +
                            "LEFT JOIN FETCH res.reservationguests " +
                            "WHERE r.isDeleted = false",
                    Room.class
            ).list();
        }
    }


    // Tìm phòng theo ID
    public Room findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "SELECT r FROM Room r " +
                                    "LEFT JOIN FETCH r.roomTypeID " +
                                    "LEFT JOIN FETCH r.prebookings pb " +
                                    "LEFT JOIN FETCH r.reservations res " +
                                    "LEFT JOIN FETCH res.reservationguests " +
                                    "WHERE r.id = :id", Room.class)
                    .setParameter("id", id)
                    .uniqueResult();
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
}
