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
            return session.createQuery("FROM Reservationguest WHERE isDeleted = false", Reservationguest.class).list();
        }
    }

    // Tìm Reservationguest theo ID
    public Reservationguest findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Reservationguest.class, id);
        }
    }

    // Thêm Reservationguest mới
    public boolean save(Reservationguest reservationguest) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(reservationguest);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
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
}
