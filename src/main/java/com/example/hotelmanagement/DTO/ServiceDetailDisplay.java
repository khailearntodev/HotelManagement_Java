package com.example.hotelmanagement.DTO;

import com.example.hotelmanagement.Models.Invoice;
import com.example.hotelmanagement.Models.Service;
import javafx.beans.property.*;

import java.math.BigDecimal;

public class ServiceDetailDisplay {
    public final IntegerProperty soThuTu=new SimpleIntegerProperty();
    private final IntegerProperty soPhong = new SimpleIntegerProperty();
    private final StringProperty thoiGianDat = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> giaDichVu = new SimpleObjectProperty<>();
    private final StringProperty imageLink = new SimpleStringProperty();

//    public ServiceDetailDisplay(Service service) {
//        System.out.println("Initialized! Service:");
//        this.maDichVu.set(service.getId());
//        this.tenDichVu.set(service.getServiceName());
//        this.giaDichVu.set(service.getPrice());
//        this.imageLink.set(service.getServiceImage());
//    }
//    public int getMaDichVu() { return maDichVu.get(); }
//    public String getTenDichVu() { return tenDichVu.get(); }
//    public  BigDecimal getGiaDichVu() { return giaDichVu.get(); }
//    public String getImageLink() { return imageLink.get(); }
//
//    public IntegerProperty maDichVuProperty() { return maDichVu; }
//    public StringProperty tenDichVuProperty() { return tenDichVu; }
//    public ObjectProperty<BigDecimal> giaDichVuProperty() { return giaDichVu; }
//    public StringProperty imageLinkProperty() { return imageLink; }
//
//    public void setTenDichVu(String value) { tenDichVu.set(value); }
//    public void setGiaDichVu(BigDecimal value) { giaDichVu.set(value); }
//    public void setImageLink(String value) { imageLink.set(value); }
}
