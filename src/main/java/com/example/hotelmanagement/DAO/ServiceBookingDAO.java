package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.DTO.Dashboard_ActivityItem;
import com.example.hotelmanagement.Models.Servicebooking;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceBookingDAO {

    // Lấy danh sách tất cả Servicebooking chưa bị xóa mềm
    public List<Servicebooking> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("FROM Servicebooking WHERE isDeleted = false", Servicebooking.class).list();
        }
    }

    // Tìm Servicebooking theo ID
    public Servicebooking findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Servicebooking.class, id);
        }
    }

    // Thêm Servicebooking mới
    public boolean save(Servicebooking servicebooking) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(servicebooking);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật Servicebooking
    public boolean update(Servicebooking servicebooking) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(servicebooking);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm Servicebooking
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Servicebooking entity = session.get(Servicebooking.class, id);
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

    public List<Dashboard_ActivityItem> getRecentActivities() {
        try (Session session = HibernateUtils.getSession()) {
            String hql = "FROM Servicebooking WHERE isDeleted = false ORDER BY bookingDate DESC";
            List<Servicebooking> bookings = session.createQuery(hql, Servicebooking.class)
                    .setMaxResults(10)
                    .list();

            return bookings.stream()
                    .map(this::mapToActivityItem)
                    .collect(Collectors.toList());
        }
    }

    private Dashboard_ActivityItem mapToActivityItem(Servicebooking booking) {
        String description = booking.getServiceID().getServiceName();
        int roomNumber = booking.getReservationID().getRoomID().getRoomNumber();
        Instant timestamp = booking.getBookingDate().atZone(ZoneId.systemDefault()).toInstant();
        return new Dashboard_ActivityItem(description,roomNumber, timestamp);
    }
}
