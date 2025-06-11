package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Models.Customer; // Cần import Customer để lấy tên
import com.example.hotelmanagement.Models.Room;     // Cần import Room để lấy số phòng
import com.example.hotelmanagement.Models.Servicebooking;
import com.example.hotelmanagement.ViewModels.ServiceUsageDetailViewModel; // Sử dụng ViewModel mới đổi tên
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ServiceDetailController {
    @FXML private Label roomNumberLabel;
    @FXML private Label customerNameLabel;
    @FXML private Label totalServiceCostLabel;
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
        serviceIdColumn.setCellValueFactory(data -> data.getValue().IdProperty().asObject());
        serviceNameColumn.setCellValueFactory(data -> data.getValue().serviceNameProperty());
        quantityColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        bookingTimeColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        formattedPriceColumn.setCellValueFactory(data -> data.getValue().formattedPriceProperty());
        formattedTotalColumn.setCellValueFactory(data -> data.getValue().formattedTotalProperty());

        serviceTable.setItems(serviceList);

        // exportButton.setOnAction(event -> handleExport());
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

    // private void handleExport() {
    //     System.out.println("Exporting service details...");
    //     // Implement export logic here (e.g., to PDF, Excel)
    // }
}