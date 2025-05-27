package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Prebooking;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PrebookingDAO {

    // Lấy tất cả prebooking chưa xóa
    public List<Prebooking> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Prebooking WHERE isDeleted = false", Prebooking.class).list();
        }
    }

    // Tìm Prebooking theo ID
    public Prebooking findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Prebooking.class, id);
        }
    }

    // Thêm Prebooking mới
    public boolean save(Prebooking prebooking) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(prebooking);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật Prebooking
    public boolean update(Prebooking prebooking) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(prebooking);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm Prebooking
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Prebooking prebooking = session.get(Prebooking.class, id);
            if (prebooking != null) {
                prebooking.setIsDeleted(true);
                session.update(prebooking);
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
