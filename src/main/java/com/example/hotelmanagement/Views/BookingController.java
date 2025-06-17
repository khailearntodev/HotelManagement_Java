package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.Booking_CustomerDisplay;
import com.example.hotelmanagement.DTO.Reservation_ServiceBookingDisplay;
import com.example.hotelmanagement.ViewModels.AddCustomerViewModel;
import com.example.hotelmanagement.ViewModels.BookingCalendarViewModel;
import com.example.hotelmanagement.ViewModels.BookingViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import lombok.Setter;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.ResourceBundle;

public class BookingController implements Initializable {
    @FXML private TableView<Booking_CustomerDisplay> customerTableView;
    @FXML private TableColumn<Booking_CustomerDisplay, Integer> colNum;
    @FXML private TableColumn<Booking_CustomerDisplay, String> colPhoneNumber;
    @FXML private TableColumn<Booking_CustomerDisplay, String> colIDNumber;
    @FXML private TableColumn<Booking_CustomerDisplay, String> colName;
    @FXML private TableColumn<Booking_CustomerDisplay, String> colAddress;
    @FXML private MFXButton openAddCustomerBtn;
    @FXML private MFXButton bookingServiceBtn;
    @FXML private MFXTextField bookingNoteTextField;
    @FXML private Label priceLabel;
    @FXML private Button calendarButton;
    @Setter
    @FXML private MFXTextField checkoutDateTextfield;
    @FXML private Label checkInDateLabel;
    @FXML private Label roomTypeLabel;
    @FXML private Label roomNumberLabel;
    @FXML private VBox BookingVBox;
    @FXML private MFXButton addCustomerBtn;
    @FXML private ImageView closeIcon;

    private BookingViewModel viewModel;
    private ObservableList<Booking_CustomerDisplay> customerBookingDisplays = FXCollections.observableArrayList();

    public void setViewModel(BookingViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.loadService();
        bindData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        };

        Bindings.bindBidirectional(
                checkoutDateTextfield.textProperty(),
                viewModel.getCheckOutDate(),
                converter
        );

