package com.example.hotelmanagement.Views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SelectRoomForCheckOutViewModel implements Initializable {
    @FXML private TilePane tilePane;
    @FXML private VBox selectRoomForCheckOutVBox;

    List<ReservationController.PhongTest> selectedRooms = new ArrayList<>();
    List<VBox> selectedContainers = new ArrayList<>();

    private void seletectItem(VBox item) {
        if (!selectedContainers.contains(item)) {
            selectedContainers.add(item);
            selectedRooms.add((ReservationController.PhongTest) item.getUserData());
            item.getStyleClass().add("room-container-selected");
        } else {
            selectedContainers.remove(item);
            selectedRooms.remove((ReservationController.PhongTest) item.getUserData());
            item.getStyleClass().remove("room-container-selected");
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<ReservationController.PhongTest> danhSachPhong = FXCollections.observableArrayList(
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
        );

        Locale vietnameseLocale = new Locale("vi", "VN");
        Locale.setDefault(vietnameseLocale);

        Platform.runLater(() -> selectRoomForCheckOutVBox.requestFocus());

        for (ReservationController.PhongTest phong : danhSachPhong) {
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
            container.setUserData(phong);
            container.setOnMouseClicked(mouseEvent -> seletectItem(container));
            tilePane.getChildren().add(container);
        }
    }

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) selectRoomForCheckOutVBox.getScene().getWindow();
        stage.close();
    }

    public void nextCheckout(MouseEvent mouseEvent) {
        System.out.println(selectedRooms.stream().map(ReservationController.PhongTest::getSoPhong).collect(Collectors.toList()));
    }
}
