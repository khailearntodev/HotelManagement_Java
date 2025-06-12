package com.example.hotelmanagement.ViewModels;

import com.example.hotelmanagement.DAO.CustomerDAO;
import com.example.hotelmanagement.DAO.CustomerTypeDAO;
import com.example.hotelmanagement.DTO.CustomerManagement_CustomerDisplay;
import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Customertype;
import com.itextpdf.layout.properties.TextAlignment;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import java.io.File;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.time.LocalDateTime;


public class CustomerManagementViewModel {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CustomerTypeDAO customerTypeDAO = new CustomerTypeDAO();

    @Getter
    private final ObservableList<CustomerManagement_CustomerDisplay> customerDisplays = FXCollections.observableArrayList();

    @Getter
    private final ObservableList<String> customerTypesNames = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<String> statusList = FXCollections.observableArrayList("Tất cả", "Đang thuê", "Từng thuê");

    private final ObservableList<String> monthList = FXCollections.observableArrayList();
    private final ObservableList<String> yearList = FXCollections.observableArrayList();

    private final StringProperty keyword = new SimpleStringProperty("");
    private final StringProperty selectedStatus = new SimpleStringProperty("Tất cả");
    private final StringProperty selectedTypeName = new SimpleStringProperty("Loại khách");
    private final StringProperty selectedMonth = new SimpleStringProperty("Tháng");
    private final StringProperty selectedYear = new SimpleStringProperty("Năm");

    private final StringProperty realTime = new SimpleStringProperty();
    private final String ALL_TYPE_NAME = "Loại khách";

    public CustomerManagementViewModel() {
        loadCustomerTypes();
        loadMonthYearOptions();
        loadCustomers();
        initClock();
    }

    public ObservableList<String> getMonthList() {
        return monthList;
    }

    public ObservableList<String> getYearList() {
        return yearList;
    }

    public void setSelectedMonth(String month) {
        selectedMonth.set(month);
    }

    public void setSelectedYear(String year) {
        selectedYear.set(year);
    }


    public StringProperty keywordProperty() {
        return keyword;
    }

    public StringProperty selectedStatusProperty() {
        return selectedStatus;
    }

    public StringProperty selectedTypeNameProperty() {
        return selectedTypeName;
    }

    public void setSelectedTypeName(String value) {
        selectedTypeName.set(value);
    }

    public StringProperty selectedMonthProperty() {
        return selectedMonth;
    }

    public StringProperty selectedYearProperty() {
        return selectedYear;
    }

    public StringProperty realTimeProperty() {
        return realTime;
    }

    public String getSelectedMonth() {
        return selectedMonth.get();
    }

    public String getSelectedYear() {
        return selectedYear.get();
    }

    public String getSelectedTypeName() {
        return selectedTypeName.get();
    }

    public String getSelectedStatus() {
        return selectedStatus.get();
    }

    public String getKeyword() {
        return keyword.get();
    }


    private void loadMonthYearOptions() {
        monthList.clear();
        monthList.add("Tháng");
        for (int i = 1; i <= 12; i++) {
            monthList.add(String.valueOf(i));
        }

        int currentYear = LocalDate.now().getYear();
        yearList.clear();
        yearList.add("Năm");
        for (int y = currentYear - 10; y <= currentYear + 2; y++) {
            yearList.add(String.valueOf(y));
        }

        selectedMonth.set("Tháng");
        selectedYear.set("Năm");
    }

    public void loadCustomerTypes() {
        List<Customertype> types = customerTypeDAO.getAll();
        customerTypesNames.clear();
        customerTypesNames.add(ALL_TYPE_NAME);
        if (types != null) {
            for (Customertype type : types) {
                customerTypesNames.add(type.getTypeName());
            }
        }
        selectedTypeName.set(ALL_TYPE_NAME);
    }

    public void loadCustomers() {
        List<Customer> rawCustomers = customerDAO.getAllCustomers();
        List<CustomerManagement_CustomerDisplay> displayList = rawCustomers.stream()
                .map(this::mapToCustomerDisplay)
                .collect(Collectors.toList());
        customerDisplays.setAll(displayList);
    }

    private CustomerManagement_CustomerDisplay mapToCustomerDisplay(Customer c) {
        String status = "Từng thuê";

        if (c.getReservationguests() != null) {
            boolean isRenting = c.getReservationguests().stream()
                    .filter(rg -> !rg.getIsDeleted())
                    .anyMatch(rg -> {
                        var res = rg.getReservationID();
                        return res != null && res.getCheckInDate() != null && res.getCheckOutDate() != null
                                && Instant.now().isAfter(res.getCheckInDate())
                                && Instant.now().isBefore(res.getCheckOutDate());
                    });

            if (isRenting) status = "Đang thuê";
        }

        return new CustomerManagement_CustomerDisplay(
                c.getId(),
                c.getFullName(),
                c.getCustomerTypeID() != null ? c.getCustomerTypeID().getTypeName() : "",
                c.getDateOfBirth(),
                c.getIdentityNumber(),
                c.getPhoneNumber(),
                status,
                c.getCustomerAddress(),
                c.getGender()
        );
    }

