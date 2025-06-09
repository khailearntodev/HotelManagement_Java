package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.CustomerDAO;
import com.example.hotelmanagement.DAO.CustomerTypeDAO;
import com.example.hotelmanagement.DAO.ReservationGuestDAO;
import com.example.hotelmanagement.DTO.CustomerManagement_CustomerDisplay;
import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Customertype;
import com.example.hotelmanagement.Utils.HibernateUtils;
import javafx.beans.property.*;
import org.hibernate.Session;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class CustomerDetailViewModel {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CustomerTypeDAO customerTypeDAO = new CustomerTypeDAO();

    private final StringProperty hoTen = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> ngaySinh = new SimpleObjectProperty<>();
    private final StringProperty loaiKhach = new SimpleStringProperty();
    private final StringProperty tuoi = new SimpleStringProperty();
    private final BooleanProperty gioiTinh = new SimpleBooleanProperty();
    private final StringProperty cccd = new SimpleStringProperty();
    private final StringProperty diaChi = new SimpleStringProperty();
    private final StringProperty sdt = new SimpleStringProperty();
    private final StringProperty trangThai = new SimpleStringProperty();
    private final StringProperty soLanDat = new SimpleStringProperty();

    private final int customerId;

    public CustomerDetailViewModel(CustomerManagement_CustomerDisplay dto)
    {
        this.customerId = dto.getCustomerId();
        hoTen.set(dto.getCustomerName());
        loaiKhach.set(dto.getCustomerType());
        cccd.set(dto.getIdentityNumber());
        diaChi.set(dto.getAddress());
        sdt.set(dto.getPhoneNumber());
        trangThai.set(dto.getStatus());
        gioiTinh.set(dto.getGender());

        if (dto.getBirthday() != null)
        {
            LocalDate birthDate = Instant.ofEpochMilli(dto.getBirthday().toEpochMilli()).atZone(ZoneId.systemDefault()).toLocalDate();
            ngaySinh.set(birthDate);

            int age = Period.between(birthDate, LocalDate.now()).getYears();
            tuoi.set(String.valueOf(age));
        }
        else
        {
            tuoi.set("Không rõ");
        }
        ReservationGuestDAO dao = new ReservationGuestDAO();
        int count = dao.countReservationsByCustomerId(customerId);
        soLanDat.set(String.valueOf(count));
    }


    // Getters cho binding
    public StringProperty hoTenProperty() { return hoTen; }
    public ObjectProperty<LocalDate> ngaySinhProperty() { return ngaySinh; }
    public StringProperty loaiKhachProperty() { return loaiKhach; }
    public StringProperty tuoiProperty() { return tuoi; }
    public BooleanProperty gioiTinhProperty() { return gioiTinh; }
    public StringProperty cccdProperty() { return cccd; }
    public StringProperty diaChiProperty() { return diaChi; }
    public StringProperty sdtProperty() { return sdt; }
    public StringProperty trangThaiProperty() { return trangThai; }
    public StringProperty soLanDatProperty() { return soLanDat; }

    public void reload() {
        Customer customer = customerDAO.findById(customerId);
        if (customer != null) {
            hoTen.set(customer.getFullName());
            if (customer.getDateOfBirth() != null) {
                LocalDate birthDate = customer.getDateOfBirth().atZone(ZoneId.systemDefault()).toLocalDate();
                ngaySinh.set(birthDate);
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                tuoi.set(String.valueOf(age));
            } else {
                ngaySinh.set(null);
                tuoi.set("Không rõ");
            }
            loaiKhach.set(customer.getCustomerTypeID().getTypeName());
            gioiTinh.set(customer.getGender());
            cccd.set(customer.getIdentityNumber());
            diaChi.set(customer.getCustomerAddress());
            sdt.set(customer.getPhoneNumber());
        }
    }

    public boolean save() {
        try {
            Customer customer = customerDAO.findById(customerId);
            if (customer == null) {
                customer = new Customer();
                customer.setId(customerId);
            }
            customer.setFullName(hoTen.get());
            if (ngaySinh.get() != null) {
                Instant instant = ngaySinh.get().atStartOfDay(ZoneId.systemDefault()).toInstant();
                customer.setDateOfBirth(instant);
            } else {
                customer.setDateOfBirth(null);
            }

            Customertype customerType = customerTypeDAO.findByName(loaiKhach.get());
            customer.setCustomerTypeID(customerType);

            customer.setGender(gioiTinh.get());
            customer.setIdentityNumber(cccd.get());
            customer.setCustomerAddress(diaChi.get());
            customer.setPhoneNumber(sdt.get());
            return customerDAO.saveOrUpdate(customer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
