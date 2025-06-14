package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.CustomerManagement_CustomerDisplay;
import com.example.hotelmanagement.Main;
import com.example.hotelmanagement.ViewModels.CustomerManagementViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;

public class CustomerManagementController {

    private final CustomerManagementViewModel viewModel = new CustomerManagementViewModel();

    @FXML private MFXTextField keywordField;
    @FXML private MFXComboBox<String> customerTypeComboBox;
    @FXML private MFXComboBox<String> statusComboBox;
    @FXML private MFXComboBox<String> monthComboBox;
    @FXML private MFXComboBox<String> yearComboBox;
    @FXML private MFXButton btnRefresh;
    @FXML private TableView<CustomerManagement_CustomerDisplay> customerTable;
    @FXML private TableColumn<CustomerManagement_CustomerDisplay, String> idNumberColumn;
    @FXML private TableColumn<CustomerManagement_CustomerDisplay, String> nameColumn;
    @FXML private TableColumn<CustomerManagement_CustomerDisplay, String> typeColumn;
    @FXML private TableColumn<CustomerManagement_CustomerDisplay, String> birthdayColumn;
    @FXML private TableColumn<CustomerManagement_CustomerDisplay, String> identifyColumn;
    @FXML private TableColumn<CustomerManagement_CustomerDisplay, String> phoneColumn;
    @FXML private TableColumn<CustomerManagement_CustomerDisplay, String> statusColumn;
    @FXML private MFXButton btnExportPdf;

    private void openCustomerDetail(CustomerManagement_CustomerDisplay customerDTO) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Views/CustomerDetailView.fxml"));
            Parent root = loader.load();

            CustomerDetailController controller = loader.getController();
            controller.loadCustomer(customerDTO);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Chi tiết khách hàng");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Thiết lập dữ liệu cho bảng
        customerTable.setItems(viewModel.getCustomerDisplays());
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCustomerName()));
        idNumberColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getCustomerId())));
        identifyColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getIdentityNumber()));
        phoneColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhoneNumber()));
        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCustomerType()));
        birthdayColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFormattedBirthday()));
        statusColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));

        List<String> typeNames = viewModel.getCustomerTypesNames();
        if (!typeNames.contains("Loại khách")) {
            typeNames.add(0, "Loại khách");
        }
        customerTypeComboBox.setItems(FXCollections.observableArrayList(typeNames));
        customerTypeComboBox.setValue("Loại khách");

        // Trạng thái khách
        statusComboBox.setItems(viewModel.getStatusList());
        statusComboBox.setValue("Tất cả");
        statusComboBox.valueProperty().bindBidirectional(viewModel.selectedStatusProperty());

        monthComboBox.setItems(FXCollections.observableArrayList("Tháng", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
        monthComboBox.setValue("Tháng");

        yearComboBox.setItems(FXCollections.observableArrayList("Năm", "2020", "2021", "2022", "2023", "2024", "2025"));
        yearComboBox.setValue("Năm");

        viewModel.keywordProperty().bind(keywordField.textProperty());

        customerTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.setSelectedTypeName(newVal);
                viewModel.filterCustomers();
            }
        });

        monthComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.setSelectedMonth(newVal);
                viewModel.filterCustomers();
            }
        });

        yearComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.setSelectedYear(newVal);
                viewModel.filterCustomers();
            }
        });

        keywordField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.filterCustomers());
        statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> viewModel.filterCustomers());
        btnExportPdf.setOnAction(e -> viewModel.exportCustomersToPdf(btnExportPdf.getScene().getWindow()));

        btnRefresh.setOnAction(e -> {
            viewModel.loadCustomers();
            viewModel.loadCustomerTypes();
            customerTypeComboBox.setValue("Loại khách");
            statusComboBox.setValue("Tất cả");
            monthComboBox.setValue("Tháng");
            yearComboBox.setValue("Năm");
            keywordField.clear();
        });

        customerTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                var selected = customerTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openCustomerDetail(selected);
                }
            }
        });
    }
}
