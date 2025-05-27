package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Invoice;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class InvoiceDAO {

    // Lấy tất cả hóa đơn chưa bị xóa
    public List<Invoice> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Invoice WHERE isDeleted = false", Invoice.class).list();
        }
    }

    // Tìm hóa đơn theo ID
    public Invoice findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Invoice.class, id);
        }
    }

    // Thêm hóa đơn mới
    public boolean save(Invoice invoice) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(invoice);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật hóa đơn
    public boolean update(Invoice invoice) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(invoice);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm hóa đơn
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Invoice invoice = session.get(Invoice.class, id);
            if (invoice != null) {
                invoice.setIsDeleted(true);
                session.update(invoice);
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

    // Tìm theo tên khách hàng (tuỳ chọn)
    public List<Invoice> findByCustomerName(String customerName) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "FROM Invoice WHERE customerName LIKE :name AND isDeleted = false", Invoice.class)
                    .setParameter("name", "%" + customerName + "%")
                    .list();
        }
    }
}
