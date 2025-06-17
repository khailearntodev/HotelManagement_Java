package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.DAO.*;
import com.example.hotelmanagement.Models.*;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

public class RevenueReportService {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();
    private final RevenueReportDAO revenueReportDAO = new RevenueReportDAO();
    private final RevenueReportDetailDAO revenueReportDetailDAO = new RevenueReportDetailDAO();
    public boolean generateReport(int month, int year) {
        Session session = null;
        Transaction tx = null;

        try {
            session = HibernateUtils.getSession();
            tx = session.beginTransaction();

            List<RevenueReport> existingReports = session.createQuery(
                            "FROM RevenueReport WHERE month = :m AND year = :y", RevenueReport.class)
                    .setParameter("m", month)
                    .setParameter("y", year)
                    .list();

            RevenueReport newReport;

            if (!existingReports.isEmpty()) {
                System.out.println("Báo cáo cho tháng " + month + "/" + year + " đã tồn tại. Đang cập nhật...");

                RevenueReport existingReport = existingReports.get(0);

                session.createQuery("DELETE FROM RevenueReportDetail WHERE reportID.id = :reportId")
                        .setParameter("reportId", existingReport.getId())
                        .executeUpdate();

                newReport = existingReport;
            } else {
                newReport = new RevenueReport();
                newReport.setMonth(month);
                newReport.setYear(year);
                newReport.setIsDeleted(false);
                session.save(newReport);
            }


            List<Invoice> invoicesInPeriod = invoiceDAO.findByMonthAndYear(session, month, year);
            if (invoicesInPeriod.isEmpty()) {
                System.out.println("Không có dữ liệu hóa đơn cho tháng " + month + "/" + year);
                tx.rollback();
                return false;
            }

            // Lấy reservation theo tháng và năm
            List<Reservation> reservationsInPeriod = reservationDAO.findByMonthAndYear(session, month, year);
            if (reservationsInPeriod.isEmpty()) {
                System.out.println("Không có dữ liệu hóa đơn cho tháng " + month + "/" + year);
                return false;
            }

// Tính tổng doanh thu dịch vụ
            BigDecimal totalServiceRevenue = reservationsInPeriod.stream()
                    .flatMap(reservation -> serviceBookingDAO.findByReservationId(reservation.getId()).stream())
                    .filter(sb -> "Đã xử lý".equals(sb.getStatus()))
                    .map(sb -> sb.getServiceID().getPrice().multiply(BigDecimal.valueOf(sb.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

// Tính tổng doanh thu thuê phòng
            BigDecimal totalRental = reservationsInPeriod.stream()
                    .map(Reservation::getTotal)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

// Cập nhật vào báo cáo
            newReport.setTotalService(totalServiceRevenue);
            newReport.setTotalRental(totalRental);
            session.update(newReport);

// Tạo chi tiết báo cáo
            Map<Roomtype, List<Reservation>> reservationsByRoomType = reservationsInPeriod.stream()
                    .filter(r -> r.getRoomID() != null && r.getRoomID().getRoomTypeID() != null)
                    .collect(Collectors.groupingBy(r -> r.getRoomID().getRoomTypeID()));

            for (Map.Entry<Roomtype, List<Reservation>> entry : reservationsByRoomType.entrySet()) {
                Roomtype roomType = entry.getKey();
                List<Reservation> reservationsForType = entry.getValue();

                int totalReservations = reservationsForType.size();
                BigDecimal revenueForType = reservationsForType.stream()
                        .map(Reservation::getTotal)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                RevenueReportDetail detail = new RevenueReportDetail();
                detail.setReportID(newReport);
                detail.setRoomTypeID(roomType);
                detail.setTotalReservation(totalReservations);
                detail.setRevenue(revenueForType);
                detail.setIsDeleted(false);
                session.save(detail);
            }

            tx.commit();
            return true;

        } catch (NullPointerException npe) {
            System.err.println("!!! LỖI DỮ LIỆU: CÓ MỘT RESERVATION KHÔNG CÓ ROOM HOẶC ROOM KHÔNG CÓ ROOMTYPE HOẶC TOTAL NULL TRONG RESERVATION !!!");
            npe.printStackTrace();
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    System.err.println("Không thể rollback vì session đã đóng.");
                }
            }
            return false;
        } catch (Exception e) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    System.err.println("Không thể rollback vì session đã đóng.");
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

}