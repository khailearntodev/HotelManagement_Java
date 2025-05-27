package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ReservationDAO {

    // Lấy tất cả Reservation chưa bị xóa
    public List<Reservation> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Reservation WHERE isDeleted = false", Reservation.class).list();
        }
    }

    // Tìm Reservation theo ID
    public Reservation findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Reservation.class, id);
        }
    }

    // Thêm Reservation mới
    public boolean save(Reservation reservation) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(reservation);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật Reservation
    public boolean update(Reservation reservation) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(reservation);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm Reservation
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Reservation reservation = session.get(Reservation.class, id);
            if (reservation != null) {
                reservation.setIsDeleted(true);
                session.update(reservation);
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
}
