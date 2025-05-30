package com.example.hotelmanagement.Views;
import java.io.IOException;
import java.util.Locale;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import java.time.LocalDateTime;
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

import static io.github.palexdev.materialfx.utils.RandomUtils.random;

public class ReservationController implements Initializable {
    @FXML private MFXButton searchButton;
    @FXML private AnchorPane anchorPane;
    @FXML private TableView<PhongTest> bookingTable;
    @FXML private TableColumn<PhongTest, String> colRoomNumber;
    @FXML private TableColumn<PhongTest, String> colRoomType;
    @FXML private Pagination pagination;
    @FXML private TableColumn<PhongTest, String> colCheckInOut;
    @FXML private TableColumn<PhongTest, String> colAmount;
    @FXML private TableColumn<PhongTest, String> colStatus;
    @FXML private TableColumn<PhongTest, Void> colAction;
    @FXML private TableColumn<PhongTest, String> colQuantity;

    private final static int ROWS_PER_PAGE = 15;
    private ObservableList<PhongTest> allData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> anchorPane.requestFocus());
        colRoomNumber.setCellValueFactory(cell -> cell.getValue().tenPhongProperty());
        colRoomType.setCellValueFactory(cell -> cell.getValue().loaiPhongProperty());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colCheckInOut.setCellValueFactory(cell ->
                Bindings.createStringBinding(() -> {
                            LocalDateTime checkInDate = cell.getValue().thoigianCheckInProperty().get();
                            LocalDateTime checkOutDate = cell.getValue().thoigianCheckOutProperty().get();
                            return (checkInDate != null && checkOutDate != null) ? checkInDate.format(formatter) + " - " + checkOutDate.format(formatter) : "";
                            },
                        cell.getValue().thoigianCheckInProperty()
                )
        );

        NumberFormat Numformatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        colAmount.setCellValueFactory(cell ->
                Bindings.createStringBinding(
                        () -> Numformatter.format(cell.getValue().giaThueProperty().get())  + " VNĐ/đêm",
                        cell.getValue().giaThueProperty()
                )
        );

        colStatus.setCellValueFactory(cell -> cell.getValue().trangThaiProperty());
        colStatus.setCellFactory(cell -> new TableCell<PhongTest, String>() {
            private final Label label = new Label();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    if (item.equals("Đang thuê")) {
                        Label label = new Label(item);
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
                    else if (item.equals("Còn trống")) {
                        Label label = new Label(item);
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
                    else if (item.equals("Được đặt trước")) {
                        Label label = new Label(item);
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

        colQuantity.setCellValueFactory(cell ->
                Bindings.createStringBinding(
                        () -> Numformatter.format(cell.getValue().slProperty().get()),
                        cell.getValue().slProperty()
                )
        );

        colAction.setCellFactory(col -> new TableCell<PhongTest, Void>() {
            private final Button btn = new Button();
            private final Button subbtn = new Button();

            {
                subbtn.setOnAction(e -> {
                    PhongTest p = getTableView().getItems().get(getIndex());
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
                        System.out.println("Đặt dịch vụ phòng: " + p.getTenPhong());
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
                    });
                    btn3.setOnAction(ev -> {
                        System.out.println("Xem phiếu thuê phòng: " + p.getTenPhong());
                        popup.hide();
                    });

                    popup.getContent().add(vbox);

                    Node source = (Node) e.getSource();
                    Bounds bounds = source.localToScreen(source.getBoundsInLocal());

                    double x = bounds.getMinX();
                    double y = bounds.getMaxY();

                    popup.show(source, x, y);
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    PhongTest p = getTableView().getItems().get(getIndex());
                    if ("Còn trống".equals(p.getTrangThai())) {
                        btn.setOnAction(ae -> {
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingView.fxml"));
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
                        });

                        btn.setPrefWidth(112);
                        btn.setMinWidth(112);
                        btn.setMaxWidth(112);
                        btn.setPadding(Insets.EMPTY);
                        btn.setPrefHeight(25);
                        btn.setMinHeight(25);
                        btn.setMaxHeight(25);
                        btn.setAlignment(Pos.CENTER);
                        btn.setText("Đặt phòng");
                        btn.getStyleClass().setAll("booking-button-table-view");
                    } else if ("Đang thuê".equals(p.getTrangThai())) {
                        btn.setOnAction(e -> {
                            System.out.println("Click phòng: " + p.getTenPhong());
                        });
                        btn.setPrefWidth(87);
                        btn.setMinWidth(87);
                        btn.setMaxWidth(87);
                        btn.setPadding(Insets.EMPTY);
                        btn.setPrefHeight(25);
                        btn.setMinHeight(25);
                        btn.setMaxHeight(25);
                        btn.setAlignment(Pos.CENTER);
                        btn.setText("Thanh toán");
                        btn.getStyleClass().setAll("checkout-button-table-view");
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
                        subbtn.getStyleClass().setAll("checkout-chevron-button-table-view");
                    } else if ("Được đặt trước".equals(p.getTrangThai())) {
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

                        btn.setPrefWidth(112);
                        btn.setMinWidth(112);
                        btn.setMaxWidth(112);
                        btn.setPadding(Insets.EMPTY);
                        btn.setPrefHeight(25);
                        btn.setMinHeight(25);
                        btn.setMaxHeight(25);
                        btn.setAlignment(Pos.CENTER);
                        btn.setText("Nhận phòng");
                        btn.getStyleClass().setAll("confirm-button-table-view");
                    }
                    if ("Đang thuê".equals(p.getTrangThai())) {
                        HBox box = new HBox(btn, subbtn);
                        box.setAlignment(Pos.CENTER);
                        box.setPrefWidth(Double.MAX_VALUE);
                        setGraphic(box);
                        setAlignment(Pos.CENTER);
                    } else {
                        HBox box = new HBox(btn);
                        box.setAlignment(Pos.CENTER);
                        box.setPrefWidth(Double.MAX_VALUE);
                        setGraphic(box);
                        setAlignment(Pos.CENTER);
                    }
                }
            }
        });

        String[] statusOptions = {"Còn trống", "Đang thuê", "Được đặt trước"};

        for (int i = 1; i <= 100; i++) {
            allData.add(new PhongTest(String.valueOf(i), "Phòng cơ bản VIP đặc biệt", LocalDateTime.now(), LocalDateTime.now().plusDays(i), 150000*i, statusOptions[random.nextInt(statusOptions.length)], random.nextInt(4) + 1));
        }

        int pageCount = (int) Math.ceil(allData.size() * 1.0 / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(pageIndex -> {
            updateTableView(pageIndex);
            return new AnchorPane();
        });

        updateTableView(0);
    }

    private void updateTableView(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, allData.size());
        bookingTable.setItems(FXCollections.observableArrayList(allData.subList(fromIndex, toIndex)));
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

    public static class PhongTest {
        private final StringProperty tenPhong;
        private final StringProperty loaiPhong;
        private final ObjectProperty<LocalDateTime> thoigianCheckIn;
        private final ObjectProperty<LocalDateTime> thoigianCheckOut;
        private final ObjectProperty<Integer> giaThue;
        private final StringProperty trangThai;
        private final ObjectProperty<Integer> sl;

        public PhongTest(String ten, String loai, LocalDateTime thoigianCheckIn, LocalDateTime thoigianCheckOut, Integer giaThue, String trangThai, Integer sl) {
            this.tenPhong = new SimpleStringProperty(ten);
            this.loaiPhong = new SimpleStringProperty(loai);
            this.thoigianCheckIn = new SimpleObjectProperty<>(thoigianCheckIn);
            this.thoigianCheckOut = new SimpleObjectProperty<>(thoigianCheckOut);
            this.giaThue = new SimpleObjectProperty<>(giaThue);
            this.trangThai = new SimpleStringProperty(trangThai);
            this.sl = new SimpleObjectProperty<>(sl);
        }

        public StringProperty tenPhongProperty() {
            return tenPhong;
        }

        public StringProperty loaiPhongProperty() {
            return loaiPhong;
        }

        public ObjectProperty<LocalDateTime> thoigianCheckInProperty() {
            return thoigianCheckIn;
        }

        public ObjectProperty<LocalDateTime> thoigianCheckOutProperty() {
            return thoigianCheckOut;
        }

        public ObjectProperty<Integer> giaThueProperty() {
            return giaThue;
        }

        public StringProperty trangThaiProperty() {
            return trangThai;
        }

        public String getTenPhong() {
            return tenPhong.get();
        }

        public String getTrangThai() {
            return trangThai.get();
        }

        public ObjectProperty<Integer> slProperty() {
            return sl;
        }

        public String getSoPhong() {
            return tenPhong.get();
        }

        public String getLoaiPhong() {
            return loaiPhong.get();
        }

        public double getDonGia() {
            return giaThue.get();
        }
    }
}