package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Models.Invoice;
import com.example.hotelmanagement.ViewModels.InvoiceDetailViewModel;
import com.example.hotelmanagement.ViewModels.InvoiceViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button; // Import Button
import com.example.hotelmanagement.DTO.InvoiceDetail;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


public class InvoiceDetailController {

    @FXML private Label invoiceNoLabel;
    @FXML private Label paymentDateLabel;

    // Customer
    @FXML private Label customerNameLabel;
    @FXML private Label customerAddress;

    // Order Details
    @FXML private TableView<InvoiceDetailViewModel> detailTable;
    @FXML private TableColumn<InvoiceDetailViewModel, Number> soThuTuColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, Number> soPhongColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, Number> soNgayThueColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, BigDecimal> phiDichVuColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, BigDecimal> donGiaPhongColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, BigDecimal> thanhTienColumn;
    @FXML private TableColumn<InvoiceDetailViewModel, String> viewServicesColumn;
    @FXML private Label employeeNameLabel;

    // Totals
    @FXML private Label totalHtLabel;
    @FXML private Label discountLabel;
    @FXML private Label totalVatLabel;
    @FXML private Label vatAmountLabel;
    @FXML private Label totalDueLabel;

    // Footer
    @FXML private Label companyInfo1Label;
    @FXML private Label companyInfo2Label;
    @FXML private Label companyInfo3Label;
    private final ObservableList<InvoiceDetailViewModel> detailList = FXCollections.observableArrayList();
    private Invoice invoice = new Invoice();
    @FXML
    public void initialize() {
        // --- 1. Gán dữ liệu trực tiếp cho các Label ---
        invoiceNoLabel.setText("F-23520610");
        paymentDateLabel.setText("17/06/2023");

        customerNameLabel.setText("Công Ty TNHH ABC");
//        customerAddressLine1Label.setText("74 RUE ANATOLE FRANCENATOLE FRANCE");
//        customerAddressLine2Label.setText("LEVALLOIS-PERRET, 92300, France.");

        employeeNameLabel.setText("Huy Le");

//        totalHtLabel.setText("7036.99");
//        discountLabel.setText("140");
//        totalVatLabel.setText("1457.32");
//        vatAmountLabel.setText("1,379.40");
     //   totalDueLabel.setText();
//
//        companyInfo1Label.setText("UIT, Capital 1000, SIRET : 90106223200011");
//        companyInfo2Label.setText("VAT: FRZ0901062232, Activity Type:58.29C");
//        companyInfo3Label.setText("RCS: 901062232 Paris 8");

        // --- 2. Cấu hình TableView và thêm dữ liệu mẫu ---
        // Cấu hình các cột của TableView
        soThuTuColumn.setCellValueFactory(data -> data.getValue().soThuTuProperty());
        soPhongColumn.setCellValueFactory(data -> data.getValue().soPhongProperty());
        soNgayThueColumn.setCellValueFactory(data -> data.getValue().soNgayThueProperty());
        phiDichVuColumn.setCellValueFactory(data -> data.getValue().phiDichVuProperty());
        donGiaPhongColumn.setCellValueFactory(data -> data.getValue().donGiaPhongProperty());
        thanhTienColumn.setCellValueFactory(data -> data.getValue().thanhTienProperty());

        viewServicesColumn.setCellFactory(col -> new TableCell<InvoiceDetailViewModel, String>() {

            private final Button viewButton = new Button("Xem");

            {
                // Tùy chỉnh Button nếu cần
                viewButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 3 8; -fx-font-size: 11px;");
                viewButton.setOnAction(event -> {
//                    InvoiceViewModel item = getTableView().getItems().get(getIndex());
//                    System.out.println("Xem dịch vụ của phòng: " + item.getRoomNo());
                });

            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);

                }
            }
        });

        detailTable.setItems(detailList);
    }
    public void setInvoiceDetails(List<InvoiceDetail> details) {
        List<InvoiceDetailViewModel> viewModels = details.stream()
                .map(d -> new InvoiceDetailViewModel(
                        d.soThuTuProperty().get(),
                        d.soPhongProperty().get(),
                        d.soNgayThueProperty().get(),
                        d.phiDichVuProperty().get(),
                        d.donGiaPhongProperty().get(),
                        d.thanhTienProperty().get()
                ))
                .collect(Collectors.toList());

        detailList.setAll(viewModels);
    }
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        invoiceNoLabel.setText(String.valueOf(invoice.getId()));
        customerNameLabel.setText(invoice.getCustomerName());
        customerAddress.setText(invoice.getCustomerAddres());
        employeeNameLabel.setText(invoice.getEmployeeID().getFullName());
        paymentDateLabel.setText(invoice.getIssueDate().toString());
        totalDueLabel.setText(invoice.getTotalAmount().toString());
    }



}