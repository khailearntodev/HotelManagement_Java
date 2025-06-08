package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.RevenueReportService;
import com.example.hotelmanagement.Models.RevenueReportDetail;
import com.example.hotelmanagement.ViewModels.StatisticViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;

public class StatisticController {

    @FXML
    private MFXComboBox<String> monthComboBox;
    @FXML private MFXComboBox<String> yearComboBox;
    @FXML private MFXButton byMonthButton;
    @FXML private MFXButton byYearButton;
    @FXML private MFXButton generateReportButton;
    @FXML private MFXButton exportButton;
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalRentalLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label popularRoomTypeLabel;
    @FXML private Label averageRevenueLabel;
    @FXML private PieChart roomTypeRevenuePieChart;
    @FXML private TableView<RevenueReportDetail> revenueReportTable;
    @FXML private TableColumn<RevenueReportDetail, String> roomTypeColumn;
    @FXML private TableColumn<RevenueReportDetail, String> revenueColumn;
    @FXML private TableColumn<RevenueReportDetail, String> contributionColumn;

    private StatisticViewModel viewModel;
    private RevenueReportService revenueReportService = new RevenueReportService();


    public void initialize() {
        viewModel = new StatisticViewModel();

        monthComboBox.setItems(viewModel.getMonths());
        yearComboBox.setItems(viewModel.getYears());

        monthComboBox.valueProperty().bindBidirectional(viewModel.selectedMonthProperty());
        yearComboBox.valueProperty().bindBidirectional(viewModel.selectedYearProperty());

        totalRevenueLabel.textProperty().bind(viewModel.totalRevenueProperty());
        totalRentalLabel.textProperty().bind(viewModel.totalRentalProperty());
        totalBookingsLabel.textProperty().bind(viewModel.totalBookingsProperty());
        popularRoomTypeLabel.textProperty().bind(viewModel.popularRoomTypeProperty());
        averageRevenueLabel.textProperty().bind(viewModel.averageRevenueProperty());

        roomTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoomTypeID().getTypeName()));
        revenueColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%,.0f", data.getValue().getRevenue())));

        // Tính toán và hiển thị cột tỷ lệ đóng góp
        contributionColumn.setCellValueFactory(data -> {
            try {
                double total = Double.parseDouble(totalRevenueLabel.getText().replaceAll("[^\\d.]", ""));
                if (total > 0) {
                    double revenue = data.getValue().getRevenue().doubleValue();
                    double contribution = (revenue / total) * 100;
                    return new SimpleObjectProperty<>(String.format("%.2f%%", contribution));
                }
            } catch (NumberFormatException e) {
            }
            return new SimpleObjectProperty<>("N/A");
        });


        revenueReportTable.setItems(viewModel.getRevenueDetails());

        byMonthButton.setOnAction(e -> loadStatisticsByMonth());
        byYearButton.setOnAction(e -> loadStatisticsByYear());
        generateReportButton.setOnAction(e -> generateNewReport());
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
            } catch (NumberFormatException e) {
                System.err.println("Invalid month or year selected");
            }
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
            } catch (NumberFormatException e) {
                System.err.println("Invalid year selected");
            }
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
                System.out.println("Tạo báo cáo thành công!");
                viewModel.loadStatisticsByMonth(month, year);
            } else {
                System.out.println("Tạo báo cáo thất bại hoặc báo cáo đã tồn tại.");
            }
        } else {
            System.out.println("Vui lòng chọn tháng và năm để tạo báo cáo.");
        }
    }

}
