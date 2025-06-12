package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Reservationguest;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ReservationGuestDAO {

    // Lấy tất cả Reservationguest chưa bị xóa
    public List<Reservationguest> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                    "SELECT rg FROM Reservationguest rg " +
                            "LEFT JOIN FETCH rg.reservationID " +
                            "LEFT JOIN FETCH rg.customerID " +
                            "WHERE rg.isDeleted = false", Reservationguest.class
            ).list();
        }
    }

    // Tìm Reservationguest theo ID
    public Reservationguest findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "SELECT rg FROM Reservationguest rg " +
                                    "LEFT JOIN FETCH rg.reservationID " + // Nếu cần tải Reservation
                                    "LEFT JOIN FETCH rg.customerID " +    // Nếu cần tải Customer
                                    "WHERE rg.id = :id AND rg.isDeleted = false", Reservationguest.class)
                    .setParameter("id", id)
                    .uniqueResultOptional().orElse(null);
        }
    }

    // Thêm Reservationguest mới
    public boolean save(Reservationguest guest) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtils.getSession();
            tx = session.beginTransaction();

            session.save(guest);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }


    // Cập nhật Reservationguest
    public boolean update(Reservationguest reservationguest) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(reservationguest);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm Reservationguest
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Reservationguest reservationguest = session.get(Reservationguest.class, id);
            if (reservationguest != null) {
                reservationguest.setIsDeleted(true);
                session.update(reservationguest);
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

    public int countReservationsByCustomerId(int customerId) {
        try (Session session = HibernateUtils.getSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(rg.id) FROM Reservationguest rg WHERE rg.customerID.id = :cid AND rg.isDeleted = false", Long.class)
                    .setParameter("cid", customerId)
                    .uniqueResult();

            return count != null ? count.intValue() : 0;
        }
    }
}
