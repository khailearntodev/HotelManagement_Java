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
import io.github.palexdev.materialfx.controls.MFXSlider;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceController {

    @FXML private MFXComboBox<String> nCustomCombo;
    @FXML private MFXComboBox<String> entriesComboBox;
    @FXML private MFXComboBox<String> ComboBox;

    @FXML private MFXSlider minValue;
    @FXML private MFXSlider maxValue;

    @FXML private TextField filterTextField;

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
        startDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIssueDate().toString()));
        invoiceAmountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTotalAmount().toPlainString()));
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

        minValue.valueProperty().addListener((obs, oldVal, newVal) -> applyAmountFilter());
        maxValue.valueProperty().addListener((obs, oldVal, newVal) -> applyAmountFilter());

        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.filterByKeyword(newVal));

        entriesComboBox.getItems().addAll("10", "25", "50", "Tất cả");
        //entriesComboBox.getSelectionModel().select("Tất cả");
        entriesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals("Tất cả")) {
                viewModel.setLimit(-1);
            } else {
                viewModel.setLimit(Integer.parseInt(newVal));
            }
        });
    }

    private void applyAmountFilter() {
        BigDecimal min = BigDecimal.valueOf(minValue.getValue());
        BigDecimal max = BigDecimal.valueOf(maxValue.getValue());
        viewModel.filterByAmountRange(min, max);
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