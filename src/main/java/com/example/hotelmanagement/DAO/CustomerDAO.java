package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CustomerDAO {

    // Lấy tất cả khách hàng chưa bị xóa
    public List<Customer> getAllCustomers() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Customer WHERE isDeleted = false", Customer.class).list();
        }
    }

    // Tìm khách hàng theo ID
    public Customer findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Customer.class, id);
        }
    }

    // Tìm khách hàng theo số CCCD/CMND
    public Customer findByIdentityNumber(String identityNumber) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "FROM Customer WHERE identityNumber = :identityNumber AND isDeleted = false", Customer.class)
                    .setParameter("identityNumber", identityNumber)
                    .uniqueResult();
        }
    }

    // Thêm khách hàng mới
    public boolean save(Customer customer) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(customer);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật thông tin khách hàng
    public boolean update(Customer customer) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(customer);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm khách hàng
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Customer customer = session.get(Customer.class, id);
            if (customer != null) {
                customer.setIsDeleted(true);
                session.update(customer);
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
