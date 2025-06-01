package com.example.hotelmanagement.Views;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DTO.RoomReservationDisplay;
import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.Models.Roomtype;
import com.example.hotelmanagement.ViewModels.BookingViewModel;
import com.example.hotelmanagement.ViewModels.ReservationViewModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

public class ReservationController implements Initializable {
    @FXML private MFXComboBox<Roomtype> comboBoxRoomType;
    @FXML private MFXButton searchButton;
    @FXML private AnchorPane anchorPane;
    @FXML private TableView<RoomReservationDisplay> bookingTable;
    @FXML private TableColumn<RoomReservationDisplay, Integer> colRoomNumber;
    @FXML private TableColumn<RoomReservationDisplay, String> colRoomType;
    @FXML private Pagination pagination;
    @FXML private TableColumn<RoomReservationDisplay, String> colCheckInOut;
    @FXML private TableColumn<RoomReservationDisplay, String> colAmount;
    @FXML private TableColumn<RoomReservationDisplay, Integer> colStatus;
    @FXML private TableColumn<RoomReservationDisplay, Void> colAction;
    @FXML private TableColumn<RoomReservationDisplay, String> colQuantity;

    private final static int ROWS_PER_PAGE = 15;
    private ObservableList<RoomReservationDisplay> rooms = FXCollections.observableArrayList();
    @Setter
    @Getter
    private ReservationViewModel viewModel = new ReservationViewModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.loadFromModel();
        this.rooms = viewModel.getRooms();
        Platform.runLater(() -> anchorPane.requestFocus());

        comboBoxRoomType.setItems(viewModel.getRoomTypes());
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

        colRoomNumber.setCellValueFactory(cellData -> cellData.getValue().roomNumberProperty().asObject());
        colRoomType.setCellValueFactory(cell -> cell.getValue().roomTypeNameProperty());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colCheckInOut.setCellValueFactory(cell -> cell.getValue().checkInOutDateProperty());

        NumberFormat Numformatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        colAmount.setCellValueFactory(cell ->
                Bindings.createStringBinding(
                        () -> Numformatter.format(cell.getValue().roomTypePriceProperty().get())  + " VNĐ/đêm",
                        cell.getValue().roomTypePriceProperty()
                )
        );

        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty().asObject());
        colStatus.setCellFactory(cell -> new TableCell<RoomReservationDisplay, Integer>() {
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

        colAction.setCellFactory(col -> new TableCell<RoomReservationDisplay, Void>() {
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
                RoomReservationDisplay p = getTableView().getItems().get(getIndex());
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
                        setupConfirmButton(p);
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
                        BookingViewModel vm = new BookingViewModel(p);
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

            private void setupCheckoutButton(RoomReservationDisplay p) {
                btn.setOnAction(e -> {
                    System.out.println("Click phòng: " + p.getRoomNumber());
                });

                configureButton(btn, 87, "Thanh toán");
                btn.getStyleClass().add("checkout-button-table-view");
            }

            private void setupConfirmButton(RoomReservationDisplay p) {
                btn.setOnAction(e -> {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/ConfirmBookingCodeView.fxml"));
                        Parent root = fxmlLoader.load();
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
                });

                configureButton(btn, 112, "Nhận phòng");
                btn.getStyleClass().add("confirm-button-table-view");
            }

            private void setupSubButton(RoomReservationDisplay p) {
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

                    MFXButton btn1 = new MFXButton("Đặt dịch vụ");
                    MFXButton btnXemGhiChu = new MFXButton("Xem ghi chú");
                    MFXButton btn3 = new MFXButton("Xem phiếu thuê");

                    btn1.setPrefWidth(120);
                    btnXemGhiChu.setPrefWidth(120);
                    btn3.setPrefWidth(120);

                    btn1.getStyleClass().add("popup-button");
                    btnXemGhiChu.getStyleClass().add("popup-button");
                    btn3.getStyleClass().add("popup-button");

                    vbox.getChildren().addAll(btn1, btnXemGhiChu, btn3);

                    btn1.setOnAction(ev -> {
                        System.out.println("Đặt dịch vụ phòng: " + p.getRoomNumber());
                        popup.hide();
                    });

                    btnXemGhiChu.setOnAction(ev -> {
                        try {
                            FXMLLoader bookingNotefxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingNoteView.fxml"));
                            Parent root = bookingNotefxmlLoader.load();
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

                    btn3.setOnAction(ev -> {
                        System.out.println("Xem phiếu thuê phòng: " + p.getRoomNumber());
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

        int pageCount = (int) Math.ceil(rooms.size() * 1.0 / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(pageIndex -> {
            myUpdateTableView(pageIndex);
            return new AnchorPane();
        });

        myUpdateTableView(0);
    }

    private void myUpdateTableView(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, rooms.size());
        ObservableList<RoomReservationDisplay> pageData = FXCollections.observableArrayList();
        pageData.addAll(rooms.subList(fromIndex, toIndex));
        bookingTable.setItems(pageData);
        bookingTable.refresh();
    }

    public void showBookingInAdvanceView(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingInAdvanceView.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());

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