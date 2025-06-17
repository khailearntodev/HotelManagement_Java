package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DAO.InvoiceDAO;
import com.example.hotelmanagement.DAO.InvoiceDetailAssembler;
import com.example.hotelmanagement.DAO.ReservationDAO;
import com.example.hotelmanagement.DTO.InvoiceDetail;
import com.example.hotelmanagement.Main;
import com.example.hotelmanagement.Models.Invoice;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.ViewModels.InvoiceViewModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat; // Import this
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale; // Import this

public class InvoiceController {

    @FXML private MFXComboBox<String> nCustomCombo;
    @FXML private MFXComboBox<String> ComboBox;
    @FXML private MFXDatePicker startDay;
    @FXML private MFXDatePicker endDay;

    @FXML private AnchorPane rootPane;
    @FXML
    private TextField minPriceField, maxPriceField;
    @FXML private MFXSlider minValue;
    @FXML private MFXSlider maxValue;

    @FXML private MFXTextField filterTextField;

    @FXML private TableView<Invoice> invoiceTable;

    @FXML private TableColumn<Invoice, String> invoiceIdColumn;
    @FXML private TableColumn<Invoice, String> nameClientColumn;
    @FXML private TableColumn<Invoice, String> invoiceTypes;
    @FXML private TableColumn<Invoice, String> startDateColumn;
    @FXML private TableColumn<Invoice, String> invoiceAmountColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;

    private final InvoiceViewModel viewModel = new InvoiceViewModel();
    private final InvoiceDAO dao=new InvoiceDAO();

    @FXML
    public void initialize() {
        URL cssUrl = getClass().getResource("/CSS/style.css");
        if (cssUrl != null) {
            rootPane.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("CSS file not found: /CSS/style.css");
        }

        minPriceField.setText("0₫");
        maxPriceField.setText("100.000.000₫");
        maxValue.setValue(100000000);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        invoiceTable.setRowFactory(tv -> {
            TableRow<Invoice> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Invoice selectedInvoice = row.getItem();
                    openInvoiceDetailView(selectedInvoice);
                }
            });
            return row;
        });

        invoiceIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        nameClientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        invoiceTypes.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getInvoiceType().toString()));

        invoiceTypes.setCellFactory(col -> new TableCell<Invoice, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    String display = switch (item) {
                        case "1" -> "Hóa đơn đặt trước";
                        case "2" -> "Hóa đơn thanh toán";
                        default -> "Không xác định";
                    };
                    String style = switch (item) {
                        case "1" -> "completedinvoice-label-table-view";
                        case "2" -> "notcomplete-label-table-view";
                        default -> "";
                    };
                    setGraphic(createStyledLabel(display, style));
                    setText(null);
                }
            }
        });
        startDateColumn.setCellValueFactory(cellData -> {
            Instant issueInstant = cellData.getValue().getIssueDate();
            if (issueInstant != null) {
                LocalDate issueDate = issueInstant.atZone(ZoneId.systemDefault()).toLocalDate();
                return new SimpleStringProperty(issueDate.format(formatter));
            } else {
                return new SimpleStringProperty("");
            }
        });
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        currencyFormatter.setMinimumFractionDigits(0); // Optional: if you don't want decimal places for VND

        invoiceAmountColumn.setCellValueFactory(cellData -> {
            BigDecimal amount = cellData.getValue().getTotalAmount();
            return new SimpleStringProperty(currencyFormatter.format(amount));
        });
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPaymentStatus()));

        statusColumn.setCellFactory(col -> new TableCell<Invoice, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    String style = switch (item) {
                        case "Đã thanh toán" -> "completedinvoice-label-table-view";
                        case "Chưa thanh toán" -> "notcomplete-label-table-view";
                        default -> "";
                    };
                    setGraphic(createStyledLabel(item, style));
                    setText(null);
                }
            }
        });
        invoiceTable.setItems(viewModel.getInvoiceList());

        nCustomCombo.getItems().addAll("Tất cả", "Đặt trước", "Thanh toán");
        nCustomCombo.getSelectionModel().selectFirst();
        nCustomCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            switch (newVal) {
                case "Đặt trước" -> viewModel.filterByInvoiceType(1);
                case "Thanh toán" -> viewModel.filterByInvoiceType(2);
                default -> viewModel.filterByInvoiceType(-1);
            }
        });

        ComboBox.getItems().addAll("Tất cả trạng thái", "Đã thanh toán", "Chưa thanh toán");
        ComboBox.getSelectionModel().selectFirst();
        ComboBox.valueProperty().addListener((obs, oldVal, newVal) -> viewModel.filterByPaymentStatus(newVal));

        minValue.valueProperty().addListener((obs, oldVal, newVal) -> {applyAmountFilter();
            minPriceField.setText(String.format("%,.0f₫", newVal.doubleValue()));});
        maxValue.valueProperty().addListener((obs, oldVal, newVal) -> {applyAmountFilter();
            maxPriceField.setText(String.format("%,.0f₫", newVal.doubleValue()));});

        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.filterByKeyword(newVal));

        startDay.valueProperty().addListener((obs, oldVal, newVal) -> applyDateFilter());
        endDay.valueProperty().addListener((obs, oldVal, newVal) -> applyDateFilter());
    }

    private void applyAmountFilter() {
        BigDecimal min = BigDecimal.valueOf(minValue.getValue());
        BigDecimal max = BigDecimal.valueOf(maxValue.getValue());
        viewModel.filterByAmountRange(min, max);
    }

    private void applyDateFilter() {
        LocalDate startDate = startDay.getValue();
        LocalDate endDate = endDay.getValue();
        viewModel.filterByIssueDateRange(startDate, endDate);
    }

    private Label createStyledLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.setPadding(new Insets(1, 6, 1, 6));
        label.setWrapText(false);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Region.USE_COMPUTED_SIZE);
        label.setMinWidth(Region.USE_PREF_SIZE);
        label.setMaxHeight(Region.USE_COMPUTED_SIZE);
        label.setMinHeight(Region.USE_COMPUTED_SIZE);
        label.getStyleClass().setAll(styleClass);
        return label;
    }
    private void openInvoiceDetailView(Invoice invoice) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Views/InvoiceDetailView.fxml"));
            Parent root = loader.load();

            Invoice fullInvoice = dao.getInvoiceWithDetails(invoice.getId());
            InvoiceDetailController controller = loader.getController();

            controller.setInvoice(fullInvoice);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết hóa đơn #" + invoice.getId());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}