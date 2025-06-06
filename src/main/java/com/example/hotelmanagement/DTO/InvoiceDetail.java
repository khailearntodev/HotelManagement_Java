package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.Models.Service;
import javafx.beans.property.*;

import java.math.BigDecimal;

public class InvoiceDetail {
    private final IntegerProperty soThuTu = new SimpleIntegerProperty();
    private final IntegerProperty soPhong = new SimpleIntegerProperty();
    private final IntegerProperty soNgayThue = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> phiDichVu = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> donGiaPhong = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> thanhTien = new SimpleObjectProperty<>();

    public IntegerProperty soThuTuProperty() { return soThuTu; }
    public IntegerProperty soPhongProperty() { return soPhong; }
    public IntegerProperty soNgayThueProperty() { return soNgayThue; }
    public ObjectProperty<BigDecimal> phiDichVuProperty() { return phiDichVu; }
    public ObjectProperty<BigDecimal> donGiaPhongProperty() { return donGiaPhong; }
    public ObjectProperty<BigDecimal> thanhTienProperty() { return thanhTien; }
}

