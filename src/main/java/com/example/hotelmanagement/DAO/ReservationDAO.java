package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.DTO.Dashboard_BookingDisplay;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    // Lấy tất cả Reservation chưa bị xóa
    public List<Reservation> getAll() {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                    "SELECT r FROM Reservation r " +
                            "JOIN FETCH r.roomID ro " +
                            "JOIN FETCH ro.roomTypeID rt " +
                            "WHERE r.isDeleted = false", Reservation.class
            ).list();

        }
    }

    // Tìm Reservation theo ID
    public Reservation findById(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "JOIN FETCH r.roomID ro " +
                                    "JOIN FETCH ro.roomTypeID rt " +
                                    "LEFT JOIN FETCH r.servicebookings sb " +
                                    "LEFT JOIN FETCH sb.serviceID " +
                                    "LEFT JOIN FETCH r.reservationguests rg " +
                                    "LEFT JOIN FETCH rg.customerID " +
                                    "WHERE r.id = :id AND r.isDeleted = false",
                            Reservation.class)
                    .setParameter("id", id)
                    .uniqueResultOptional().orElse(null);
        }
    }
    public Reservation findByIdForServiceBK(int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "LEFT JOIN FETCH r.roomID ro " +
                                    "LEFT JOIN FETCH ro.roomTypeID " +
                                    "LEFT JOIN FETCH r.servicebookings sb " +
                                    "LEFT JOIN FETCH sb.serviceID " +
                                    "WHERE r.id = :id AND r.isDeleted = false",
                            Reservation.class)
                    .setParameter("id", id)
                    .uniqueResultOptional().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Reservation findActiveReservationByRoomId(int roomId) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "LEFT JOIN FETCH r.roomID ro " +
                                    "LEFT JOIN FETCH ro.roomTypeID " +
                                    "LEFT JOIN FETCH r.reservationguests " +
                                    "LEFT JOIN FETCH r.servicebookings sb " +
                                    "LEFT JOIN FETCH sb.serviceID " +
                                    "WHERE r.roomID.id = :roomId " +
                                    "AND r.invoiceID IS NULL " +
                                    "AND r.isDeleted = false",
                            Reservation.class)
                    .setParameter("roomId", roomId)
                    .uniqueResultOptional().orElse(null); // Sử dụng uniqueResultOptional để trả về null nếu không tìm thấy
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Reservation findByRoomNumber(int roomNumber) {
        try (Session session = HibernateUtils.getSession()) {
            return session.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "JOIN FETCH r.roomID ro " +
                                    "JOIN FETCH ro.roomTypeID rt " +
                                    "LEFT JOIN FETCH r.servicebookings sb " +
                                    "LEFT JOIN FETCH sb.serviceID " +
                                    "WHERE ro.roomNumber = :roomNumber AND r.isDeleted = false AND r.invoiceID IS NULL ",
                            Reservation.class)
                    .setParameter("roomNumber", roomNumber)
                    .uniqueResultOptional().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Thêm Reservation mới
    public boolean save(Reservation reservation) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtils.getSession();
            tx = session.beginTransaction();
            session.save(reservation);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
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


    // Cập nhật Reservation
    public boolean update(Reservation reservation) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.update(reservation);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // Xóa mềm Reservation
    public boolean softDelete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            Reservation reservation = session.get(Reservation.class, id);
            if (reservation != null) {
                reservation.setIsDeleted(true);
                session.update(reservation);
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

    public int countCheckInByDate(LocalDate date) {
        try (Session session = HibernateUtils.getSession()) {
            Instant startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant startOfNextDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

            return ((Long) session.createQuery("""
            SELECT COUNT(r) FROM Reservation r 
            WHERE r.checkInDate >= :startOfDay AND r.checkInDate < :startOfNextDay AND r.isDeleted = false
        """)
                    .setParameter("startOfDay", startOfDay)
                    .setParameter("startOfNextDay", startOfNextDay)
                    .uniqueResult()
            ).intValue();
        }
    }

    public int countCheckOutByDate(LocalDate date) {
        try (Session session = HibernateUtils.getSession()) {
            Instant startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant startOfNextDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

            return ((Long) session.createQuery("""
            SELECT COUNT(r) FROM Reservation r 
            WHERE r.checkOutDate >= :startOfDay AND r.checkOutDate < :startOfNextDay AND r.isDeleted = false
        """)
                    .setParameter("startOfDay", startOfDay)
                    .setParameter("startOfNextDay", startOfNextDay)
                    .uniqueResult()
            ).intValue();
        }
    }

    public List<Dashboard_BookingDisplay> getRecentBookingDisplays() {
        try (Session session = HibernateUtils.getSession()) {
            String sql = """
                        SELECT TOP (10) r.ReservationID, c.FullName, rm.RoomNumber, rmt.TypeName, rm.Status, r.CheckInDate, r.CheckOutDate, 'Reservation'
                        FROM RESERVATION r
                        JOIN RESERVATIONGUEST rg ON r.ReservationID = rg.ReservationID
                        JOIN CUSTOMER c ON rg.CustomerID = c.CustomerID
                        JOIN ROOM rm ON r.RoomID = rm.RoomID
                        JOIN ROOMTYPE rmt ON rmt.RoomTypeID = rm.RoomTypeID
                        WHERE r.IsDeleted = 0
                        ORDER BY r.CheckInDate DESC
                    """;
            List<Object[]> rows = session.createNativeQuery(sql).list();
            List<Dashboard_BookingDisplay> result = new ArrayList<>();

            for (Object[] row : rows) {
                String id = row[0] != null ? row[0].toString() : "N/A";
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

