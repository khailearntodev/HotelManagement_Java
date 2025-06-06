package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Servicebooking;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ServiceBookingDAO {

    // Lấy danh sách tất cả Servicebooking chưa bị xóa mềm
    public List<Servicebooking> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Servicebooking WHERE isDeleted = false", Servicebooking.class).list();
        }
    }

    // Tìm Servicebooking theo ID
    public Servicebooking findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Servicebooking.class, id);
        }
    }

    // Thêm Servicebooking mới
    public boolean save(Servicebooking servicebooking) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtils.getSession();
            tx = session.beginTransaction();
            session.save(servicebooking);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
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

    // Cập nhật Servicebooking
    public boolean update(Servicebooking servicebooking) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(servicebooking);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm Servicebooking
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Servicebooking entity = session.get(Servicebooking.class, id);
            if (entity != null) {
                entity.setIsDeleted(true);
                session.update(entity);
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
