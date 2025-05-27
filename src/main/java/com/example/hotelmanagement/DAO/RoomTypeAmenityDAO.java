package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Roomtypeamenity;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RoomTypeAmenityDAO {

    // Lấy danh sách tất cả Roomtypeamenity chưa bị soft delete
    public List<Roomtypeamenity> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Roomtypeamenity WHERE isDeleted = false", Roomtypeamenity.class).list();
        }
    }

    // Tìm Roomtypeamenity theo ID
    public Roomtypeamenity findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Roomtypeamenity.class, id);
        }
    }

    // Thêm Roomtypeamenity mới
    public boolean save(Roomtypeamenity roomtypeamenity) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(roomtypeamenity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật Roomtypeamenity
    public boolean update(Roomtypeamenity roomtypeamenity) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(roomtypeamenity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm Roomtypeamenity
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Roomtypeamenity entity = session.get(Roomtypeamenity.class, id);
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
