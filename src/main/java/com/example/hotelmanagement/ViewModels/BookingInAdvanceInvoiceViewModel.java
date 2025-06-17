package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.EmployeeDAO;
import com.example.hotelmanagement.DAO.InvoiceDAO;
import com.example.hotelmanagement.DAO.PrebookingDAO;
import com.example.hotelmanagement.DAO.UserAccountDAO;
import com.example.hotelmanagement.DTO.PreBookingInvoiceDisplay;
import com.example.hotelmanagement.Models.*;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.scene.control.Alert;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
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
        Employee e = new EmployeeDAO().findById(LoginViewModel.employeeId);
        Invoice invoice = new Invoice();
        invoice.setIssueDate(preBookingInvoiceDisplay.getInvoiceDate().atZone(ZoneOffset.UTC).toInstant());
        invoice.setInvoiceType(1);
        invoice.setTotalAmount(preBookingInvoiceDisplay.getTotalPrice());
        invoice.setCustomerName(preBookingInvoiceDisplay.getName());
        invoice.setCustomerAddress(preBookingInvoiceDisplay.getAddress());
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
        p.setCheckInDate(preBookingInvoiceDisplay.getStartDate().atStartOfDay(ZoneOffset.UTC).toInstant());
        p.setCheckOutDate(preBookingInvoiceDisplay.getEndDate().atStartOfDay(ZoneOffset.UTC).toInstant());
        p.setBookingDate(preBookingInvoiceDisplay.getInvoiceDate().atZone(ZoneOffset.UTC).toInstant());
        p.setPrice(preBookingInvoiceDisplay.getTotalPrice());
        p.setIsDeleted(false);
        PrebookingDAO pDao = new PrebookingDAO();
        pDao.save(p);

        parent.getRoomReservationDisplays().removeIf(r -> r.getId() == room.getId());
        parent.setSelectedItem(null);

        printInvoiceWithPdfPreview();
    }
        public void printInvoiceWithPdfPreview() {
            Employee e = new EmployeeDAO().findById(LoginViewModel.employeeId);
            String employeeName = e.getFullName();
            try {
                File tempFile = File.createTempFile("invoice_", ".pdf");
                String tempPdfPath = tempFile.getAbsolutePath();

                PdfWriter writer = new PdfWriter(tempPdfPath);
                PdfDocument pdf = new PdfDocument(writer);
                Document doc = new Document(pdf);

                PdfFont vietnameseFont = PdfFontFactory.createFont(
                        "c:/windows/fonts/arial.ttf",
                        PdfEncodings.IDENTITY_H,
                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
                );

                PdfFont boldFont = PdfFontFactory.createFont(
                        "c:/windows/fonts/arialbd.ttf",
                        PdfEncodings.IDENTITY_H,
                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
                );

                doc.add(new Paragraph("KHÁCH SẠN N10")
                        .setFont(boldFont)
                        .setFontSize(16)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(5));
                doc.add(new Paragraph("Địa chỉ: 123 Đường XYZ, Quận 1, TP.HCM")
                        .setFont(vietnameseFont)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(5));
                doc.add(new Paragraph("Điện thoại: 0123.456.789 | Email: info@khachsanabc.com")
                        .setFont(vietnameseFont)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20));

                doc.add(new Paragraph("HÓA ĐƠN THANH TOÁN ĐẶT TRƯỚC")
                        .setFont(boldFont)
                        .setFontSize(24)
                        .setFontColor(new DeviceRgb(0, 79, 157))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                doc.add(new Paragraph("Thời gian in hóa đơn: " + LocalDateTime.now().format(formatter))
                        .setFont(vietnameseFont)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMarginBottom(10));

                Paragraph bookingCode = new Paragraph()
                        .add(new Text("Mã đặt phòng: ").setFont(vietnameseFont)).setFontSize(12)
                        .add(new Text(preBookingInvoiceDisplay.getCode()).setFont(boldFont)).setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(10);
                doc.add(bookingCode);

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                doc.add(new Paragraph("THÔNG TIN ĐẶT PHÒNG:").setFont(boldFont));
                doc.add(new Paragraph("Phòng số: " + preBookingInvoiceDisplay.getRoomNumber()).setFont(vietnameseFont).setMarginLeft(20));
                doc.add(new Paragraph("Loại phòng: " + preBookingInvoiceDisplay.getRoomTypeName()).setFont(vietnameseFont).setMarginLeft(20));
                doc.add(new Paragraph("Ngày nhận: " + preBookingInvoiceDisplay.getStartDate().format(dateFormatter))
                        .setFont(vietnameseFont).setMarginLeft(20));
                doc.add(new Paragraph("Ngày trả: " + preBookingInvoiceDisplay.getEndDate().format(dateFormatter))
                        .setFont(vietnameseFont).setMarginLeft(20));

                long soNgay = java.time.temporal.ChronoUnit.DAYS.between(preBookingInvoiceDisplay.getStartDate(), preBookingInvoiceDisplay.getEndDate()) + 1;
                doc.add(new Paragraph("Số ngày: " + soNgay + " ngày")
                        .setFont(vietnameseFont).setMarginLeft(20).setMarginBottom(10));

                Div infoBox = new Div()
                        .setBackgroundColor(new DeviceRgb(240, 240, 240))
                        .setPadding(10)
                        .setMarginBottom(10);

                Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1.5f, 1f}))
                        .useAllAvailableWidth();

                Div leftColumn = new Div();
                leftColumn.add(new Paragraph("Nhân viên thanh toán: " + employeeName).setFont(boldFont).setFontSize(12));
                leftColumn.add(new Paragraph("Hóa đơn từ:").setFont(boldFont).setFontSize(12).setMarginTop(5));
                leftColumn.add(new Paragraph("Khách sạn N10").setFont(vietnameseFont).setMarginLeft(10));
                leftColumn.add(new Paragraph("123 Đường XYZ, Quận 1, TP.HCM").setFont(vietnameseFont).setMarginLeft(10));

                Div rightColumn = new Div();

                rightColumn.add(new Paragraph("Ngày lập: " + LocalDateTime.now().format(dateFormatter))
                        .setFont(boldFont).setFontSize(12));
                rightColumn.add(new Paragraph("Đến:").setFont(boldFont).setFontSize(12).setMarginTop(5));
                rightColumn.add(new Paragraph(preBookingInvoiceDisplay.getName()).setFont(vietnameseFont).setMarginLeft(10));
                rightColumn.add(new Paragraph(preBookingInvoiceDisplay.getAddress()).setFont(vietnameseFont).setMarginLeft(10));

                infoTable.addCell(new Cell().add(leftColumn).setBorder(null));
                infoTable.addCell(new Cell().add(rightColumn).setBorder(null));

                infoBox.add(infoTable);
                doc.add(infoBox);

                NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                Paragraph summary = new Paragraph()
                        .add("Tổng tiền hóa đơn: ")
                        .add(new Text(currencyFormat.format(preBookingInvoiceDisplay.getTotalPrice()) + " VNĐ")
                                .setFont(boldFont)
                                .setFontColor(ColorConstants.RED)
                                .setFontSize(14));
                summary.setTextAlignment(TextAlignment.RIGHT)
                        .setFont(vietnameseFont);
                doc.add(summary);

                doc.close();

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(tempFile);
                }

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            tempFile.delete();
                        } catch (Exception e) {

                        }
                    }
                }, 30000);

            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }
}
