package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Models.Servicebooking;
import com.example.hotelmanagement.ViewModels.ServiceUsageDetailViewModel; // Sử dụng ViewModel mới đổi tên
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
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ServiceDetailController {
    @FXML private Label roomNumberLabel;
    @FXML private Label customerNameLabel;
    @FXML private Label totalServiceCostLabel;
    @FXML private MFXButton closeButton;
    @FXML private Button exportButton;

    @FXML
    private TableView<ServiceUsageDetailViewModel> serviceTable;
    @FXML private TableColumn<ServiceUsageDetailViewModel, Integer> serviceIdColumn;
    @FXML private TableColumn<ServiceUsageDetailViewModel, String> serviceNameColumn;
    @FXML private TableColumn<ServiceUsageDetailViewModel, Integer> quantityColumn;
    @FXML private TableColumn<ServiceUsageDetailViewModel, String> bookingTimeColumn;
    @FXML private TableColumn<ServiceUsageDetailViewModel, String> formattedPriceColumn;
    @FXML private TableColumn<ServiceUsageDetailViewModel, String> formattedTotalColumn;

    private final ObservableList<ServiceUsageDetailViewModel> serviceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        });
        serviceIdColumn.setCellValueFactory(data -> data.getValue().IdProperty().asObject());
        serviceNameColumn.setCellValueFactory(data -> data.getValue().serviceNameProperty());
        quantityColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        bookingTimeColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        formattedPriceColumn.setCellValueFactory(data -> data.getValue().formattedPriceProperty());
        formattedTotalColumn.setCellValueFactory(data -> data.getValue().formattedTotalProperty());

        serviceTable.setItems(serviceList);

         exportButton.setOnAction(event -> handleExport());
    }

    public void setServiceDetails(Room room, Customer customer, List<Servicebooking> bookings) {
        if (room != null) {
            roomNumberLabel.setText(room.getRoomNumber().toString());
        } else {
            roomNumberLabel.setText("N/A");
        }

        if (customer != null) {
            customerNameLabel.setText(customer.getFullName());
        } else {
            customerNameLabel.setText("N/A");
        }

        List<ServiceUsageDetailViewModel> filtered = bookings.stream()
                .filter(sb -> "Đã xử lý".equals(sb.getStatus()))
                .map(ServiceUsageDetailViewModel::new)
                .toList();

        serviceList.setAll(filtered);

        BigDecimal totalCost = BigDecimal.ZERO;
        for (ServiceUsageDetailViewModel item : filtered) {
            totalCost = totalCost.add(item.getTotalPrice());
        }
        totalServiceCostLabel.setText(formatCurrency(totalCost));
    }

    private String formatCurrency(BigDecimal amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormatter.format(amount);
    }

    private void handleExport() {
        if (serviceList.isEmpty()) {
            System.out.println("Thông báo: Không có chi tiết dịch vụ để in PDF.");
            return;
        }
        Window parentWindow = exportButton.getScene().getWindow();
        exportServiceDetailsToPdf(serviceList, parentWindow);
    }

    private void exportServiceDetailsToPdf(List<ServiceUsageDetailViewModel> details, Window parentWindow) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "ChiTietDichVu_" + (roomNumberLabel.getText() != null && !roomNumberLabel.getText().equals("N/A") ? "Phong" + roomNumberLabel.getText() : "KH" + customerNameLabel.getText()) + "_" + timeStamp + ".pdf";
            fileChooser.setInitialFileName(fileName);

            File file = fileChooser.showSaveDialog(parentWindow);
            if (file == null) {
                return;
            }

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(50, 50, 50, 50);

            PdfFont vietnameseFont = PdfFontFactory.createFont(
                    "c:/windows/fonts/arial.ttf", // KIỂM TRA LẠI ĐƯỜNG DẪN NÀY
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            );
            PdfFont boldFont = PdfFontFactory.createFont(
                    "c:/windows/fonts/arialbd.ttf", // KIỂM TRA LẠI ĐƯỜNG DẪN NÀY
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            );

            document.add(new Paragraph("CHI TIẾT DỊCH VỤ SỬ DỤNG")
                    .setFont(boldFont)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));


            document.add(new Paragraph("Phòng số: " + roomNumberLabel.getText())
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setMarginBottom(5));
            document.add(new Paragraph("Khách hàng: " + customerNameLabel.getText())
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setMarginBottom(20));


            // --- BẢNG CHI TIẾT DỊCH VỤ ---
            float[] columnWidths = {30f, 160f, 60f, 90f, 80f, 80f}; // Có thể thử thay đổi các giá trị này
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100)); // Bảng chiếm 100% chiều rộng khả dụng
            table.setMarginBottom(20);

            String[] headers = {"ID", "Tên dịch vụ", "Số lượng", "Ngày sử dụng", "Đơn giá", "Thành tiền"};
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header))
                        .setFont(boldFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER)
                        .setBackgroundColor(new DeviceRgb(220, 230, 241)) // Màu nền header
                        .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(8));
            }

            BigDecimal totalServiceCost = BigDecimal.ZERO;
            for (ServiceUsageDetailViewModel detail : details) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.IdProperty())))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(detail.serviceNameProperty().getValue()))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.LEFT).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.quantityProperty().getValue())))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(detail.dateProperty().getValue()))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(detail.formattedPriceProperty().getValue()))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                table.addCell(new Cell().add(new Paragraph(detail.formattedTotalProperty().getValue()))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
                totalServiceCost = totalServiceCost.add(detail.getTotalPrice());
            }
            document.add(table);

            document.add(new Paragraph("Tổng chi phí dịch vụ: " + formatCurrency(totalServiceCost))
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(30));

            document.add(new Paragraph("Ngày xuất: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .setFont(vietnameseFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER));


            document.close();
            System.out.println("Thành công: Đã xuất chi tiết dịch vụ PDF thành công tại: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Lỗi khi xuất PDF (có thể do không tìm thấy font hoặc file đang mở): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Đã xảy ra lỗi không mong muốn khi xuất PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}