package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.EmployeeDAO;
import com.example.hotelmanagement.DAO.InvoiceDAO;
import com.example.hotelmanagement.DAO.PrebookingDAO;
import com.example.hotelmanagement.DTO.PreBookingInvoiceDisplay;
import com.example.hotelmanagement.Models.Employee;
import com.example.hotelmanagement.Models.Invoice;
import com.example.hotelmanagement.Models.Prebooking;
import com.example.hotelmanagement.Models.Room;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class BookingInAdvanceInvoiceViewModel {
    private Room room;
    @Setter
    @Getter
    private BookingInAdvanceViewModel parent;
    @Getter
    PreBookingInvoiceDisplay preBookingInvoiceDisplay = new PreBookingInvoiceDisplay();
    public BookingInAdvanceInvoiceViewModel(Room room, LocalDate startDate, LocalDate endDate, String customerName, String customerAddress, String customerPhone) {
        preBookingInvoiceDisplay.setRoomNumber(room.getRoomNumber());
        preBookingInvoiceDisplay.setRoomTypeName(room.getRoomTypeID().getTypeName());
        preBookingInvoiceDisplay.setInvoiceDate(LocalDateTime.now());
        preBookingInvoiceDisplay.setPrice(room.getRoomTypeID().getBasePrice());
        preBookingInvoiceDisplay.setStartDate(startDate);
        preBookingInvoiceDisplay.setEndDate(endDate);
        preBookingInvoiceDisplay.setTotalPrice(room.getRoomTypeID().getBasePrice().multiply(BigDecimal.valueOf(Math.abs(ChronoUnit.DAYS.between(endDate, startDate)) + 1)));
        preBookingInvoiceDisplay.setName(customerName);
        preBookingInvoiceDisplay.setAddress(customerAddress);
        preBookingInvoiceDisplay.setPhoneNumber(customerPhone);
        String uuid = UUID.randomUUID().toString();
        String shortId = uuid.substring(0, 8).toUpperCase();
        preBookingInvoiceDisplay.setCode(shortId);

        this.room = room;
    }

    public void saveInvoice() {
        //
        Employee e = new EmployeeDAO().findById(2);
        //

        Invoice invoice = new Invoice();
        invoice.setIssueDate(preBookingInvoiceDisplay.getInvoiceDate().atZone(ZoneId.systemDefault()).toInstant());
        invoice.setInvoiceType(1);
        invoice.setTotalAmount(preBookingInvoiceDisplay.getTotalPrice());
        invoice.setCustomerName(preBookingInvoiceDisplay.getName());
        invoice.setCustomerAddres(preBookingInvoiceDisplay.getAddress());
        invoice.setEmployeeID(e);
        invoice.setPaymentStatus("Đã thanh toán");
        invoice.setIsDeleted(false);
        InvoiceDAO dao = new InvoiceDAO();
        dao.save(invoice);

        Prebooking p = new Prebooking();
        p.setBookingCode(preBookingInvoiceDisplay.getCode());

        p.setCustomerName(preBookingInvoiceDisplay.getName());
        p.setInvoiceID(invoice);
        p.setReservationID(null);
        p.setRoomID(room);
        p.setPhoneNumber(preBookingInvoiceDisplay.getPhoneNumber());
        p.setCheckInDate(preBookingInvoiceDisplay.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        p.setCheckOutDate(preBookingInvoiceDisplay.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        p.setBookingDate(preBookingInvoiceDisplay.getInvoiceDate().atZone(ZoneId.systemDefault()).toInstant());
        p.setPrice(preBookingInvoiceDisplay.getTotalPrice());
        p.setIsDeleted(false);
        PrebookingDAO pDao = new PrebookingDAO();
        pDao.save(p);

        parent.getRoomReservationDisplays().removeIf(r -> r.getId() == room.getId());
        parent.setSelectedItem(null);
    }
}
