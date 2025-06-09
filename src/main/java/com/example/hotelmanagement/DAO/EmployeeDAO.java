package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Employee;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EmployeeDAO {

    // Lấy tất cả nhân viên chưa bị xóa
    public List<Employee> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Employee WHERE isDeleted = false", Employee.class).list();
        }
    }

    // Tìm nhân viên theo ID
    public Employee findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Employee.class, id);
        }
    }

    // Tìm nhân viên theo số CMND/CCCD
    public Employee findByIdentityNumber(String identityNumber) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "FROM Employee WHERE identityNumber = :identityNumber AND isDeleted = false", Employee.class)
                    .setParameter("identityNumber", identityNumber)
                    .uniqueResult();
        }
    }

    // Thêm nhân viên mới
    public boolean save(Employee employee) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(employee);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật thông tin nhân viên
    public boolean update(Employee employee) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(employee);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm nhân viên
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Employee emp = session.get(Employee.class, id);
            if (emp != null) {
                emp.setIsDeleted(true);
                session.update(emp);
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

    public List<String> getAllPositions() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                    "SELECT DISTINCT e.position FROM Employee e WHERE e.isDeleted = false AND e.position IS NOT NULL", String.class
            ).list();
        }
    }

}
