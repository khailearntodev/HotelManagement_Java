package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.Booking_CustomerDisplay;
import com.example.hotelmanagement.ViewModels.ReservationFormViewModel;
import com.example.hotelmanagement.ViewModels.ReservationViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ReservationFormController {
    @FXML private TableView<Booking_CustomerDisplay> customerTableView;
    @FXML private Label priceLabel;
    @FXML private Label checkOutDateLabel;
    @FXML private Label checkInDateLabel;
    @FXML private Label roomTypeLabel;
    @FXML private Label roomNumberLabel;
    @FXML private VBox ReservationFormVBox;
    @FXML private TableColumn<Booking_CustomerDisplay, Integer> colNum;
    @FXML private TableColumn<Booking_CustomerDisplay, String> colPhoneNumber;
    @FXML private TableColumn<Booking_CustomerDisplay, String> colIDNumber;
    @FXML private TableColumn<Booking_CustomerDisplay, String> colName;
    @FXML private TableColumn<Booking_CustomerDisplay, String> colAddress;

    ReservationFormViewModel viewModel;

    public void setViewModel(ReservationFormViewModel viewModel) {
        this.viewModel = viewModel;
        bindData();
    }

    private void bindData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        roomNumberLabel.textProperty().bind(viewModel.getReservationDetailDisplay().roomNumberProperty());
        roomTypeLabel.textProperty().bind(viewModel.getReservationDetailDisplay().roomTypeNameProperty());

        checkInDateLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            LocalDate date = viewModel.getReservationDetailDisplay().checkInDateProperty().get();
                            return date != null ? date.format(formatter) : "";
                        },
                        viewModel.getReservationDetailDisplay().checkInDateProperty()
                )
        );
        checkOutDateLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            LocalDate date = viewModel.getReservationDetailDisplay().checkOutDateProperty().get();
                            return date != null ? date.format(formatter) : "";
                        },
                        viewModel.getReservationDetailDisplay().checkOutDateProperty()
                )
        );
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        currencyFormat.setMaximumFractionDigits(0);
        priceLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            BigDecimal price = viewModel.getReservationDetailDisplay().getPrice();
                            return price != null
                                    ? currencyFormat.format(price) + " VNĐ/đêm"
                                    : "";
                        },
                        viewModel.getReservationDetailDisplay().priceProperty()
                )
        );

        customerTableView.setItems(viewModel.getReservationDetailDisplay().getCustomerDisplayList());
        colNum.setCellValueFactory(cell -> cell.getValue().ordinalNumberProperty().asObject());
        colNum.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        });
        colName.setCellValueFactory(cell -> cell.getValue().fullNameProperty());
        colName.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        colIDNumber.setCellValueFactory(cell -> cell.getValue().identityNumberProperty());
        colIDNumber.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        });

        colPhoneNumber.setCellValueFactory(cell -> cell.getValue().phoneNumberProperty());
        colPhoneNumber.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        });

        colAddress.setCellValueFactory(cell -> cell.getValue().customerAddressProperty());

        colAddress.setCellFactory(tc -> new TableCell<>() {
            private final Label label = new Label();
            {
                label.setWrapText(true);
                label.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setPrefHeight(35);
                } else {
                    label.setText(item);
                    double columnWidth = getTableColumn().getWidth();
                    label.setMaxWidth(columnWidth);
                    label.setPrefWidth(columnWidth);
                    setGraphic(label);
                    setText(null);
                    setAlignment(Pos.CENTER_LEFT);

                    Platform.runLater(() -> {
                        Text textNode = new Text(item);
                        textNode.setFont(label.getFont());
                        textNode.setWrappingWidth(columnWidth);

                        double textHeight = textNode.getBoundsInLocal().getHeight();
                        double cellHeight = Math.max(30, textHeight);

                        setPrefHeight(cellHeight);
                        setMinHeight(cellHeight);
                        setMaxHeight(cellHeight);
                    });
                }
            }
        });

    }

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) ReservationFormVBox.getScene().getWindow();
        stage.close();
    }
}
