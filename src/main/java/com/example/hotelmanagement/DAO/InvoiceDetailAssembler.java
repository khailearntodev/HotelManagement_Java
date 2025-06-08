package com.example.hotelmanagement.DAO;

import com.example.hotelmanagement.DTO.InvoiceDetail;
import com.example.hotelmanagement.Models.Invoice;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Models.Servicebooking;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDetailAssembler {

    public static List<InvoiceDetail> buildDetailsFromInvoice(Invoice invoice) {
        List<InvoiceDetail> details = new ArrayList<>();
        int stt = 1;

        for (Reservation reservation : invoice.getReservations()) {
            Room room = reservation.getRoomID();
            BigDecimal giaPhong =reservation.getPrice();

            long soNgay = Duration.between(
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate()
            ).toDays();

            if (soNgay <= 0) soNgay = 1;

            // Tổng phí dịch vụ cho Reservation này
            BigDecimal tongPhiDV = reservation.getServicebookings().stream()
                    .map(sb -> sb.getServiceID().getPrice().multiply(BigDecimal.valueOf(sb.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal thanhTien = giaPhong.multiply(BigDecimal.valueOf(soNgay)).add(tongPhiDV);

            InvoiceDetail detail = new InvoiceDetail();
            detail.soThuTuProperty().set(stt++);
            detail.soPhongProperty().set(room.getRoomNumber());
            detail.soNgayThueProperty().set((int) soNgay);
            detail.donGiaPhongProperty().set(giaPhong);
            detail.phiDichVuProperty().set(tongPhiDV);
            detail.thanhTienProperty().set(thanhTien);

            details.add(detail);
        }

        return details;
    }
}
