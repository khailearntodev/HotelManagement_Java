package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Customertype;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CustomerTypeDAO {

    // Lấy tất cả loại khách hàng chưa bị xóa
    public List<Customertype> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Customertype WHERE isDeleted = false", Customertype.class).list();
        }
    }

    // Tìm loại khách hàng theo ID
    public Customertype findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Customertype.class, id);
        }
    }

    // Tìm loại khách hàng theo tên (tùy chọn)
    public Customertype findByTypeName(String typeName) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Customertype WHERE typeName = :typeName AND isDeleted = false", Customertype.class)
                    .setParameter("typeName", typeName)
                    .uniqueResult();
        }
    }

    // Thêm mới loại khách hàng
    public boolean save(Customertype customertype) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(customertype);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật loại khách hàng
    public boolean update(Customertype customertype) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(customertype);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm loại khách hàng
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Customertype ct = session.get(Customertype.class, id);
            if (ct != null) {
                ct.setIsDeleted(true);
                session.update(ct);
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
    public Customertype findByName(String name) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Customertype WHERE typeName = :name", Customertype.class)
                    .setParameter("name", name)
                    .uniqueResult();
        }
    }
}
