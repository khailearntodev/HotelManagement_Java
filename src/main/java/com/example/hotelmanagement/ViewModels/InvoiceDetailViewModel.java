package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.EmployeeDAO;
import com.example.hotelmanagement.DAO.InvoiceDAO;
import com.example.hotelmanagement.DAO.ReservationDAO;
import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.Models.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.Getter;

@Getter
public class InvoiceDetailViewModel {
    private final IntegerProperty soThuTu = new SimpleIntegerProperty();
    private final IntegerProperty soPhong = new SimpleIntegerProperty();
    private final IntegerProperty soNgayThue = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> phiDichVu = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> donGiaPhong = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> thanhTien = new SimpleObjectProperty<>();

    @Getter
    private final Reservation reservation;

    private final ObservableList<InvoiceDetailViewModel> reservationDetails = FXCollections.observableArrayList();
    private final ObjectProperty<BigDecimal> tongTien = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<Invoice> invoice = new SimpleObjectProperty<>();

    public InvoiceDetailViewModel(Reservation reservation) {
        this.reservation = reservation;
        initializeSingleReservation(reservation);
    }

    public InvoiceDetailViewModel(List<Reservation> reservations) {
        this.reservation = null;
        Invoice newInvoice = new Invoice();
        Employee employee = new EmployeeDAO().findById(23520610); //for test
        newInvoice.setEmployeeID(employee);
        newInvoice.setIssueDate(java.time.Instant.now());
        invoice.set(newInvoice);

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < reservations.size(); i++) {
            InvoiceDetailViewModel detail = new InvoiceDetailViewModel(reservations.get(i));
            detail.soThuTu.set(i + 1);
            reservationDetails.add(detail);
            total = total.add(detail.thanhTien.get());
        }
        tongTien.set(total);
        newInvoice.setTotalAmount(total);
    }

    private void initializeSingleReservation(Reservation reservation) {
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
        this.phiDichVu.set(phiDV);
        this.thanhTien.set(giaPhong.multiply(BigDecimal.valueOf(days)).add(phiDV));
    }

    public void saveInvoice() {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        invoiceDAO.save(invoice.get());

        ReservationDAO reservationDAO = new ReservationDAO();
        RoomDAO roomDAO = new RoomDAO();

        for (InvoiceDetailViewModel detail : reservationDetails) {
            Reservation res = detail.getReservation();
            res.setInvoiceID(invoice.get());
            reservationDAO.update(res);

            Room room = res.getRoomID();
            room.setStatus(1);
            roomDAO.update(room);
        }
    }
    public IntegerProperty soThuTuProperty() { return soThuTu; }
    public IntegerProperty soPhongProperty() { return soPhong; }
    public IntegerProperty soNgayThueProperty() { return soNgayThue; }
    public ObjectProperty<BigDecimal> phiDichVuProperty() { return phiDichVu; }
    public ObjectProperty<BigDecimal> donGiaPhongProperty() { return donGiaPhong; }
    public ObjectProperty<BigDecimal> thanhTienProperty() { return thanhTien; }

    public ObservableList<InvoiceDetailViewModel> getReservationDetails() { return reservationDetails; }
    public ObjectProperty<BigDecimal> tongTienProperty() { return tongTien; }
    public ObjectProperty<Invoice> invoiceProperty() { return invoice; }
}