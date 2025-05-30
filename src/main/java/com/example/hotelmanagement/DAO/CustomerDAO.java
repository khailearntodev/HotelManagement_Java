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
    public Customer findByIdentityNumber(String identityNumber, String identityType) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "FROM Customer c JOIN FETCH c.customerTypeID " +
                                    "WHERE c.identityNumber = :identityNumber " +
                                    "AND c.identityType = :identityType " +
                                    "AND c.isDeleted = false",
                            Customer.class)
                    .setParameter("identityNumber", identityNumber)
                    .setParameter("identityType", identityType)
                    .uniqueResult();
        }
    }



    // Thêm khách hàng mới
    public boolean save(Customer customer) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtils.getSession();
            tx = session.beginTransaction();
            session.save(customer);
            tx.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Error during customer save: " + e.getMessage());

            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                try {
                    session.close();
                } catch (Exception closeEx) {
                    System.err.println("Error closing session: " + closeEx.getMessage());
                }
            }
        }
    }


    // Cập nhật thông tin khách hàng
    public boolean update(Customer customer) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();

            // Kiểm tra customer có ID hợp lệ không
            if (customer.getId() == null || customer.getId() <= 0) {
                System.err.println("Customer ID is invalid for update: " + customer.getId());
                return false;
            }

            // Kiểm tra customer có tồn tại trong DB không
            Customer existingCustomer = session.get(Customer.class, customer.getId());
            if (existingCustomer == null) {
                System.err.println("Customer not found with ID: " + customer.getId());
                return false;
            }

            // Sử dụng merge thay vì update để tránh lỗi detached object
            session.merge(customer);
            tx.commit();
            return true;

        } catch (Exception e) {
            System.err.println("Error during customer update: " + e.getMessage());
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
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
