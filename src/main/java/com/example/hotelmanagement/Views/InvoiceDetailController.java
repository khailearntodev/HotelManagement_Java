package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DAO.CustomerDAO;
import com.example.hotelmanagement.DAO.InvoiceDAO;
import com.example.hotelmanagement.Main;
import com.example.hotelmanagement.Models.*;
import com.example.hotelmanagement.ViewModels.InvoiceDetailViewModel;
import com.example.hotelmanagement.ViewModels.InvoiceViewModel;
import com.example.hotelmanagement.ViewModels.LoginViewModel;
import com.example.hotelmanagement.ViewModels.SelectRoomForCheckOutViewModel;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import com.example.hotelmanagement.DTO.InvoiceDetail;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.itextpdf.layout.element.Cell;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;


public class InvoiceDetailController {

    @FXML private AnchorPane rootPane;
    @FXML private Label invoiceNoLabel;
    @FXML private Label paymentDateLabel;
    @FXML private Label employeeNameLabel;
    @FXML private TextField customerNameField;
    @FXML private TextField customerAddressField;
    @FXML private TableView<InvoiceDetailViewModel> detailTable;
    @FXML private TableColumn<InvoiceDetailViewModel, Integer> soThuTuColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, Integer> soPhongColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, Integer> soNgayThueColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, BigDecimal> donGiaPhongColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, BigDecimal> tienPhongColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, BigDecimal> phiDichVuColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, BigDecimal> tiencocColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, BigDecimal> tongColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, Void> viewServicesColumn;
    @FXML private Label totalDueLabel;
    @FXML private MFXButton closeButton;
    @FXML private MFXButton payButton;
    @FXML private MFXButton payAndPrintButton;
    @FXML private ImageView qrCodeImageView;

    private Invoice invoice;
    private InvoiceDAO dao = new InvoiceDAO();
    private ObservableList<InvoiceDetailViewModel> detailList = FXCollections.observableArrayList();
    private InvoiceDetailViewModel invoiceDetailViewModelForCreation;