    public void filterCustomers() {
        System.out.println("Filtering: Month = " + selectedMonth.get() + ", Year = " + selectedYear.get());

        List<CustomerManagement_CustomerDisplay> filtered = customerDAO.getAllCustomers().stream()
                .filter(c -> {
                    // Keyword filter
                    boolean matchKeyword = keyword.get().isEmpty() ||
                            c.getFullName().toLowerCase().contains(keyword.get().toLowerCase()) ||
                            (c.getIdentityNumber() != null && c.getIdentityNumber().contains(keyword.get()));

                    // Type filter
                    boolean matchType = selectedTypeName.get().equals(ALL_TYPE_NAME) ||
                            (c.getCustomerTypeID() != null && c.getCustomerTypeID().getTypeName().equals(selectedTypeName.get()));

                    // Status filter
                    boolean matchStatus = selectedStatus.get().equals("Tất cả") ||
                            mapToCustomerDisplay(c).getStatus().equals(selectedStatus.get());

                    // Date filter
                    boolean matchDate = true;

                    boolean monthSelected = !selectedMonth.get().equals("Tháng");
                    boolean yearSelected = !selectedYear.get().equals("Năm");

                    if (monthSelected || yearSelected) {
                        int month = monthSelected ? Integer.parseInt(selectedMonth.get()) : -1;
                        int year = yearSelected ? Integer.parseInt(selectedYear.get()) : -1;

                        if (c.getReservationguests() != null && !c.getReservationguests().isEmpty()) {
                            var latestDate = c.getReservationguests().stream()
                                    .filter(rg -> !rg.getIsDeleted())
                                    .map(rg -> rg.getReservationID().getCheckInDate())
                                    .filter(Objects::nonNull)
                                    .max(Instant::compareTo)
                                    .orElse(null);

                            if (latestDate != null) {
                                LocalDate localDate = latestDate.atZone(ZoneId.systemDefault()).toLocalDate();

                                boolean matchMonth = (month == -1) || (localDate.getMonthValue() == month);
                                boolean matchYear = (year == -1) || (localDate.getYear() == year);

                                matchDate = matchMonth && matchYear;
                            } else {
                                matchDate = false;
                            }
                        } else {
                            matchDate = false;
                        }
                    }

                    return matchKeyword && matchType && matchStatus && matchDate;
                })
                .map(this::mapToCustomerDisplay)
                .collect(Collectors.toList());

        customerDisplays.setAll(filtered);
    }


    private void initClock() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    LocalTime now = LocalTime.now();
                    realTime.set(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                });
            }
        }, 0, 1000);
    }

    public void exportCustomersToPdf(Window parentWindow) {
        List<CustomerManagement_CustomerDisplay> customers = customerDisplays;
        if (customers == null || customers.isEmpty()) {
            System.out.println("Không có khách hàng để xuất.");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            fileChooser.setInitialFileName("DanhSachKhachHang_" + timeStamp + ".pdf");

            File file = fileChooser.showSaveDialog(parentWindow);
            if (file == null) {
                return;
            }

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

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

            document.add(new Paragraph("DANH SÁCH KHÁCH HÀNG")
                    .setFont(boldFont)
                    .setFontSize(20)
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            document.add(new Paragraph("Ngày xuất: " + currentDate)
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(10));

            for (CustomerManagement_CustomerDisplay customer : customers) {
                Div customerBlock = new Div()
                        .setBackgroundColor(new DeviceRgb(255, 250, 205))
                        .setPadding(10)
                        .setMarginBottom(10);

                customerBlock.add(new Paragraph("Mã KH: " + customer.getCustomerId() + " | Họ tên: " + customer.getCustomerName())
                        .setFont(boldFont)
                        .setFontSize(12));
                customerBlock.add(new Paragraph("Loại: " + customer.getCustomerType() + " | Số giấy tờ: " + customer.getIdentityNumber())
                        .setFont(vietnameseFont)
                        .setFontSize(11));
                customerBlock.add(new Paragraph("Ngày sinh: " + customer.getFormattedBirthday() + " | Trạng thái: " + customer.getStatus())

                        .setFont(vietnameseFont)
                        .setFontSize(11));
                customerBlock.add(new Paragraph("CCCD / Hộ chiếu: " + customer.getIdentityNumber() + " | SDT: " + customer.getPhoneNumber())
                        .setFont(vietnameseFont)
                        .setFontSize(11));
                customerBlock.add(new Paragraph("Địa chỉ: " + (customer.getAddress() == null ? "" : customer.getAddress()))
                        .setFont(vietnameseFont)
                        .setFontSize(11));

                document.add(customerBlock);
            }

            document.close();

            System.out.println("Đã xuất danh sách khách hàng ra PDF tại: " + file.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Lỗi khi xuất PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
