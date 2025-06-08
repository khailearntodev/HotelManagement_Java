package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.DTO.Dashboard_BookingDisplay;
import com.example.hotelmanagement.Models.Prebooking;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class PrebookingDAO {

    // Lấy tất cả prebooking chưa xóa
    public List<Prebooking> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                    "SELECT DISTINCT p FROM Prebooking p " +
                            "JOIN FETCH p.roomID r " +
                            "JOIN FETCH r.roomTypeID " +
                            "LEFT JOIN FETCH r.reservations " +
                            "WHERE p.isDeleted = false",
                    Prebooking.class
            ).list();
        }
    }



    // Tìm Prebooking theo ID
    public Prebooking findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.get(Prebooking.class, id);
        }
    }

    // Thêm Prebooking mới
    public boolean save(Prebooking prebooking) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.save(prebooking);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật Prebooking
    public boolean update(Prebooking prebooking) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(prebooking);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm Prebooking
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Prebooking prebooking = session.get(Prebooking.class, id);
            if (prebooking != null) {
                prebooking.setIsDeleted(true);
                session.update(prebooking);
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

    public int countByDate(LocalDate date) {
        try (Session session = HibernateUtils.getSession()) {
            Instant instantDate = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            return ((Long) session.createQuery("""
            SELECT COUNT(p) FROM Prebooking p 
            WHERE p.bookingDate = :date AND p.isDeleted = false
        """)
                    .setParameter("date", instantDate)
                    .uniqueResult()).intValue();
        }
    }


    // Danh sách Prebooking còn hiệu lực để chọn Check-In
    public List<Prebooking> getAvailableBookings() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery("""
            FROM Prebooking 
            WHERE isDeleted = false AND checkInDate = CURRENT_DATE
        """, Prebooking.class).list();
        }
    }

    public List<Dashboard_BookingDisplay> getRecentBookingDisplays() {
        try (Session session = HibernateUtils.getSession()) {
            String sql = "SELECT TOP (10) p.BookingCode, p.CustomerName, r.RoomNumber, rt.TypeName, r.Status, p.CheckInDate, p.CheckOutDate, 'Prebooking' " +
                    "FROM PREBOOKING p " +
                    "JOIN ROOM r ON p.RoomID = r.RoomID " +
                    "JOIN ROOMTYPE rt ON rt.RoomTypeID = r.RoomTypeID " +
                    "WHERE p.IsDeleted = 0 AND p.ReservationID = NULL " +
                    "ORDER BY p.CheckInDate DESC";

            List<Object[]> rows = session.createNativeQuery(sql).list();
            System.out.println("Số dòng từ DB (Prebooking/Reservation): " + rows.size());
            List<Dashboard_BookingDisplay> result = new ArrayList<>();

            for (Object[] row : rows) {
                String id =  row[0].toString();
                String customerName = (String) row[1];
                int roomNumber = ((Number) row[2]).intValue();
                String roomType = (String) row[3];
                int status = ((Number) row[4]).intValue();
                Instant checkInDate = row[5] != null ? ((java.util.Date) row[5]).toInstant() : null;
                Instant checkOutDate = row[6] != null ? ((java.util.Date) row[6]).toInstant() : null;
                String sourceType = (String) row[7];

                Dashboard_BookingDisplay bd = new Dashboard_BookingDisplay(
                        id,
                        customerName,
                        roomNumber,
                        roomType,
                        status,
                        checkInDate,
                        checkOutDate,
                        sourceType
                );
                result.add(bd);
            }

            return result;
        }
    }


}