    @FXML
    public void initialize() {
        URL cssUrl = getClass().getResource("/CSS/invoicedetail.css");
        if (cssUrl != null) {
            rootPane.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("CSS file not found: /CSS/style.css");
        }
        soThuTuColumn.setCellValueFactory(cellData -> cellData.getValue().soThuTuProperty().asObject());
        soPhongColumn.setCellValueFactory(cellData -> cellData.getValue().soPhongProperty().asObject());
        soNgayThueColumn.setCellValueFactory(cellData -> cellData.getValue().soNgayThueProperty().asObject());
        donGiaPhongColumn.setCellValueFactory(cellData -> cellData.getValue().donGiaPhongProperty());
        tienPhongColumn.setCellValueFactory(cellData -> cellData.getValue().tienPhongProperty());
        phiDichVuColumn.setCellValueFactory(cellData -> cellData.getValue().phiDichVuProperty());
        tiencocColumn.setCellValueFactory(cellData -> cellData.getValue().tienCocProperty());
        tongColumn.setCellValueFactory(cellData -> cellData.getValue().tongCongProperty());

        formatCurrencyColumn(donGiaPhongColumn);
        formatCurrencyColumn(tienPhongColumn);
        formatCurrencyColumn(phiDichVuColumn);
        formatCurrencyColumn(tiencocColumn);
        formatCurrencyColumn(tongColumn);

        detailTable.setItems(detailList);

        viewServicesColumn.setCellFactory(param -> new TableCell<>() {
            private final MFXButton viewButton = new MFXButton("Xem");

            {
                viewButton.setOnAction(event -> {
                    InvoiceDetailViewModel detail = getTableView().getItems().get(getIndex());
                    List<Servicebooking> bookings = detail.getReservation().getServicebookings().stream().toList();
                    openServiceDetail(bookings);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });

        closeButton.setOnAction(event -> closeWindow());
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        this.invoiceDetailViewModelForCreation = null;

        if (invoice != null) {
            invoiceNoLabel.setText(invoice.getId() != null ? "HD-" + invoice.getId() : "N/A");
            paymentDateLabel.setText(formatInstantToDateTime(invoice.getIssueDate()));
            employeeNameLabel.setText(invoice.getEmployeeID() != null ? invoice.getEmployeeID().getFullName() : "N/A");

            customerNameField.setText(invoice.getCustomerName());
            customerAddressField.setText(invoice.getCustomerAddress());

            totalDueLabel.setText(formatCurrency(invoice.getTotalAmount()));

            loadInvoiceDetails();
            updateButtonStates();
            generatePaymentQRCode();
        }
    }

    public void setViewModelForCreation(InvoiceDetailViewModel viewModel) {
        this.invoiceDetailViewModelForCreation = viewModel;
        this.invoice = viewModel.getInvoice().get();

        invoiceNoLabel.setText("Chưa lưu");
        customerNameField.setText(invoice.getCustomerName() != null ? invoice.getCustomerName() : "");
        customerAddressField.setText(invoice.getCustomerAddress() != null ? invoice.getCustomerAddress() : "");

        employeeNameLabel.setText(invoice.getEmployeeID() != null ? invoice.getEmployeeID().getFullName() : "N/A");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        paymentDateLabel.setText(LocalDateTime.now().format(formatter));

        totalDueLabel.setText(formatCurrency(viewModel.getTongTien().get()));
        detailList.setAll(viewModel.getReservationDetails());
        detailTable.setItems(detailList);

        payButton.setVisible(true);
        payButton.setText("Thanh Toán");
        payButton.setOnAction(event -> handlePayInvoice());

        payAndPrintButton.setVisible(true);
        payAndPrintButton.setText("Thanh Toán và In Hóa Đơn");
        payAndPrintButton.setOnAction(event -> handlePayAndPrintInvoice());

        customerNameField.setEditable(true);
        customerAddressField.setEditable(true);
        generatePaymentQRCode();
    }


    private void updateButtonStates() {
        if (invoice == null) return;

        if ("Đã thanh toán".equals(invoice.getPaymentStatus())) {
            payButton.setDisable(true);
            payAndPrintButton.setText("In Hóa Đơn");
            payAndPrintButton.setOnAction(event -> handlePrintInvoice());
            customerNameField.setEditable(false);
            customerAddressField.setEditable(false);
        } else {
            payButton.setVisible(true);
            payButton.setText("Thanh Toán");
            payButton.setOnAction(event -> handlePayInvoice());
            payAndPrintButton.setVisible(true);
            payAndPrintButton.setText("Thanh Toán và In Hóa Đơn");
            payAndPrintButton.setOnAction(event -> handlePayAndPrintInvoice());
            customerNameField.setEditable(true);
            customerAddressField.setEditable(true);
        }
    }

    private boolean validateAndUpdateInvoiceCustomerInfo() {
        String customerName = customerNameField.getText();
        String customerAddress = customerAddressField.getText();

        if (customerName == null || customerName.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Tên khách hàng không được để trống.");
            return false;
        }
        if (customerAddress == null || customerAddress.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Địa chỉ khách hàng không được để trống.");
            return false;
        }

        if (this.invoice != null) {
            this.invoice.setCustomerName(customerName.trim());
            this.invoice.setCustomerAddress(customerAddress.trim());
        }
        return true;
    }

    @FXML
    private void handlePayInvoice() {
        if (!validateAndUpdateInvoiceCustomerInfo()) {
            return;
        }

        try {
            if (this.invoice != null) {
                this.invoice.setPaymentStatus("Đã thanh toán");

                boolean saveSuccessful = false;
                if (this.invoice.getId() == null && this.invoiceDetailViewModelForCreation != null) {
                    this.invoiceDetailViewModelForCreation.saveInvoice();
                    this.invoice = this.invoiceDetailViewModelForCreation.getInvoice().get();
                    invoiceNoLabel.setText("HD-" + invoice.getId());
                    saveSuccessful = true;
                } else {

                    saveSuccessful = dao.update(this.invoice);
                }

                if (saveSuccessful) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Hóa đơn đã được thanh toán.");
                    updateButtonStates();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu hoặc cập nhật hóa đơn vào cơ sở dữ liệu.");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thanh toán hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePayAndPrintInvoice() {
        if (!validateAndUpdateInvoiceCustomerInfo()) {
            return;
        }

        try {
            if (this.invoice != null) {
                this.invoice.setPaymentStatus("Đã thanh toán");

                boolean saveSuccessful = false;
                if (this.invoice.getId() == null && this.invoiceDetailViewModelForCreation != null) {
                    // Đây là hóa đơn mới
                    this.invoiceDetailViewModelForCreation.saveInvoice();
                    this.invoice = this.invoiceDetailViewModelForCreation.getInvoice().get();
                    invoiceNoLabel.setText("HD-" + invoice.getId());
                    saveSuccessful = true;
                } else {
                    saveSuccessful = dao.update(this.invoice);
                }

                if (saveSuccessful) {
                    exportInvoiceToPdf(this.invoice, detailList, rootPane.getScene().getWindow());
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Hóa đơn đã được thanh toán.");
                    updateButtonStates();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu hoặc cập nhật hóa đơn vào cơ sở dữ liệu.");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thanh toán và in hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrintInvoice() {
        if (this.invoice != null) {
            exportInvoiceToPdf(this.invoice, detailList, rootPane.getScene().getWindow());
            showAlert(Alert.AlertType.INFORMATION, "In Hóa Đơn", "Đang tiến hành in hóa đơn số " + invoice.getId());
        }
    }

    private String formatInstantToDateTime(Instant instant) {
        if (instant == null) {
            return "N/A";
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private void closeWindow() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void loadInvoiceDetails() {
        if (invoice != null && invoice.getReservations() != null) {
            List<InvoiceDetailViewModel> viewModels = invoice.getReservations().stream()
                    .map(InvoiceDetailViewModel::new)
                    .toList();

            detailList.setAll(viewModels);
        }
    }

    @FXML
    public void openServiceDetail(List<Servicebooking> bookings) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Views/ServiceDetailView.fxml"));
            Parent root = loader.load();

            ServiceDetailController controller = loader.getController();

            Room room = null;
            Customer customer = null;
            if (bookings != null && !bookings.isEmpty()) {
                Servicebooking firstBooking = bookings.get(0);
                if (firstBooking.getReservationID().getRoomID() != null) {
                    room = firstBooking.getReservationID().getRoomID();
                }
                if (firstBooking.getReservationID().getReservationguests() != null && !firstBooking.getReservationID().getReservationguests().isEmpty()) {
                    int customerId = firstBooking.getReservationID().getReservationguests().stream()
                            .findFirst().get().getCustomerID().getId();

                    CustomerDAO customerDAO = new CustomerDAO();
                    customer = customerDAO.findById(customerId);
                }
            }
            controller.setServiceDetails(room, customer, bookings);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết dịch vụ");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void exportInvoiceToPdf(Invoice invoiceToPrint, List<InvoiceDetailViewModel> detailsToPrint, Window parentWindow) {
        if (invoiceToPrint == null || detailsToPrint == null || detailsToPrint.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Không có dữ liệu hóa đơn để xuất PDF.");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "HoaDon_" + (invoiceToPrint.getId() != null ? invoiceToPrint.getId() : "Moi") + "_" + timeStamp + ".pdf";
            fileChooser.setInitialFileName(fileName);

            File file = fileChooser.showSaveDialog(parentWindow);
            if (file == null) {
                return;
            }

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4); // Use A4 page size
            document.setMargins(50, 50, 50, 50); // Top, Right, Bottom, Left margins

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

            // --- HEADER THÔNG TIN CÔNG TY (Placeholder) ---
            document.add(new Paragraph("KHÁCH SẠN N10")
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));
            document.add(new Paragraph("Địa chỉ: 123 Đường XYZ, Quận 1, TP.HCM")
                    .setFont(vietnameseFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));
            document.add(new Paragraph("Điện thoại: 0123.456.789 | Email: info@khachsanabc.com")
                    .setFont(vietnameseFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // --- TIÊU ĐỀ HÓA ĐƠN ---
            document.add(new Paragraph("HÓA ĐƠN THANH TOÁN")
                    .setFont(boldFont)
                    .setFontSize(24)
                    .setFontColor(new DeviceRgb(0, 79, 157)) // Màu xanh đậm
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // --- THÔNG TIN CHUNG HÓA ĐƠN ---
            document.add(new Paragraph("Số hóa đơn: " + (invoiceToPrint.getId() != null ? "HD-" + invoiceToPrint.getId() : "Chưa lưu"))
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setMarginBottom(5));
            document.add(new Paragraph("Ngày thanh toán: " + formatInstantToDateTime(invoiceToPrint.getIssueDate()))
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setMarginBottom(5));
            document.add(new Paragraph("Nhân viên lập: " + (invoiceToPrint.getEmployeeID() != null ? invoiceToPrint.getEmployeeID().getFullName() : "N/A"))
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setMarginBottom(5));
            document.add(new Paragraph("Tên khách hàng: " + (invoiceToPrint.getCustomerName() != null ? invoiceToPrint.getCustomerName() : "N/A"))
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setMarginBottom(5));
            document.add(new Paragraph("Địa chỉ khách hàng: " + (invoiceToPrint.getCustomerAddress() != null ? invoiceToPrint.getCustomerAddress() : "N/A"))
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setMarginBottom(20));

            // --- BẢNG CHI TIẾT DỊCH VỤ/PHÒNG ---
            float[] columnWidths = {30f, 60f, 60f, 80f, 80f, 80f, 80f, 80f};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginBottom(20);

            // Table Headers
            String[] headers = {"STT", "Số Phòng", "Ngày Thuê", "Đơn Giá Phòng", "Tiền Phòng", "Phí Dịch Vụ", "Tiền Cọc", "Tổng"};
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header))
                        .setFont(boldFont)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBackgroundColor(new DeviceRgb(220, 230, 241))
                        .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f))
                        .setPadding(8));
            }

            // Table Rows
            int stt = 1;
            for (InvoiceDetailViewModel detail : detailsToPrint) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(stt++)))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.getSoPhong().get())))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.getSoNgayThue().get())))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(formatCurrency(detail.getDonGiaPhong().get())))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(formatCurrency(detail.getTienPhong().get())))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(formatCurrency(detail.getPhiDichVu().get())))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(formatCurrency(detail.getTienCoc().get())))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(formatCurrency(detail.getTongCong().get())))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
            }
            document.add(table);

            // --- TỔNG KẾT ---
            document.add(new Paragraph("Tổng số tiền phải thanh toán: " + formatCurrency(invoiceToPrint.getTotalAmount()))
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(30));


            document.close();
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xuất hóa đơn PDF thành công tại: " + file.getAbsolutePath());

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xuất PDF: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi không mong muốn khi xuất PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void formatCurrencyColumn(TableColumn<InvoiceDetailViewModel, BigDecimal> column) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        column.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(item));
                }
            }
        });
    }
    private String formatCurrency(BigDecimal amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormatter.format(amount);
    }
    private void generatePaymentQRCode() {
        if (invoice == null || invoice.getTotalAmount() == null) {
            qrCodeImageView.setImage(null);
            return;
        }

        String amount = totalDueLabel.getText()
                .replace("₫", "")
                .replace(".", "")
                .replace(",", "")
                .replaceAll("\\D", "");
        System.out.println(amount);
        try {
            String qrData = createVietQR(
                    "970422",
                    "56500824100585",
                    "TRUONG DUC HUY",
                    Long.parseLong(amount),
                    "Thanh toan hoa don khach san"
            );

            BufferedImage qrImageBuffer = generateQRCodeImage(qrData, 200, 200);
            Image fxImage = SwingFXUtils.toFXImage(qrImageBuffer, null);
            qrCodeImageView.setImage(fxImage);

        } catch (WriterException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi QR Code", "Không thể tạo mã QR: " + e.getMessage());
            e.printStackTrace();
            qrCodeImageView.setImage(null);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi định dạng số tiền", "Không thể chuyển đổi số tiền để tạo mã QR: " + e.getMessage());
            e.printStackTrace();
            qrCodeImageView.setImage(null);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi QR VietQR", "Đã xảy ra lỗi khi tạo mã QR VietQR: " + e.getMessage());
            e.printStackTrace();
            qrCodeImageView.setImage(null);
        }
    }
    private String buildTLV(String tag, String value) {
        return tag + String.format("%02d", value.length()) + value;
    }

    private String createVietQR(String bankBin, String accountNumber, String name, long amount, String addInfo) {
        StringBuilder qrData = new StringBuilder();

        qrData.append(buildTLV("00", "01"));
        qrData.append(buildTLV("01", "12"));

        // Merchant Account Information - VietQR
        String guid = buildTLV("00", "A000000727");
        String service = buildTLV("01", "1");
        String binTag = buildTLV("02", bankBin);
        String accTag = buildTLV("03", accountNumber);
        String info38 = guid + service + binTag + accTag;
        qrData.append(buildTLV("38", info38));

        qrData.append(buildTLV("52", "QRIBFTTA"));
        qrData.append(buildTLV("53", "704"));
        qrData.append(buildTLV("54", String.valueOf(amount)));
        qrData.append(buildTLV("58", "VN"));
        qrData.append(buildTLV("59", name.toUpperCase()));
        qrData.append(buildTLV("60", "HANOI"));

        String purpose = buildTLV("05", addInfo.replace(" ", ""));
        qrData.append(buildTLV("62", purpose));

        String dataWithoutCRC = qrData.toString() + "6304";
        String crc = calculateCRC16(dataWithoutCRC);
        return dataWithoutCRC + crc;
    }

    private String calculateCRC16(String str) {
        byte[] bytes = str.getBytes();
        int crc = 0xFFFF;
        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0)
                    crc = (crc << 1) ^ 0x1021;
                else
                    crc <<= 1;
            }
        }
        crc &= 0xFFFF;
        return String.format("%04X", crc);
    }

    private BufferedImage generateQRCodeImage(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
        return image;
    }

}