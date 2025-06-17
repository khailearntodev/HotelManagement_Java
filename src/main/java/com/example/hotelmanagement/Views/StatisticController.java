package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.RevenueReportService;
import com.example.hotelmanagement.Models.RevenueReportDetail;
import com.example.hotelmanagement.ViewModels.StatisticViewModel;
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
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatisticController {

    @FXML private MFXComboBox<String> monthComboBox;
    @FXML private MFXComboBox<String> yearComboBox;
    @FXML private MFXButton byMonthButton;
    @FXML private MFXButton byYearButton;
    @FXML private MFXButton generateReportButton;
    @FXML private MFXButton exportButton;
    @FXML private Label totalServiceLabel;
    @FXML private Label totalRentalLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label popularRoomTypeLabel;
    @FXML private Label averageRevenueLabel;
    @FXML private PieChart roomTypeRevenuePieChart;
    @FXML private TableView<RevenueReportDetail> revenueReportTable;
    @FXML private TableColumn<RevenueReportDetail, String> roomTypeColumn;
    @FXML private TableColumn<RevenueReportDetail, String> revenueColumn;
    @FXML private TableColumn<RevenueReportDetail, String> contributionColumn;
    private boolean isMonthlyReport = false;

    private StatisticViewModel viewModel;
    private RevenueReportService revenueReportService = new RevenueReportService();

    public void initialize() {
        viewModel = new StatisticViewModel();

        monthComboBox.setItems(viewModel.getMonths());
        yearComboBox.setItems(viewModel.getYears());

        monthComboBox.valueProperty().bindBidirectional(viewModel.selectedMonthProperty());
        yearComboBox.valueProperty().bindBidirectional(viewModel.selectedYearProperty());

        totalServiceLabel.textProperty().bind(viewModel.totalRevenueProperty());
        totalRentalLabel.textProperty().bind(viewModel.totalRentalProperty());
        totalBookingsLabel.textProperty().bind(viewModel.totalBookingsProperty());
        popularRoomTypeLabel.textProperty().bind(viewModel.popularRoomTypeProperty());
        averageRevenueLabel.textProperty().bind(viewModel.averageRevenueProperty());

        roomTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoomTypeID().getTypeName()));
        revenueColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%,.0f", data.getValue().getRevenue())));

        contributionColumn.setCellValueFactory(data -> {
            try {
                String totalText = totalRentalLabel.getText().replaceAll("[.,]", "").replaceAll(" VNĐ", "");
                double total = Double.parseDouble(totalText);
                if (total > 0) {
                    double revenue = data.getValue().getRevenue().doubleValue();
                    double contribution = (revenue / total) * 100;
                    return new SimpleObjectProperty<>(String.format("%.2f%%", contribution));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return new SimpleObjectProperty<>("N/A");
        });

        revenueReportTable.setItems(viewModel.getRevenueDetails());

        byMonthButton.setOnAction(e -> loadStatisticsByMonth());
        byYearButton.setOnAction(e -> loadStatisticsByYear());
        generateReportButton.setOnAction(e -> generateNewReport());
        exportButton.setOnAction(event -> export());
    }

    private void loadStatisticsByMonth() {
        String monthStr = monthComboBox.getValue();
        String yearStr = yearComboBox.getValue();

        if (monthStr != null && !monthStr.isEmpty() && yearStr != null && !yearStr.isEmpty()) {
            try {
                int month = Integer.parseInt(monthStr);
                int year = Integer.parseInt(yearStr);
                viewModel.loadStatisticsByMonth(month, year);
                updatePieChart();
                revenueReportTable.refresh();
                isMonthlyReport = true;
            } catch (NumberFormatException e) {
                showAlert("Lỗi", "Tháng hoặc năm không hợp lệ!", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Thông báo", "Vui lòng chọn tháng và năm!", Alert.AlertType.WARNING);
        }
    }

    private void loadStatisticsByYear() {
        String yearStr = yearComboBox.getValue();
        if (yearStr != null && !yearStr.isEmpty()) {
            try {
                int year = Integer.parseInt(yearStr);
                viewModel.loadStatisticsByYear(year);
                updatePieChart();
                revenueReportTable.refresh();
                isMonthlyReport = false;
            } catch (NumberFormatException e) {
                showAlert("Lỗi", "Năm không hợp lệ!", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Thông báo", "Vui lòng chọn năm!", Alert.AlertType.WARNING);
        }
    }

    private void updatePieChart() {
        roomTypeRevenuePieChart.getData().clear();
        for (RevenueReportDetail detail : viewModel.getRevenueDetails()) {
            roomTypeRevenuePieChart.getData().add(
                    new PieChart.Data(detail.getRoomTypeID().getTypeName(), detail.getRevenue().doubleValue())
            );
        }
    }

    private void generateNewReport() {
        String monthStr = monthComboBox.getValue();
        String yearStr = yearComboBox.getValue();

        if (monthStr != null && yearStr != null) {
            int month = Integer.parseInt(monthStr);
            int year = Integer.parseInt(yearStr);

            boolean success = revenueReportService.generateReport(month, year);
            if (success) {
                showAlert("Thành công", "Tạo báo cáo thành công!", Alert.AlertType.INFORMATION);
                viewModel.loadStatisticsByMonth(month, year);
                loadStatisticsByMonth();
            } else {
                showAlert("Thất bại", "Không có dữ liệu cho tháng này.", Alert.AlertType.WARNING);
            }
        } else {
            showAlert("Thông báo", "Vui lòng chọn tháng và năm để tạo báo cáo.", Alert.AlertType.WARNING);
        }
    }

    void export() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "BaoCaoDoanhThu_" + timeStamp + ".pdf";
            fileChooser.setInitialFileName(fileName);

            File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());
            if (file == null) {
                return;
            }

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(50, 50, 50, 50);

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

            document.add(new Paragraph("BÁO CÁO DOANH THU KHÁCH SẠN")
                    .setFont(boldFont)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            String reportPeriod = "";
            if (isMonthlyReport) {
                reportPeriod = "Tháng: " + monthComboBox.getValue() + " - Năm: " + yearComboBox.getValue();
            } else {
                reportPeriod = "Năm: " + yearComboBox.getValue();
            }

            document.add(new Paragraph("Kỳ báo cáo: " + reportPeriod)
                    .setFont(vietnameseFont)
                    .setFontSize(12)
                    .setMarginBottom(10)
                    .setTextAlignment(TextAlignment.CENTER));


            float[] columnWidths = {150f, 150f, 150f};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginBottom(20);

            String[] headers = {"Loại phòng", "Doanh thu", "Tỷ lệ đóng góp"};
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header))
                        .setFont(boldFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER)
                        .setBackgroundColor(new DeviceRgb(220, 230, 241))
                        .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)).setPadding(8));
            }

            for (RevenueReportDetail detail : viewModel.getRevenueDetails()) {
                table.addCell(new Cell().add(new Paragraph(detail.getRoomTypeID().getTypeName()))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.LEFT).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));

                table.addCell(new Cell().add(new Paragraph(String.format("%,.0f", detail.getRevenue())))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));

                double total = 0;
                try {
                    total = Double.parseDouble(totalRentalLabel.getText().replaceAll("[^\\d.]", ""));
                } catch (NumberFormatException e) {
                    showAlert("Lỗi", "Không thể tính toán tỷ lệ đóng góp do lỗi dữ liệu.", Alert.AlertType.ERROR);
                }

                String contribution = "N/A";
                if (total > 0) {
                    double revenue = detail.getRevenue().doubleValue();
                    double cont = (revenue / total) * 100;
                    contribution = String.format("%.2f%%", cont);
                }
                table.addCell(new Cell().add(new Paragraph(contribution))
                        .setFont(vietnameseFont).setFontSize(10).setTextAlignment(TextAlignment.RIGHT).setPadding(5).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
            }
            document.add(table);

            document.add(new Paragraph("Tổng doanh thu dịch vụ: " + totalServiceLabel.getText())
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Tổng doanh thu thuê phòng: " + totalRentalLabel.getText())
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Tổng lượt thuê phòng: " + totalBookingsLabel.getText())
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Loại phòng phổ biến: " + popularRoomTypeLabel.getText())
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Doanh thu trung bình mỗi lượt thuê: " + averageRevenueLabel.getText())
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(30));

            document.add(new Paragraph("Ngày xuất báo cáo: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .setFont(vietnameseFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();

            showAlert("Thành công", "Đã xuất báo cáo PDF thành công tại:\n" + file.getAbsolutePath(), Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            showAlert("Lỗi", "Lỗi khi xuất PDF (có thể do không tìm thấy font hoặc file đang mở).", Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Lỗi", "Đã xảy ra lỗi không mong muốn khi xuất PDF.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
