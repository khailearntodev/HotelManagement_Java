package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Role;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RoleDAO {

    // Lấy tất cả các Role chưa bị xóa
    public List<Role> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Role WHERE isDeleted = false", Role.class).list();
        }
    }

    // Tìm Role theo ID
    public Role findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Role.class, id);
        }
    }

    // Tìm Role theo tên
    public Role findByName(String roleName) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Role WHERE roleName = :name AND isDeleted = false", Role.class)
                    .setParameter("name", roleName)
                    .uniqueResult();
        }
    }

    // Thêm mới Role
    public boolean save(Role role) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(role);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật Role
    public boolean update(Role role) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(role);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm Role (soft delete)
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Role role = session.get(Role.class, id);
            if (role != null) {
                role.setIsDeleted(true);
                session.update(role);
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
