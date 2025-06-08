package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Permission;
import com.example.hotelmanagement.Models.Role;
import com.example.hotelmanagement.Models.RolePermission;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RolePermissionDAO {

    private final PermissionDAO permissionDAO = new PermissionDAO();

    // Lấy danh sách tên quyền từ roleId
    public List<Permission> getPermissionsByRoleId(int roleId) {
        try (Session session = HibernateUtils.getSession()) {
            String hql = "SELECT rp.permission FROM RolePermission rp WHERE rp.role.id = :roleId";
            return session.createQuery(hql, Permission.class)
                    .setParameter("roleId", roleId)
                    .list();
        }
    }


    public boolean deleteByRoleId(int roleId) {
        Transaction tx = null;
        Session session = null;

        try {
            session = HibernateUtils.getSession();
            tx = session.beginTransaction();

            String hql = "DELETE FROM RolePermission rp WHERE rp.role.id = :roleId";
            session.createQuery(hql)
                    .setParameter("roleId", roleId)
                    .executeUpdate();

            tx.commit();
            return true;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    // Gán danh sách quyền cho một role (xóa quyền cũ trước khi thêm mới)
    public boolean saveRolePermissions(int roleId, Set<String> permissionNames) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtils.getSession();
            tx = session.beginTransaction();

            Role role = session.get(Role.class, roleId);
            if (role == null) {
                return false;
            }

            // Xóa quyền cũ (dùng query, không load từng entity)
            session.createQuery("DELETE FROM RolePermission rp WHERE rp.roleID = :roleId")
                    .setParameter("roleId", roleId)
                    .executeUpdate();

            for (String permName : permissionNames) {
                Permission permission = permissionDAO.findByName(permName);
                if (permission != null) {
                    // Tạo mới RolePermission và set ID riêng biệt
                    RolePermission rp = new RolePermission();
                    rp.setRoleID(roleId);  // set roleID (int)
                    rp.setPermissionID(permission.getPermissionID());  // set permissionID (int)

                    // set quan hệ ManyToOne nếu muốn
                    rp.setRole(role);
                    rp.setPermission(permission);

                    // Dùng session.save hoặc session.merge nếu cần cập nhật
                    session.save(rp);
                }
            }

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
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

    public boolean addPermission(int roleId, String viewName) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();

            Role role = session.get(Role.class, roleId);
            if (role == null) {
                System.err.println("Không tìm thấy Role với ID: " + roleId);
                return false;
            }

            Permission permission = session.createQuery("FROM Permission WHERE permissionName = :name", Permission.class)
                    .setParameter("name", viewName)
                    .uniqueResult();
            if (permission == null) {
                System.err.println("Không tìm thấy Permission: " + viewName);
                return false;
            }

            RolePermission rp = new RolePermission();
            rp.setRole(role);
            rp.setPermission(permission);
            session.save(rp);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

}
