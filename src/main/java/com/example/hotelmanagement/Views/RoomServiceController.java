package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.ServiceBookingDisplay;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Models.Service;
import com.example.hotelmanagement.ViewModels.RoomServiceViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // Import này
import java.text.NumberFormat;             // Import này
import java.util.Locale;                   // Import này

public class RoomServiceController {

    @FXML private MFXButton closeButton;
    @FXML private TextField txtRoomId;
    @FXML private MFXComboBox<Service> cbService;
    @FXML private Spinner<Integer> spnQuantity;
    @FXML private MFXButton btnAddService;
    @FXML private MFXButton btnClearForm;
    @FXML private Label lblTitle;

    @FXML private TableView<ServiceBookingDisplay> tvRoomServices;
    @FXML private TableColumn<ServiceBookingDisplay, Integer> colServiceBookingId;
    @FXML private TableColumn<ServiceBookingDisplay, String> colServiceName;
    @FXML private TableColumn<ServiceBookingDisplay, Integer> colQuantity;
    @FXML private TableColumn<ServiceBookingDisplay, LocalDate> colOrderDate;
    @FXML private TableColumn<ServiceBookingDisplay, String> colStatus;
    @FXML private TableColumn<ServiceBookingDisplay, Void> colActions;

    private RoomServiceViewModel viewModel;
    private Reservation currentReservation;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setReservation(Reservation reservation) {
        this.currentReservation = reservation;
    }

    @FXML
    public void initialize() {
        viewModel = new RoomServiceViewModel();
        currencyFormatter.setMinimumFractionDigits(0);

        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        });

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spnQuantity.setValueFactory(valueFactory);

        tvRoomServices.setItems(viewModel.getRoomServiceBookings());

        tvRoomServices.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        viewModel.selectedBookingItemProperty().set(newSelection);
                    }
                }
        );

        if (colServiceBookingId != null) colServiceBookingId.setCellValueFactory(data->data.getValue().serviceBookingIdProperty().asObject());
        colServiceName.setCellValueFactory(data->data.getValue().serviceNameProperty());
        colQuantity.setCellValueFactory(data->data.getValue().quantityProperty().asObject());
        if (colOrderDate != null) {
            colOrderDate.setCellValueFactory(data -> data.getValue().bookingDateProperty());
            colOrderDate.setCellFactory(column -> new TableCell<ServiceBookingDisplay, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.format(dateFormatter));
                    }
                }
            });
        }
        colStatus.setCellValueFactory(data->data.getValue().statusProperty());
    }

    public void setupBindingsAndData() {
        if (viewModel == null) {
            viewModel = new RoomServiceViewModel();
        }
        if (this.currentReservation != null) {
            viewModel.loadInitialData(this.currentReservation);
            if (lblTitle != null && this.currentReservation.getRoomID() != null && this.currentReservation.getRoomID().getRoomNumber() != null) {
                lblTitle.setText("Quản lý dịch vụ phòng " + this.currentReservation.getRoomID().getRoomNumber());
            } else if (lblTitle != null && this.currentReservation.getId() != null) {
                lblTitle.setText("Quản lý dịch vụ cho Đặt phòng #" + this.currentReservation.getId());
            } else if (lblTitle != null) {
                lblTitle.setText("Quản lý dịch vụ phòng");
            }
        } else {
            System.err.println("RoomServiceController: currentReservation is null. Không thể load dữ liệu dịch vụ.");
            if (lblTitle != null) {
                lblTitle.setText("Quản lý dịch vụ phòng (Không có đặt phòng)");
            }
            viewModel.loadInitialData(null); // ViewModel nên xử lý trường hợp này
        }

        bindControlsToViewModel();
        configureActionsColumn();
    }

    private void bindControlsToViewModel() {

        txtRoomId.textProperty().bind(viewModel.roomIdTextProperty());
        txtRoomId.setEditable(false);

        cbService.setItems(viewModel.getAvailableServices());
        cbService.valueProperty().bindBidirectional(viewModel.selectedServiceProperty());

        cbService.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                return service == null ? "" : service.getServiceName() + " - " + currencyFormatter.format(service.getPrice());
            }

            @Override
            public Service fromString(String string) {
                if (string == null || string.isEmpty()) return null;
                return viewModel.getAvailableServices().stream()
                        .filter(s -> {
                            String serviceDisplayName = s.getServiceName() + " - " + currencyFormatter.format(s.getPrice());
                            return serviceDisplayName.equals(string);
                        })
                        .findFirst().orElse(null);
            }
        });
        cbService.setPromptText("Chọn dịch vụ");
        if (spnQuantity.getValueFactory() == null) {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
            spnQuantity.setValueFactory(valueFactory);
        }
        spnQuantity.getValueFactory().valueProperty().bindBidirectional(viewModel.quantityProperty().asObject());

        btnAddService.setOnAction(event -> viewModel.addNewService());
        btnClearForm.setOnAction(event -> viewModel.clearForm());
    }
    private void configureActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<ServiceBookingDisplay, Void>() {
            private final Button btnProcess = new Button("Xử lý");
            private final Button btnCancel = new Button("Hủy");
            private final HBox pane = new HBox(btnProcess, btnCancel);

            {
                pane.setSpacing(5);
                pane.setAlignment(Pos.CENTER);

                btnProcess.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                btnCancel.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");

                btnProcess.setOnAction(event -> {
                    ServiceBookingDisplay item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        viewModel.selectedBookingItemProperty().set(item);
                        viewModel.processSelectedBooking();
                        tvRoomServices.refresh();
                    }
                });

                btnCancel.setOnAction(event -> {
                    ServiceBookingDisplay item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        viewModel.selectedBookingItemProperty().set(item);
                        viewModel.cancelSelectedBooking();
                        tvRoomServices.refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    btnProcess.disableProperty().unbind();
                    btnCancel.disableProperty().unbind();
                    btnProcess.setDisable(true);
                    btnCancel.setDisable(true);
                } else {
                    ServiceBookingDisplay bookingItem = (ServiceBookingDisplay) getTableRow().getItem();
                    btnProcess.disableProperty().bind(bookingItem.canProcessProperty().not());
                    btnCancel.disableProperty().bind(bookingItem.canCancelProperty().not());
                    setGraphic(pane);
                }
            }
        });
    }
}