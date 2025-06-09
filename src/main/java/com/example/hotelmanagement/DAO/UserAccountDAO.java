package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Employee;
import com.example.hotelmanagement.Models.Role;
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

    public boolean save(Useraccount useraccount, int employeeId, int roleId) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtils.getSession();
            tx = session.beginTransaction();

            Employee employee = session.get(Employee.class, employeeId);
            if (employee == null) {
                return false;
            }
            useraccount.setEmployeeID(employee);

            Role role = session.get(Role.class, roleId);
            if (role == null) {
                return false;
            }
            useraccount.setRoleID(role);

            session.save(useraccount);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
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

    public boolean softDelete(int employeeId) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();

            Useraccount entity = session.createQuery(
                            "FROM Useraccount WHERE employeeID.id = :empId AND isDeleted = false", Useraccount.class)
                    .setParameter("empId", employeeId)
                    .uniqueResult();

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


    public Useraccount findByUsername(String username) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Useraccount ua JOIN FETCH ua.employeeID WHERE ua.username = :username", Useraccount.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }

    public Useraccount findByEmployeeId(int employeeId) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT ua FROM Useraccount ua LEFT JOIN FETCH ua.roleID " +
                                    "WHERE ua.employeeID.id = :employeeId AND ua.isDeleted = false",
                            Useraccount.class)
                    .setParameter("employeeId", employeeId)
                    .uniqueResult();
        }
    }
}
