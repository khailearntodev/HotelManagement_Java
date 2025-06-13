package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DAO.InvoiceDAO;
import com.example.hotelmanagement.Main;
import com.example.hotelmanagement.Models.Invoice;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Models.Servicebooking;
import com.example.hotelmanagement.ViewModels.InvoiceDetailViewModel;
import com.example.hotelmanagement.ViewModels.InvoiceViewModel;
import com.example.hotelmanagement.ViewModels.SelectRoomForCheckOutViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class InvoiceDetailController {

    @FXML private AnchorPane rootPane;
    @FXML private Label invoiceNoLabel;
    @FXML private Label paymentDateLabel;
    @FXML private MFXButton closeButton;
    @FXML private MFXButton payButton;
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
    @FXML private Label totalServiceLabel;

    // Footer
    private final ObservableList<InvoiceDetailViewModel> detailList = FXCollections.observableArrayList();
    private Invoice invoice = new Invoice();
    @FXML
    public void initialize() {
        URL cssUrl = getClass().getResource("/CSS/invoicedetail.css");
        if (cssUrl != null) {
            rootPane.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("CSS file not found: /CSS/style.css");
        }
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        });
        payButton.setOnAction(event -> {
            if ("Đã thanh toán".equals(invoice.getPaymentStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Hóa đơn này đã được thanh toán rồi.");
                return;
            }
            else {
                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Thanh toán thành công!");
            }
            invoice.setPaymentStatus("Đã thanh toán");
            InvoiceDAO dao = new InvoiceDAO();
            dao.update(invoice);
        });
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
                    Reservation reservation = rowData.getReservation();

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

    invoiceNoLabel.setText(String.valueOf(invoice.getId()));
    customerNameLabel.setText(invoice.getCustomerName());
    customerAddress.setText(invoice.getCustomerAddres());
    employeeNameLabel.setText(invoice.getEmployeeID().getFullName());
    Instant issueInstant = invoice.getIssueDate();
    if (issueInstant != null) {
        LocalDateTime localDateTime = issueInstant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        paymentDateLabel.setText(localDateTime.format(formatter));
    } else {
        paymentDateLabel.setText("N/A");
    }
    totalDueLabel.setText(invoice.getTotalAmount().toString());

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
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}