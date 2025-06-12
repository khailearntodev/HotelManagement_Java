package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.Models.Invoice;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
            return session.createQuery(
                            "SELECT i FROM Invoice i " +
                                    "LEFT JOIN FETCH i.reservations r " + // Tải tất cả Reservations của Invoice
                                    "LEFT JOIN FETCH r.roomID ro " +      // Tải Room của mỗi Reservation
                                    "LEFT JOIN FETCH ro.roomTypeID rt " + // Tải RoomType của mỗi Room
                                    "LEFT JOIN FETCH r.servicebookings sb " + // Tải Servicebookings của mỗi Reservation
                                    "LEFT JOIN FETCH sb.serviceID s " +  // Tải Service của mỗi Servicebooking
                                    "LEFT JOIN FETCH r.reservationguests rg " + // Tải ReservationGuests của mỗi Reservation
                                    "LEFT JOIN FETCH rg.customerID c " + // Tải Customer của mỗi ReservationGuest
                                    "WHERE i.id = :id AND i.isDeleted = false",
                            Invoice.class)
                    .setParameter("id", id)
                    .uniqueResultOptional().orElse(null);
        }
    }

    // Thêm hóa đơn mới
    public boolean save(Invoice invoice) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtils.getSession();
            tx = session.beginTransaction();
            session.save(invoice);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
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

    // Xóa hóa đơn (đánh dấu là đã xóa)
    public boolean delete(Invoice invoice) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            invoice.setIsDeleted(true); // Đánh dấu là đã xóa mềm
            session.update(invoice);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm hóa đơn theo tên khách hàng
    public List<Invoice> findByCustomerName(String customerName) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Invoice WHERE customerName LIKE :name AND isDeleted = false", Invoice.class)
                    .setParameter("name", "%" + customerName + "%")
                    .list();
        }
    }
    public BigDecimal findDepositAmountByReservationId(int reservationId) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Optional<BigDecimal> depositAmount = session.createQuery(
                            "SELECT p.price FROM Prebooking p " +
                                    "JOIN p.reservationID r " +
                                    "WHERE r.id = :reservationId " +
                                    "AND p.isDeleted = false",
                            BigDecimal.class)
                    .setParameter("reservationId", reservationId)
                    .uniqueResultOptional();
            return depositAmount.orElse(BigDecimal.ZERO);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    public Invoice getInvoiceWithDetails(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "SELECT DISTINCT i FROM Invoice i " +
                                    "LEFT JOIN FETCH i.employeeID " +
                                    "LEFT JOIN FETCH i.reservations r " + // Tải tất cả Reservations của Invoice
                                    "LEFT JOIN FETCH r.roomID ro " +      // Tải Room của mỗi Reservation
                                    "LEFT JOIN FETCH ro.roomTypeID rt " + // Tải RoomType của mỗi Room (nếu cần trong UI)
                                    "LEFT JOIN FETCH r.servicebookings sb " + // Tải Servicebookings của mỗi Reservation
                                    "LEFT JOIN FETCH sb.serviceID s " +  // Tải Service của mỗi Servicebooking
                                    "LEFT JOIN FETCH r.reservationguests rg " + // Tải ReservationGuests của mỗi Reservation
                                    "LEFT JOIN FETCH rg.customerID c " + // Tải Customer của mỗi ReservationGuest
                                    "WHERE i.id = :id AND i.isDeleted = false", // Đảm bảo điều kiện xóa mềm nếu có
                            Invoice.class)
                    .setParameter("id", id)
                    .uniqueResultOptional().orElse(null);
        }
    }
    public List<Invoice> findByMonthAndYear(int month, int year) {
        try (Session session = HibernateUtils.getSession()) {
            String hql = "FROM Invoice i WHERE MONTH(i.issueDate) = :month AND YEAR(i.issueDate) = :year AND i.isDeleted = false";
            return session.createQuery(hql, Invoice.class)
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .list();
        }
    }
}