        checkoutDateTextfield.setEditable(viewModel.isCanEdit());
        calendarButton.setDisable(!viewModel.isCanEdit());
    }

    private void bindData() {
        roomNumberLabel.textProperty().bindBidirectional(viewModel.getRoomDisplay().roomNumberProperty(), new NumberStringConverter());
        roomTypeLabel.textProperty().bindBidirectional(viewModel.getRoomDisplay().roomTypeNameProperty());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter(formatter, null);
        ObjectProperty<LocalDateTime> dateProperty = new SimpleObjectProperty<>(LocalDateTime.now());
        checkInDateLabel.textProperty().bindBidirectional(dateProperty, converter);
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        currencyFormat.setMaximumFractionDigits(0);
        priceLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            BigDecimal price = viewModel.getRoomDisplay().roomPriceProperty().get();
                            return price != null
                                    ? currencyFormat.format(price) + " VNĐ/đêm"
                                    : "";
                        },
                        viewModel.getRoomDisplay().roomPriceProperty()
                )
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerTableView.setFixedCellSize(Region.USE_COMPUTED_SIZE);

        colNum.setCellValueFactory(cell -> cell.getValue().ordinalNumberProperty().asObject());
        colNum.setCellFactory(column -> new TableCell<>() {
            private final Label label = new Label();
            private final Button deleteButton = new Button("X");

            {
                deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-cursor: hand;");
                deleteButton.setOnAction(event -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < viewModel.getCustomerList().size()) {
                        viewModel.getCustomerList().remove(idx);

                        customerBookingDisplays.clear();
                        for (int i = 0; i < viewModel.getCustomerList().size(); i++) {
                            customerBookingDisplays.add(new Booking_CustomerDisplay(viewModel.getCustomerList().get(i), i + 1));
                        }

                        getTableView().requestFocus();
                    }
                });

                hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
                    TableRow<?> row = getTableRow();
                    if (row != null && row.getIndex() == getIndex() && !isEmpty()) {
                        setGraphic(isNowHovered ? deleteButton : label);
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    label.setText(item.toString());
                    setText(null);
                    setGraphic(label);
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

        Platform.runLater(() -> BookingVBox.requestFocus());
    }

    public void handleClose(MouseEvent event) {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }

    public void showServicePopup(MouseEvent event) {
        Popup popup = new Popup();
        popup.setAutoHide(true);

        ListView<Reservation_ServiceBookingDisplay> listView = new ListView<>(viewModel.getServices());
        listView.setPrefSize(350, 250);
        listView.setStyle("-fx-background-color: white; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        listView.setCellFactory(new Callback<ListView<Reservation_ServiceBookingDisplay>, ListCell<Reservation_ServiceBookingDisplay>>() {
            @Override
            public ListCell<Reservation_ServiceBookingDisplay> call(ListView<Reservation_ServiceBookingDisplay> param) {
                return new ServiceListCell();
            }
        });

        MFXButton okBtn = new MFXButton("OK");
        okBtn.getStyleClass().add("OK-button");
        okBtn.setOnAction(e -> popup.hide());
        popup.setOnHidden(e -> {
            for (Reservation_ServiceBookingDisplay item : viewModel.getServices()) {
                if (!item.isSelected() || item.getQuantity() == 0) {
                    item.setSelected(false);
                    item.setQuantity(0);
                }
            }
            System.out.println("Dịch vụ đã chọn:");
            viewModel.getServices().forEach(item -> System.out.println(
                    item.getServiceName() + " - Số lượng: " + item.getQuantity()
            ));
        });

        HBox buttons = new HBox(okBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(10, listView, buttons);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: white; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;-fx-border-width: 1;-fx-border-color: black");

        popup.getContent().add(layout);
        listView.setFocusTraversable(false);
        okBtn.setFocusTraversable(false);
        layout.setFocusTraversable(false);
        Node source = (Node) event.getSource();
        Bounds bounds = source.localToScreen(source.getBoundsInLocal());
        popup.show(source, bounds.getMinX(), bounds.getMaxY() + 5);
    }

    public void saveBooking(MouseEvent mouseEvent) {
        if (viewModel.getCheckOutDate().get() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn ngày trả phòng");
            alert.showAndWait();
            return;
        }
        if (viewModel.getCustomerList().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lồi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng thêm khách hàng");
            alert.showAndWait();
            return;
        }
        viewModel.addReservation(bookingNoteTextField.getText());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Đặt phòng thành công");
        alert.showAndWait();
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }

    private class ServiceListCell extends ListCell<Reservation_ServiceBookingDisplay> {
        private HBox root;
        private MFXCheckbox checkBox;
        private Label nameLabel;
        private Label priceLabel;
        private MFXTextField quantityField;

        private Reservation_ServiceBookingDisplay currentItem;

        public ServiceListCell() {
            super();
            checkBox = new MFXCheckbox();
            checkBox.setStyle("-mfx-main: #2356eb");
            Platform.runLater(() -> checkBox.autosize());

            nameLabel = new Label();
            nameLabel.setPrefWidth(200);
            nameLabel.setMinWidth(200);
            nameLabel.setMaxWidth(200);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            priceLabel = new Label();
            priceLabel.setStyle("-fx-font-size: 12px;");

            VBox infoBox = new VBox(2, nameLabel, priceLabel);

            quantityField = new MFXTextField("0");
            quantityField.setPrefWidth(40);
            quantityField.setMinWidth(40);
            quantityField.setMaxWidth(40);
            quantityField.getStyleClass().add("quantity-textfield");
            quantityField.setDisable(true);

            root = new HBox(10, checkBox, infoBox, new Region(), quantityField);
            HBox.setHgrow(root.getChildren().get(2), Priority.ALWAYS);
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new Insets(5));

            quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (currentItem != null) {
                    if (newVal.matches("\\d*")) {
                        int val = newVal.isEmpty() ? 0 : Integer.parseInt(newVal);
                        if (val < 0) val = 0;
                        currentItem.setQuantity(val);
                    } else {
                        quantityField.setText(oldVal);
                    }
                }
            });

            checkBox.selectedProperty().addListener((obs, oldSel, newSel) -> {
                if (currentItem != null) {
                    currentItem.setSelected(newSel);
                    quantityField.setDisable(!newSel);
                    if (!newSel) {
                        currentItem.setQuantity(0);
                        quantityField.setText("0");
                    }
                }
            });
        }

        @Override
        protected void updateItem(Reservation_ServiceBookingDisplay item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                currentItem = null;
                setGraphic(null);
            } else {
                currentItem = item;
                nameLabel.setText(item.getServiceName());
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                priceLabel.setText(nf.format(item.getPrice()) + " VNĐ");
                checkBox.setSelected(item.isSelected());
                quantityField.setText(String.valueOf(item.getQuantity()));
                quantityField.setDisable(!item.isSelected());

                setGraphic(root);
            }
        }
    }

    public void handleAddCustomer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/AddCustomerView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
            AddCustomerViewModel vm = new AddCustomerViewModel();
            vm.setParent(viewModel);
            AddCustomerController controller = loader.getController();
            controller.setViewModel(vm);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            customerBookingDisplays.clear();
            for (int i = 0; i < viewModel.getCustomerList().size(); i++) {
                customerBookingDisplays.add(new Booking_CustomerDisplay(viewModel.getCustomerList().get(i), i + 1));
            }
            customerTableView.setItems(customerBookingDisplays);
            customerTableView.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openCalendar(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingCalendarView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
            BookingCalendarViewModel vm = new BookingCalendarViewModel(viewModel.getRoom());
            vm.setParent(viewModel);
            BookingCalendarController controller = loader.getController();
            controller.setViewModel(vm);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            LocalDateStringConverter dateConverter = new LocalDateStringConverter(DateTimeFormatter.ofPattern("dd/MM/yyyy"), null);
            Bindings.bindBidirectional(checkoutDateTextfield.textProperty(), viewModel.getCheckOutDate(), dateConverter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
