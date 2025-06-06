package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Main;
import com.example.hotelmanagement.Models.Invoice;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Models.Servicebooking;
import com.example.hotelmanagement.ViewModels.InvoiceDetailViewModel;
import com.example.hotelmanagement.ViewModels.InvoiceViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    @FXML private Label totalDueLabel;

    // Footer
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
                viewButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 3 8; -fx-font-size: 11px;");
                viewButton.setOnAction(event -> {
                    InvoiceDetailViewModel rowData = getTableView().getItems().get(getIndex());
                    Reservation reservation = rowData.getReservation(); // ← Bạn cần lưu Reservation trong ViewModel!

                    List<Servicebooking> bookings = new ArrayList<>(reservation.getServicebookings());
                    openServiceDetail(bookings);
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

public void setInvoice(Invoice invoice) {
    this.invoice = invoice;

    // 1. Hiển thị thông tin hóa đơn
    invoiceNoLabel.setText(String.valueOf(invoice.getId()));
    customerNameLabel.setText(invoice.getCustomerName());
    customerAddress.setText(invoice.getCustomerAddres());
    employeeNameLabel.setText(invoice.getEmployeeID().getFullName());
    paymentDateLabel.setText(invoice.getIssueDate().toString());
    totalDueLabel.setText(invoice.getTotalAmount().toString());

    // 2. Hiển thị bảng chi tiết từ reservation
    List<InvoiceDetailViewModel> viewModels = invoice.getReservations().stream()
            .map(InvoiceDetailViewModel::new)
            .toList();

    detailList.setAll(viewModels);
}

    @FXML
    public void openServiceDetail(List<Servicebooking> bookings) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Views/ServiceDetailView.fxml"));
            Parent root = loader.load();

            ServiceDetailController controller = loader.getController();
            controller.setServiceDetails(bookings);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết dịch vụ");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}