package com.example.hotelmanagement.Views;

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


public class InvoiceDetailController {

    @FXML private Label invoiceNoLabel;
    @FXML private Label paymentDateLabel;

    // Customer
    @FXML private Label customerNameLabel;
    @FXML private Label customerAddressLine1Label;
    @FXML private Label customerAddressLine2Label;

    // Order Details
    @FXML private TableView<RoomRentalDetail> roomDetailsTableView;
    @FXML private TableColumn<RoomRentalDetail, String> sttColumn;
    @FXML private TableColumn<RoomRentalDetail, String> roomNoColumn;
    @FXML private TableColumn<RoomRentalDetail, String> rentalDaysColumn;
    @FXML private TableColumn<RoomRentalDetail, String> serviceFeeColumn;
    @FXML private TableColumn<RoomRentalDetail, String> roomPriceColumn;
    @FXML private TableColumn<RoomRentalDetail, String> subtotalColumn;
    @FXML private TableColumn<RoomRentalDetail, String> viewServicesColumn;
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

    @FXML
    public void initialize() {
        // --- 1. Gán dữ liệu trực tiếp cho các Label ---
        invoiceNoLabel.setText("F-23520610");
        paymentDateLabel.setText("17/06/2023");

        customerNameLabel.setText("Công Ty TNHH ABC");
        customerAddressLine1Label.setText("74 RUE ANATOLE FRANCENATOLE FRANCE");
        customerAddressLine2Label.setText("LEVALLOIS-PERRET, 92300, France.");

        employeeNameLabel.setText("Huy Le");

        totalHtLabel.setText("7036.99");
        discountLabel.setText("140");
        totalVatLabel.setText("1457.32");
        vatAmountLabel.setText("1,379.40");
        totalDueLabel.setText("8276.78");

        companyInfo1Label.setText("UIT, Capital 1000, SIRET : 90106223200011");
        companyInfo2Label.setText("VAT: FRZ0901062232, Activity Type:58.29C");
        companyInfo3Label.setText("RCS: 901062232 Paris 8");

        // --- 2. Cấu hình TableView và thêm dữ liệu mẫu ---
        // Cấu hình các cột của TableView
        sttColumn.setCellValueFactory(new PropertyValueFactory<>("stt"));
        roomNoColumn.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        rentalDaysColumn.setCellValueFactory(new PropertyValueFactory<>("rentalDays"));
        serviceFeeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceFee"));
        roomPriceColumn.setCellValueFactory(new PropertyValueFactory<>("roomPrice"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        viewServicesColumn.setCellValueFactory(new PropertyValueFactory<>("viewServices"));

        viewServicesColumn.setCellFactory(col -> new TableCell<RoomRentalDetail, String>() {

            private final Button viewButton = new Button("Xem");

            {
                // Tùy chỉnh Button nếu cần
                viewButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 3 8; -fx-font-size: 11px;");
                viewButton.setOnAction(event -> {
                    RoomRentalDetail item = getTableView().getItems().get(getIndex());
                    System.out.println("Xem dịch vụ của phòng: " + item.getRoomNo());
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


        // Tạo dữ liệu mẫu cho TableView
        ObservableList<RoomRentalDetail> roomDetails = FXCollections.observableArrayList(
                new RoomRentalDetail("1", "101", "3", "50.00", "150.00", "500.00", ""),
                new RoomRentalDetail("2", "205", "5", "75.00", "120.00", "675.00", ""),
                new RoomRentalDetail("3", "302", "2", "20.00", "100.00", "220.00", "")
        );

        // Đổ dữ liệu vào TableView
        roomDetailsTableView.setItems(roomDetails);
    }

    // --- Class lồng cho RoomRentalDetail ---
    // Class này cần có các phương thức getter cho PropertyValueFactory
    public static class RoomRentalDetail {
        private final SimpleStringProperty stt;
        private final SimpleStringProperty roomNo;
        private final SimpleStringProperty rentalDays;
        private final SimpleStringProperty serviceFee;
        private final SimpleStringProperty roomPrice;
        private final SimpleStringProperty subtotal;
        private final SimpleStringProperty viewServices; // Giữ lại nếu bạn muốn truyền dữ liệu nhưng không bắt buộc khi dùng Button/ImageView

        public RoomRentalDetail(String stt, String roomNo, String rentalDays, String serviceFee, String roomPrice, String subtotal, String viewServices) {
            this.stt = new SimpleStringProperty(stt);
            this.roomNo = new SimpleStringProperty(roomNo);
            this.rentalDays = new SimpleStringProperty(rentalDays);
            this.serviceFee = new SimpleStringProperty(serviceFee);
            this.roomPrice = new SimpleStringProperty(roomPrice);
            this.subtotal = new SimpleStringProperty(subtotal);
            this.viewServices = new SimpleStringProperty(viewServices);
        }

        // Getters cho PropertyValueFactory
        public String getStt() { return stt.get(); }
        public SimpleStringProperty sttProperty() { return stt; }

        public String getRoomNo() { return roomNo.get(); }
        public SimpleStringProperty roomNoProperty() { return roomNo; }

        public String getRentalDays() { return rentalDays.get(); }
        public SimpleStringProperty rentalDaysProperty() { return rentalDays; }

        public String getServiceFee() { return serviceFee.get(); }
        public SimpleStringProperty serviceFeeProperty() { return serviceFee; }

        public String getRoomPrice() { return roomPrice.get(); }
        public SimpleStringProperty roomPriceProperty() { return roomPrice; }

        public String getSubtotal() { return subtotal.get(); }
        public SimpleStringProperty subtotalProperty() { return subtotal; }

        public String getViewServices() { return viewServices.get(); }
        public SimpleStringProperty viewServicesProperty() { return viewServices; }
    }
}