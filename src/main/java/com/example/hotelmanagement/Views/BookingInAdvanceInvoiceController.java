package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.ViewModels.BookingInAdvanceInvoiceViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class BookingInAdvanceInvoiceController implements Initializable {
    @FXML private Label codeLabel;
    @FXML private TextArea customerInfoTextArea;
    @FXML private Label totalLabel;
    @FXML private Label checkinDateLabel;
    @FXML private Label checkoutDateLabel;
    @FXML private Label priceLabel;
    @FXML private Label invoiceDateLabel;
    @FXML private Label roomTypeName;
    @FXML private Label roomNumberLabel;
    @FXML private VBox bookingInAdvanceInvoiceVBox;

    BookingInAdvanceInvoiceViewModel viewModel;

    public void setViewModel(BookingInAdvanceInvoiceViewModel viewModel) {
        this.viewModel = viewModel;
        codeLabel.textProperty().bind(viewModel.getPreBookingInvoiceDisplay().codeProperty());

        Bindings.bindBidirectional(
                roomNumberLabel.textProperty(),
                viewModel.getPreBookingInvoiceDisplay().roomNumberProperty(),
                new NumberStringConverter()
        );
        roomTypeName.textProperty().bindBidirectional(viewModel.getPreBookingInvoiceDisplay().roomTypeNameProperty());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        invoiceDateLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> formatter.format(viewModel.getPreBookingInvoiceDisplay().getInvoiceDate()),
                        viewModel.getPreBookingInvoiceDisplay().invoiceDateProperty()
                )
        );

        NumberFormat numFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        priceLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> numFormatter.format(viewModel.getPreBookingInvoiceDisplay().getPrice()) + " VNĐ/đêm",
                        viewModel.getPreBookingInvoiceDisplay().priceProperty()
                )
        );

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        checkinDateLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> formatter2.format(viewModel.getPreBookingInvoiceDisplay().getStartDate()),
                        viewModel.getPreBookingInvoiceDisplay().startDateProperty()
                )
        );

        checkoutDateLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> formatter2.format(viewModel.getPreBookingInvoiceDisplay().getEndDate()),
                        viewModel.getPreBookingInvoiceDisplay().endDateProperty()
                )
        );

        totalLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> numFormatter.format(viewModel.getPreBookingInvoiceDisplay().getTotalPrice()) + " VNĐ",
                        viewModel.getPreBookingInvoiceDisplay().totalPriceProperty()
                )
        );

        customerInfoTextArea.textProperty().bind(
                Bindings.concat(viewModel.getPreBookingInvoiceDisplay().getName(), "\n", viewModel.getPreBookingInvoiceDisplay().getAddress(), "\nSĐT: ", viewModel.getPreBookingInvoiceDisplay().getPhoneNumber())
        );
    }

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) bookingInAdvanceInvoiceVBox.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void handleConfirmInvoice(MouseEvent mouseEvent) {
        viewModel.saveInvoice();
        Stage stage = (Stage) bookingInAdvanceInvoiceVBox.getScene().getWindow();
        stage.close();
    }
}
