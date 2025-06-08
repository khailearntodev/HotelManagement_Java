package com.example.hotelmanagement.Views;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import com.example.hotelmanagement.DAO.ReservationDAO;
import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DTO.Reservation_RoomDisplay;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Models.Roomtype;
import com.example.hotelmanagement.ViewModels.*;
import com.example.hotelmanagement.ViewModels.SelectRoomForCheckOutViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.net.URL;
import java.text.NumberFormat;
import java.util.stream.Collectors;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

public class ReservationController implements Initializable {
    @FXML private MFXTextField textfieldSearch;
    @FXML private MFXComboBox<Integer> comboBoxStatus;
    @FXML private MFXComboBox<Roomtype> comboBoxRoomType;
    @FXML private MFXButton searchButton;
    @FXML private AnchorPane anchorPane;
    @FXML private TableView<Reservation_RoomDisplay> bookingTable;
    @FXML private TableColumn<Reservation_RoomDisplay, Integer> colRoomNumber;
    @FXML private TableColumn<Reservation_RoomDisplay, String> colRoomType;
    @FXML private Pagination pagination;
    @FXML private TableColumn<Reservation_RoomDisplay, String> colCheckInOut;
    @FXML private TableColumn<Reservation_RoomDisplay, String> colAmount;
    @FXML private TableColumn<Reservation_RoomDisplay, Integer> colStatus;
    @FXML private TableColumn<Reservation_RoomDisplay, Void> colAction;
    @FXML private TableColumn<Reservation_RoomDisplay, String> colQuantity;

    private final static int ROWS_PER_PAGE = 15;
    @Setter
    @Getter
    private ReservationViewModel viewModel = new ReservationViewModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.loadFromModel();
        Platform.runLater(() -> anchorPane.requestFocus());

        comboBoxRoomType.setOnAction(e -> applyFilter());
        comboBoxStatus.setOnAction(e -> applyFilter());

        comboBoxRoomType.setItems(viewModel.getRoomTypes());
        comboBoxStatus.setItems(viewModel.getRoomStatus());

        comboBoxRoomType.setConverter(new StringConverter<>() {
            @Override
            public String toString(Roomtype roomtype) {
                return roomtype != null ? roomtype.getTypeName() : "";
            }

            @Override
            public Roomtype fromString(String string) {
                return null;
            }
        });

        comboBoxStatus.setConverter(new StringConverter<>() {

            @Override
            public String toString(Integer integer) {
                if (integer == null) return "Tất cả";
                switch (integer) {
                    case 0: return "Tất cả";
                    case 1: return "Còn trống";
                    case 2: return "Đang thuê";
                    case 3: return "Được đặt trước";
                    default: return "";
                }
            }

            @Override
            public Integer fromString(String s) {
                return 0;
            }
        });

        searchButton.setOnAction(e -> applyFilter());

        colRoomNumber.setCellValueFactory(cellData -> cellData.getValue().roomNumberProperty().asObject());
        colRoomType.setCellValueFactory(cell -> cell.getValue().roomTypeNameProperty());
        colCheckInOut.setCellValueFactory(cell -> cell.getValue().checkInOutDateProperty());

