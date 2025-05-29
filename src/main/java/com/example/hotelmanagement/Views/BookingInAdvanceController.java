package com.example.hotelmanagement.Views;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class BookingInAdvanceController implements Initializable {
    @FXML private MFXDatePicker checkOutPicker;
    @FXML private MFXDatePicker checkInPicker;
    @FXML private TilePane tilePane;
    @FXML private VBox BookingInAdvanceVBox;

    VBox selectedItem = null;

    private void chonItem(VBox item) {
        /*if (selectedItem != null) {
            selectedItem.getStyleClass().remove("room-container-selected");
        }
        selectedItem = item;
        selectedItem.getStyleClass().add("room-container-selected");
        var p = (ReservationController.PhongTest) selectedItem.getUserData();
        System.out.println(p.getSoPhong());*/
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*ObservableList<ReservationController.PhongTest> danhSachPhong = FXCollections.observableArrayList(
                new ReservationController.PhongTest("101", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 800000, "Trống", 1),
                new ReservationController.PhongTest("102", "Standard", LocalDateTime.now(), LocalDateTime.now(), 500000, "Trống", 1),
                new ReservationController.PhongTest("103", "Suite", LocalDateTime.now(), LocalDateTime.now(), 1200000, "Trống", 1),
                new ReservationController.PhongTest("104", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 750000, "Trống", 1),
                new ReservationController.PhongTest("105", "Standard", LocalDateTime.now(), LocalDateTime.now(), 480000, "Trống", 1),
                new ReservationController.PhongTest("101", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 800000, "Trống", 1),
                new ReservationController.PhongTest("102", "Standard", LocalDateTime.now(), LocalDateTime.now(), 500000, "Trống", 1),
                new ReservationController.PhongTest("103", "Suite", LocalDateTime.now(), LocalDateTime.now(), 1200000, "Trống", 1),
                new ReservationController.PhongTest("104", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 750000, "Trống", 1),
                new ReservationController.PhongTest("105", "Standard", LocalDateTime.now(), LocalDateTime.now(), 480000, "Trống", 1),
                new ReservationController.PhongTest("101", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 800000, "Trống", 1),
                new ReservationController.PhongTest("102", "Standard", LocalDateTime.now(), LocalDateTime.now(), 500000, "Trống", 1),
                new ReservationController.PhongTest("103", "Suite", LocalDateTime.now(), LocalDateTime.now(), 1200000, "Trống", 1),
                new ReservationController.PhongTest("104", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 750000, "Trống", 1),
                new ReservationController.PhongTest("105", "Standard", LocalDateTime.now(), LocalDateTime.now(), 480000, "Trống", 1),
                new ReservationController.PhongTest("101", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 800000, "Trống", 1),
                new ReservationController.PhongTest("102", "Standard", LocalDateTime.now(), LocalDateTime.now(), 500000, "Trống", 1),
                new ReservationController.PhongTest("103", "Suite", LocalDateTime.now(), LocalDateTime.now(), 1200000, "Trống", 1),
                new ReservationController.PhongTest("104", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 750000, "Trống", 1),
                new ReservationController.PhongTest("105", "Standard", LocalDateTime.now(), LocalDateTime.now(), 480000, "Trống", 1),
                new ReservationController.PhongTest("101", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 800000, "Trống", 1),
                new ReservationController.PhongTest("102", "Standard", LocalDateTime.now(), LocalDateTime.now(), 500000, "Trống", 1),
                new ReservationController.PhongTest("103", "Suite", LocalDateTime.now(), LocalDateTime.now(), 1200000, "Trống", 1),
                new ReservationController.PhongTest("104", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 750000, "Trống", 1),
                new ReservationController.PhongTest("105", "Standard", LocalDateTime.now(), LocalDateTime.now(), 480000, "Trống", 1),
                new ReservationController.PhongTest("101", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 800000, "Trống", 1),
                new ReservationController.PhongTest("102", "Standard", LocalDateTime.now(), LocalDateTime.now(), 500000, "Trống", 1),
                new ReservationController.PhongTest("103", "Suite", LocalDateTime.now(), LocalDateTime.now(), 1200000, "Trống", 1),
                new ReservationController.PhongTest("104", "Deluxe", LocalDateTime.now(), LocalDateTime.now(), 750000, "Trống", 1),
                new ReservationController.PhongTest("105", "Standard", LocalDateTime.now(), LocalDateTime.now(), 480000, "Trống", 1)
        );*/

        Locale vietnameseLocale = new Locale("vi", "VN");
        Locale.setDefault(vietnameseLocale);

        Platform.runLater(() -> BookingInAdvanceVBox.requestFocus());

        setupMFXDatePicker(checkOutPicker);
        setupMFXDatePicker(checkInPicker);

        /*for (ReservationController.PhongTest phong : danhSachPhong) {
            VBox container = new VBox();
            container.setCursor(Cursor.HAND);
            container.setPrefSize(235, 85);
            container.getStyleClass().add("room-container-booking-in-advance");

            HBox header = new HBox();
            header.setStyle("-fx-background-color: #2356eb; -fx-background-radius: 5 5 0 0;");
            header.setMinHeight(25);
            Text headerText = new Text("Phòng " + phong.getSoPhong());
            headerText.setFill(Color.WHITE);
            headerText.setFont(Font.font(null, FontWeight.MEDIUM, 17));
            header.setAlignment(Pos.CENTER);
            header.getChildren().add(headerText);

            VBox info = new VBox(2);
            Text loaiPhong = new Text("Loại phòng: " + phong.getLoaiPhong());
            loaiPhong.setFont(Font.font(null, FontWeight.MEDIUM, 15));

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            Text gia = new Text("Giá: " + formatter.format(phong.getDonGia()) + " đồng/đêm");
            gia.setFont(Font.font(15));

            info.getChildren().addAll(loaiPhong, gia);
            info.setStyle("-fx-padding: 5 10 5 10;");

            container.getChildren().addAll(header, info);
            container.setOnMouseClicked(event -> chonItem(container));
            container.setUserData(phong);
            tilePane.getChildren().add(container);
        }*/
    }

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) BookingInAdvanceVBox.getScene().getWindow();
        stage.close();
    }

    private void setupMFXDatePicker(MFXDatePicker datePicker) {
        if (datePicker != null) {
            Locale vietnameseLocale = new Locale("vi", "VN");

            datePicker.setConverterSupplier(() -> new StringConverter<LocalDate>() {
                @Override
                public String toString(LocalDate date) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", vietnameseLocale);
                    return date != null ? date.format(formatter) : "";
                }

                @Override
                public LocalDate fromString(String string) {
                    if (string != null && !string.trim().isEmpty()) {
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", vietnameseLocale);
                            return LocalDate.parse(string, formatter);
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    return null;
                }
            });

            try {
                Method setLocaleMethod = datePicker.getClass().getMethod("setLocale", Locale.class);
                setLocaleMethod.invoke(datePicker, vietnameseLocale);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showBIAInvoiceView(MouseEvent mouseEvent) {
        try {
            FXMLLoader bookingNotefxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingInAdvanceInvoiceView.fxml"));
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
    }
}
