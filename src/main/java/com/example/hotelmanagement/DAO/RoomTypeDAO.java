package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Roomtype;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RoomTypeDAO {

    // Lấy danh sách tất cả Roomtype chưa xóa
    public List<Roomtype> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Roomtype WHERE isDeleted = false", Roomtype.class).list();
        }
    }

    // Tìm Roomtype theo ID
    public Roomtype findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Roomtype.class, id);
        }
    }

    // Thêm Roomtype mới
    public boolean save(Roomtype roomtype) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(roomtype);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật Roomtype
    public boolean update(Roomtype roomtype) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(roomtype);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm Roomtype
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Roomtype roomtype = session.get(Roomtype.class, id);
            if (roomtype != null) {
                roomtype.setIsDeleted(true);
                session.update(roomtype);
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
