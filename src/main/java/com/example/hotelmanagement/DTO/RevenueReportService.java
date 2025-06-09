package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.DAO.InvoiceDAO;
import com.example.hotelmanagement.DAO.RevenueReportDAO;
import com.example.hotelmanagement.DAO.RevenueReportDetailDAO;
import com.example.hotelmanagement.Models.*;
import com.example.hotelmanagement.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RevenueReportService {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final RevenueReportDAO revenueReportDAO = new RevenueReportDAO();
    private final RevenueReportDetailDAO revenueReportDetailDAO = new RevenueReportDetailDAO();

    public boolean generateReport(int month, int year) {
        if (revenueReportDAO.getByMonth(month, year) != null) {
            System.out.println("Báo cáo cho tháng " + month + "/" + year + " đã tồn tại.");
            return false;
        }

        List<Invoice> invoicesInPeriod = invoiceDAO.findByMonthAndYear(month, year);
        if (invoicesInPeriod.isEmpty()) {
            System.out.println("Không có dữ liệu hóa đơn cho tháng " + month + "/" + year);
            return false;
        }

        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();

            BigDecimal totalRevenue = invoicesInPeriod.stream()
                    .map(Invoice::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<Reservation> reservationsInPeriod = invoicesInPeriod.stream()
                    .flatMap(invoice -> invoice.getReservations().stream())
                    .collect(Collectors.toList());

            BigDecimal totalRental = reservationsInPeriod.stream()
                    .map(Reservation::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            RevenueReport newReport = new RevenueReport();
            newReport.setMonth(month);
            newReport.setYear(year);
            newReport.setTotalRevenue(totalRevenue);
            newReport.setTotalRental(totalRental);
            newReport.setIsDeleted(false);
            session.save(newReport);


            Map<Roomtype, List<Reservation>> reservationsByRoomType = reservationsInPeriod.stream()
                    .collect(Collectors.groupingBy(r -> r.getRoomID().getRoomTypeID()));

            for (Map.Entry<Roomtype, List<Reservation>> entry : reservationsByRoomType.entrySet()) {
                Roomtype roomType = entry.getKey();
                List<Reservation> reservationsForType = entry.getValue();

                int totalReservations = reservationsForType.size();
                BigDecimal revenueForType = reservationsForType.stream()
                        .map(Reservation::getTotal)
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
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}