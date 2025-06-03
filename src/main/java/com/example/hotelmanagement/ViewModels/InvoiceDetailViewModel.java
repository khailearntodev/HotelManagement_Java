package com.example.hotelmanagement.ViewModels;


import javafx.beans.property.*;
import java.math.BigDecimal;

public class InvoiceDetailViewModel {
    private final IntegerProperty soThuTu = new SimpleIntegerProperty();
    private final IntegerProperty soPhong = new SimpleIntegerProperty();
    private final IntegerProperty soNgayThue = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> phiDichVu = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> donGiaPhong = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> thanhTien = new SimpleObjectProperty<>();

    public InvoiceDetailViewModel(int stt, int soPhong, int ngay, BigDecimal phiDV, BigDecimal gia, BigDecimal tong) {
        this.soThuTu.set(stt);
        this.soPhong.set(soPhong);
        this.soNgayThue.set(ngay);
        this.phiDichVu.set(phiDV);
        this.donGiaPhong.set(gia);
        this.thanhTien.set(tong);
    }

    public IntegerProperty soThuTuProperty() { return soThuTu; }
    public IntegerProperty soPhongProperty() { return soPhong; }
    public IntegerProperty soNgayThueProperty() { return soNgayThue; }
    public ObjectProperty<BigDecimal> phiDichVuProperty() { return phiDichVu; }
    public ObjectProperty<BigDecimal> donGiaPhongProperty() { return donGiaPhong; }
    public ObjectProperty<BigDecimal> thanhTienProperty() { return thanhTien; }
}

