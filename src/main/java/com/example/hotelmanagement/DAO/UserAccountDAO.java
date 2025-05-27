package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Useraccount;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserAccountDAO {

    // Lấy tất cả user chưa bị xóa mềm
    public List<Useraccount> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Useraccount WHERE isDeleted = false", Useraccount.class).list();
        }
    }

    // Tìm user theo ID
    public Useraccount findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Useraccount.class, id);
        }
    }

    // Thêm user mới
    public boolean save(Useraccount useraccount) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(useraccount);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật user
    public boolean update(Useraccount useraccount) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(useraccount);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm user (đặt isDeleted = true)
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Useraccount entity = session.get(Useraccount.class, id);
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
