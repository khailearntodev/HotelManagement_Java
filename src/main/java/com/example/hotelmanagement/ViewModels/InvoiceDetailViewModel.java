package com.example.hotelmanagement.ViewModels;


import com.example.hotelmanagement.Models.Reservation;
import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

public class InvoiceDetailViewModel {
    private final IntegerProperty soThuTu = new SimpleIntegerProperty();
    private final IntegerProperty soPhong = new SimpleIntegerProperty();
    private final IntegerProperty soNgayThue = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> phiDichVu = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> donGiaPhong = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> thanhTien = new SimpleObjectProperty<>();
    private final Reservation reservation;


    public InvoiceDetailViewModel(Reservation reservation) {
        this.reservation = reservation;
        this.soPhong.set(reservation.getRoomID().getRoomNumber());

        long days = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (days <= 0) days = 1;
        this.soNgayThue.set((int) days);

        BigDecimal giaPhong = reservation.getRoomID().getRoomTypeID().getBasePrice();
        BigDecimal phiDV = reservation.getServicebookings().stream()
                .filter(sb -> "Đã xử lý".equals(sb.getStatus()))
                .map(sb -> sb.getServiceID().getPrice().multiply(BigDecimal.valueOf(sb.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.donGiaPhong.set(giaPhong);
        this.thanhTien.set(giaPhong.multiply(BigDecimal.valueOf(days)).add(phiDV));
    }

    public Reservation getReservation() {
        return reservation;
    }
    public IntegerProperty soThuTuProperty() { return soThuTu; }
    public IntegerProperty soPhongProperty() { return soPhong; }
    public IntegerProperty soNgayThueProperty() { return soNgayThue; }
    public ObjectProperty<BigDecimal> phiDichVuProperty() { return phiDichVu; }
    public ObjectProperty<BigDecimal> donGiaPhongProperty() { return donGiaPhong; }
    public ObjectProperty<BigDecimal> thanhTienProperty() { return thanhTien; }
}

