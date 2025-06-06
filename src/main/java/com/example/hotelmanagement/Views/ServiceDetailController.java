package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Models.Servicebooking;
import com.example.hotelmanagement.ViewModels.ServiceUsageDetailViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

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
        serviceNameColumn.setCellValueFactory(data -> data.getValue().serviceNameProperty());
        serviceIdColumn.setCellValueFactory(data -> data.getValue().IdProperty().asObject());
        quantityColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        bookingTimeColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        formattedPriceColumn.setCellValueFactory(data -> data.getValue().formattedPriceProperty());
        formattedTotalColumn.setCellValueFactory(data -> data.getValue().formattedTotalProperty());

        serviceTable.setItems(serviceList);
    }

    public void setServiceDetails(List<Servicebooking> bookings) {
        List<ServiceUsageDetailViewModel> filtered = bookings.stream()
                .filter(sb -> "Đã xử lý".equals(sb.getStatus()))
                .map(ServiceUsageDetailViewModel::new)
                .toList();

        serviceList.setAll(filtered);
    }
}