        NumberFormat Numformatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        colAmount.setCellValueFactory(cell ->
                Bindings.createStringBinding(
                        () -> Numformatter.format(cell.getValue().roomPriceProperty().get())  + " VNĐ/đêm",
                        cell.getValue().roomPriceProperty()
                )
        );
        colAmount.setCellFactory(column -> new TableCell<Reservation_RoomDisplay, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Reservation_RoomDisplay r = getTableView().getItems().get(getIndex());
                            Label label = new Label(item);

                            if (r.getStatus() != 2) {
                                label.setStyle("-fx-font-size: 13.5;");
                            } else {
                                label.setStyle("");
                            }
                            setGraphic(label);
                            setText(null);
                        }
                    }
                });


        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty().asObject());
        colStatus.setCellFactory(cell -> new TableCell<Reservation_RoomDisplay, Integer>() {
            private final Label label = new Label();
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    if (item.equals(2)) {
                        Label label = new Label("Đang thuê");
                        label.setPadding(new Insets(1,6,1,6));
                        label.setWrapText(false);
                        label.setAlignment(Pos.CENTER);
                        label.setMaxWidth(Region.USE_COMPUTED_SIZE);
                        label.setMinWidth(Region.USE_PREF_SIZE);
                        label.setMaxHeight(Region.USE_COMPUTED_SIZE);
                        label.setMinHeight(Region.USE_COMPUTED_SIZE);
                        setGraphic(label);
                        setText(null);
                        label.getStyleClass().setAll("checkout-label-table-view");
                    }
                    else if (item.equals(1)) {
                        Label label = new Label("Còn trống");
                        label.setPadding(new Insets(1,6,1,6));
                        label.setWrapText(false);
                        label.setAlignment(Pos.CENTER);
                        label.setMaxWidth(Region.USE_COMPUTED_SIZE);
                        label.setMinWidth(Region.USE_PREF_SIZE);
                        label.setMaxHeight(Region.USE_COMPUTED_SIZE);
                        label.setMinHeight(Region.USE_COMPUTED_SIZE);
                        setGraphic(label);
                        setText(null);
                        label.getStyleClass().setAll("booking-label-table-view");
                    }
                    else if (item.equals(3)) {
                        Label label = new Label("Được đặt trước");
                        label.setPadding(new Insets(1,6,1,6));
                        label.setWrapText(false);
                        label.setAlignment(Pos.CENTER);
                        label.setMaxWidth(Region.USE_COMPUTED_SIZE);
                        label.setMinWidth(Region.USE_PREF_SIZE);
                        label.setMaxHeight(Region.USE_COMPUTED_SIZE);
                        label.setMinHeight(Region.USE_COMPUTED_SIZE);
                        setGraphic(label);
                        setText(null);
                        label.getStyleClass().setAll("confirm-label-table-view");
                    }
                }
            }
        });

        colQuantity.setCellValueFactory(cell -> {
            var item = cell.getValue();
            var quantityProp = item.quantityProperty();

            if (quantityProp.getValue() == 0) {
                return new SimpleStringProperty("");
            }

            return Bindings.createStringBinding(
                    () -> {
                        Integer sl = quantityProp.getValue();
                        return (sl == null) ? "" : Numformatter.format(sl);
                    },
                    quantityProp
            );
        });

        colAction.setCellFactory(col -> new TableCell<Reservation_RoomDisplay, Void>() {
            private Button btn;
            private Button subbtn;
            private HBox buttonContainer;

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                setText(null);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    return;
                }
                Reservation_RoomDisplay p = getTableView().getItems().get(getIndex());
                p.statusProperty().addListener((obs, oldVal, newVal) -> {
                    bookingTable.refresh();
                });

                Room r = new RoomDAO().findById(p.getId());

                btn = new Button();
                subbtn = new Button();
                buttonContainer = new HBox();

                buttonContainer.setAlignment(Pos.CENTER);
                buttonContainer.setPrefWidth(Double.MAX_VALUE);

                switch (p.getStatus()) {
                    case 1:
                        setupBookingButton(r);
                        buttonContainer.getChildren().add(btn);
                        break;

                    case 2:
                        setupCheckoutButton(p);
                        setupSubButton(p);
                        buttonContainer.getChildren().addAll(btn, subbtn);
                        break;

                    case 3:
                        setupConfirmButton(r);
                        buttonContainer.getChildren().add(btn);
                        break;
                }

                setGraphic(buttonContainer);
                setAlignment(Pos.CENTER);
            }

            private void setupBookingButton(Room p) {
                btn.setOnAction(ae -> {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingView.fxml"));
                        Parent root = fxmlLoader.load();
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
                        BookingViewModel vm = new BookingViewModel(p, true);
                        vm.setParent(viewModel);
                        BookingController controller = fxmlLoader.getController();
                        controller.setViewModel(vm);
                        Stage stage = new Stage();
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(scene);
                        stage.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                configureButton(btn, 112, "Đặt phòng");
                btn.getStyleClass().add("booking-button-table-view");
            }

            private void setupCheckoutButton(Reservation_RoomDisplay p) {
                btn.setOnAction(e -> {
                    System.out.println("Click phòng: " + p.getRoomNumber());
                });

                configureButton(btn, 87, "Thanh toán");
                btn.getStyleClass().add("checkout-button-table-view");
            }

            private void setupConfirmButton(Room r) {
                btn.setOnAction(e -> {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/ConfirmBookingCodeView.fxml"));
                        Parent root = fxmlLoader.load();
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
                        ConfirmBookingCodeViewModel vm = new ConfirmBookingCodeViewModel(r);
                        vm.setParent(viewModel);
                        ConfirmBookingCodeController controller = fxmlLoader.getController();
                        controller.setViewModel(vm);
                        Stage stage = new Stage();
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(scene);
                        stage.showAndWait();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });

                configureButton(btn, 112, "Nhận phòng");
                btn.getStyleClass().add("confirm-button-table-view");
            }

            private void setupSubButton(Reservation_RoomDisplay p) {
                MFXFontIcon icon = new MFXFontIcon("mfx-chevron-down", 10);
                subbtn.setPrefWidth(25);
                subbtn.setMinWidth(25);
                subbtn.setMaxWidth(25);
                subbtn.setPadding(Insets.EMPTY);
                subbtn.setPrefHeight(25);
                subbtn.setMinHeight(25);
                subbtn.setMaxHeight(25);
                subbtn.setAlignment(Pos.CENTER);
                subbtn.setGraphic(icon);
                subbtn.getStyleClass().add("checkout-chevron-button-table-view");

                subbtn.setOnAction(e -> {
                    Popup popup = new Popup();
                    popup.setAutoHide(true);
                    VBox vbox = new VBox(10);
                    vbox.setStyle("""
                -fx-background-color: white;
                -fx-border-color: gray;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-border-width: 1;
                -fx-cursor: hand;
            """);

                    MFXButton btnDatDichVu = new MFXButton("Đặt dịch vụ");
                    MFXButton btnXemGhiChu = new MFXButton("Xem ghi chú");
                    MFXButton btnXemPhieuThue = new MFXButton("Xem phiếu thuê");

                    btnDatDichVu.setPrefWidth(120);
                    btnXemGhiChu.setPrefWidth(120);
                    btnXemPhieuThue.setPrefWidth(120);

                    btnDatDichVu.getStyleClass().add("popup-button");
                    btnXemGhiChu.getStyleClass().add("popup-button");
                    btnXemPhieuThue.getStyleClass().add("popup-button");

                    vbox.getChildren().addAll(btnDatDichVu, btnXemGhiChu, btnXemPhieuThue);

                    btnDatDichVu.setOnAction(ev -> {
                        int roomID = p.getId();
                        Room room = new RoomDAO().findById(roomID);
                        Reservation reservation = room.getReservations().stream().filter(r -> r.getInvoiceID() == null).findFirst().orElse(null);
                        if (reservation != null) {
                            System.out.println(reservation.getId().toString());
                        }
                        popup.hide();
                    });

                    btnXemGhiChu.setOnAction(ev -> {
                        try {
                            FXMLLoader bookingNotefxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingNoteView.fxml"));
                            Parent root = bookingNotefxmlLoader.load();
                            BookingNoteViewModel vm = new BookingNoteViewModel(new RoomDAO().findById(p.getId()));
                            vm.setParent(viewModel);
                            BookingNoteController controller = bookingNotefxmlLoader.getController();
                            controller.setViewModel(vm);
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());

                            Stage stage = new Stage();
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setScene(scene);
                            stage.showAndWait();
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                        popup.hide();
                    });

                    btnXemPhieuThue.setOnAction(ev -> {
                        try {
                            FXMLLoader bookingNotefxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/ReservationFormView.fxml"));
                            Parent root = bookingNotefxmlLoader.load();
                            int roomID = p.getId();
                            Reservation reservation = new ReservationDAO().getAll().stream().filter(r -> r.getRoomID().getId() == roomID && r.getInvoiceID() == null).findFirst().orElse(null);
                            ReservationFormViewModel vm = new ReservationFormViewModel(reservation);
                            vm.setParent(viewModel);
                            ReservationFormController controller = bookingNotefxmlLoader.getController();
                            controller.setViewModel(vm);
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());

                            Stage stage = new Stage();
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setScene(scene);
                            stage.showAndWait();
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                        popup.hide();
                    });

                    popup.getContent().add(vbox);

                    Node source = (Node) e.getSource();
                    Bounds bounds = source.localToScreen(source.getBoundsInLocal());
                    popup.show(source, bounds.getMinX(), bounds.getMaxY());
                });
            }

            private void configureButton(Button button, int width, String text) {
                button.setPrefWidth(width);
                button.setMinWidth(width);
                button.setMaxWidth(width);
                button.setPadding(Insets.EMPTY);
                button.setPrefHeight(25);
                button.setMinHeight(25);
                button.setMaxHeight(25);
                button.setAlignment(Pos.CENTER);
                button.setText(text);
            }
        });

        String[] statusOptions = {"Còn trống", "Đang thuê", "Được đặt trước"};

        int pageCount = (int) Math.ceil(viewModel.getRooms().size() * 1.0 / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(pageIndex -> {
            myUpdateTableView(pageIndex);
            return new AnchorPane();
        });

        myUpdateTableView(0);
    }

    public void applyFilter() {
        Roomtype selectedType = comboBoxRoomType.getSelectionModel().getSelectedItem();
        Integer selectedStatus = comboBoxStatus.getSelectionModel().getSelectedItem();
        Set<Integer> roomNumbers = Arrays.stream(textfieldSearch.getText().split(",\\s*"))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && s.matches("\\d+"))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        viewModel.getRooms().setPredicate(room -> {
            boolean matchesType = (selectedType == null || selectedType.getId() == -1)
                    || room.getRoomType().getId().equals(selectedType.getId());
            boolean matchesStatus = (selectedStatus == null || selectedStatus == 0)
                    || room.getStatus() == selectedStatus;
            boolean matchesRoomNumber = roomNumbers.isEmpty()
                    || roomNumbers.contains(room.getRoomNumber());
            return matchesType && matchesStatus && matchesRoomNumber;
        });

        updatePagination();
        myUpdateTableView(0);
        pagination.setCurrentPageIndex(0);
    }

    private void updatePagination() {
        int totalItems = viewModel.getRooms().size();
        int pageCount = (int) Math.ceil(totalItems * 1.0 / ROWS_PER_PAGE);
        pageCount = Math.max(1, pageCount);

        System.out.println("Updating pagination: total=" + totalItems + ", pages=" + pageCount);
        pagination.setPageCount(pageCount);
    }

    private void myUpdateTableView(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, viewModel.getRooms().size());
        ObservableList<Reservation_RoomDisplay> pageData = FXCollections.observableArrayList();
        pageData.addAll(viewModel.getRooms().subList(fromIndex, toIndex));
        bookingTable.setItems(pageData);
        bookingTable.refresh();
    }

    public void showBookingInAdvanceView(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingInAdvanceView.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
            BookingInAdvanceViewModel vm = new BookingInAdvanceViewModel();
            vm.setParent(viewModel);
            BookingInAdvanceController controller = fxmlLoader.getController();
            controller.setViewModel(vm);
            controller.initializeRoomDisplays();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCancelBookingView(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/CancelBookingView.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
            CancelBookingViewModel vm = new CancelBookingViewModel();
            vm.setParent(viewModel);
            CancelBookingController controller = fxmlLoader.getController();
            controller.setViewModel(vm);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCheckOutView(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/SelectRoomForCheckOutView.fxml"));
            Parent root = fxmlLoader.load();
            SelectRoomForCheckOutViewModel vm = new SelectRoomForCheckOutViewModel();
            vm.setParent(viewModel);
            SelectRoomForCheckOutController controller = fxmlLoader.getController();
            controller.setViewModel(vm);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
            System.out.println(scene.getStylesheets());
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}