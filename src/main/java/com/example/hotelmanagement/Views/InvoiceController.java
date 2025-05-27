package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.Main;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;

public class InvoiceController {

    @FXML
    private MFXComboBox<String> entriesComboBox;

    @Setter
    @Getter
    @FXML
    private MFXComboBox<String> nCustomCombo;
    @FXML
    private TableView<Invoice> invoiceTable;

    @FXML
    private TextField filterTextField;

    @FXML
    private TableColumn<Invoice, String> invoiceIdColumn;
    @FXML
    private TableColumn<Invoice, String> nameClientColumn;
    @FXML
    private TableColumn<Invoice, String> emailColumn;
    @FXML
    private TableColumn<Invoice, String> startDateColumn;
    @FXML
    private TableColumn<Invoice, String> invoiceAmountColumn;
    @FXML
    private TableColumn<Invoice, String> statusColumn;
    public void showInvoiceDetail(Invoice selectedInvoice) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Views/InvoiceDetailView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 800);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Chi tiết hóa đơn");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        entriesComboBox.setValue("10");
        invoiceTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && invoiceTable.getSelectionModel().getSelectedItem() != null) {
                Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
                showInvoiceDetail(selectedInvoice);
            }
        });
        invoiceIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInvoiceId()));
        nameClientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNameClient()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        startDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartDate()));
        invoiceAmountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInvoiceAmount()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Cấu hình cột STATUS với màu sắc
        statusColumn.setCellFactory(_ -> new TextFieldTableCell<Invoice, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Đảm bảo ô lấp đầy không gian
                    setMaxWidth(Double.MAX_VALUE); //
                    setMaxHeight(Double.MAX_VALUE); //
                    setAlignment(Pos.CENTER); //
                    setWrapText(false); //

                    switch (item) {
                        case "Đã thanh toán" ->
                                setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-padding: 5; -fx-background-radius: 15; -fx-alignment: center;"); //
                        case "Chờ thanh toán"->
                                setStyle("-fx-background-color: #cff4fc; -fx-text-fill: #055160; -fx-padding: 5; -fx-background-radius: 15; -fx-alignment: center;"); //
                        case "Đã hủy" ->
                                setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #842029; -fx-padding: 5; -fx-background-radius: 15; -fx-alignment: center;"); //
                    }
                }
            }
        });

        // Thêm dữ liệu mẫu vào TableView
        invoiceTable.getItems().addAll(
                new Invoice("INV-10022024-001", "Kamisato Ayaka", "Ayaka@gmail.com", "Feb 12, 2024", "$95.66", "Đã thanh toán"),
                new Invoice("INV-10022024-002", "Kamisato Ayato", "Ayato@gmail.com", "Feb 10, 2024",  "$47.44", "Chờ thanh toán"),
                new Invoice("INV-10022024-003", "Raiden Shogun", "Raiden@gmail.com", "Feb 8, 2024",  "$48.44", "Đã thanh toán"),
                new Invoice("INV-10022024-004", "Ei Maulina", "Maulina@gmail.com", "Feb 6, 2024",  "$43.44", "Đã hủy"),
                new Invoice("INV-10022024-005", "Falah Maulana", "Falah@gmail.com", "Feb 4, 2024",  "$43.44", "Đã thanh toán"),
                new Invoice("INV-10022024-006", "Achmad Van", "Achmad@gmail.com", "Feb 2, 2024",  "$43.44", "Chờ thanh toán"),
                new Invoice("INV-10022024-007", "Xiaoli", "Xiaoli@gmail.com", "Jan 31, 2024",  "$43.44", "Đã hủy")
        );
    }

}

class Invoice {
    private String invoiceId;
    private String nameClient;
    private String email;
    private String startDate;
    private String invoiceAmount;
    private String status;

    public Invoice(String invoiceId, String nameClient, String email, String startDate, String invoiceAmount, String status) {
        this.invoiceId = invoiceId;
        this.nameClient = nameClient;
        this.email = email;
        this.startDate = startDate;
        this.invoiceAmount = invoiceAmount;
        this.status = status;
    }

    public String getInvoiceId() { return invoiceId; }
    public String getNameClient() { return nameClient; }
    public String getEmail() { return email; }
    public String getStartDate() { return startDate; }
    public String getInvoiceAmount() { return invoiceAmount; }
    public String getStatus() { return status; }
}