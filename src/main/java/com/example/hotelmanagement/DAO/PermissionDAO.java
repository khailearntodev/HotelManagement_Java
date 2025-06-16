package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Permission;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.example.hotelmanagement.Models.RolePermission;

import java.util.List;

public class PermissionDAO {

    // Lấy tất cả quyền từ bảng Permission
    public List<Permission> getAllPermissions() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Permission", Permission.class).list();
        }
    }

    // Tìm quyền theo tên
    public Permission findByName(String permissionName) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Permission WHERE permissionName = :name", Permission.class)
                    .setParameter("name", permissionName)
                    .uniqueResult();
        }
    }

    // Tìm quyền theo ID
    public Permission findById(int permissionId) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Permission.class, permissionId);
        }
    }

    // Thêm quyền mới
    public boolean save(Permission permission) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(permission);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public List<Permission> getPermissionsByRoleId(int roleId) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "SELECT rp.permission FROM RolePermission rp WHERE rp.role.id = :roleId", Permission.class)
                    .setParameter("roleId", roleId)
                    .list();
        }
    }

}
