package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Amenity;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AmenityDAO {

    // Lấy tất cả tiện ích chưa bị xóa
    public List<Amenity> getAllAmenities() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Amenity WHERE isDeleted = false", Amenity.class).list();
        }
    }

    // Tìm tiện ích theo ID
    public Amenity findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Amenity.class, id);
        }
    }

    // Lưu tiện ích mới
    public boolean save(Amenity amenity) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(amenity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật tiện ích
    public boolean update(Amenity amenity) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(amenity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm tiện ích (chuyển isDeleted = true)
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Amenity amenity = session.get(Amenity.class, id);
            if (amenity != null) {
                amenity.setIsDeleted(true);
                session.update(amenity);
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
