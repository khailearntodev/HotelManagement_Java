package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Service;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ServiceDAO {

    // Lấy danh sách tất cả Service chưa bị soft delete
    public List<Service> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Service WHERE isDeleted = false", Service.class).list();
        }
    }

    // Tìm Service theo ID
    public Service findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Service.class, id);
        }
    }

    // Thêm Service mới
    public boolean save(Service service) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(service);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật Service
    public boolean update(Service service) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(service);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm Service
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Service entity = session.get(Service.class, id);
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
