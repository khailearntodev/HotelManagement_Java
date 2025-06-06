package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DTO.SelectRoom_RoomDisplay;
import com.example.hotelmanagement.Models.Reservation;
import com.example.hotelmanagement.ViewModels.SelectRoomForCheckOutViewModel;
import javafx.application.Platform;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SelectRoomForCheckOutController implements Initializable {
    @FXML private TilePane tilePane;
    @FXML private VBox selectRoomForCheckOutVBox;

    SelectRoomForCheckOutViewModel viewModel;

    public void setViewModel(SelectRoomForCheckOutViewModel viewModel) {
        this.viewModel = viewModel;
        bindData();
    }

    List<VBox> selectedContainers = new ArrayList<>();

    private void seletectItem(VBox item) {
        if (!selectedContainers.contains(item)) {
            selectedContainers.add(item);
            viewModel.getSelectedRooms().add(((SelectRoom_RoomDisplay) item.getUserData()).getReservation());
            item.getStyleClass().add("room-container-selected");
        } else {
            selectedContainers.remove(item);
            viewModel.getSelectedRooms().remove(((SelectRoom_RoomDisplay) item.getUserData()).getReservation());
            item.getStyleClass().remove("room-container-selected");
        }
    }

    public void bindData() {
        Locale vietnameseLocale = new Locale("vi", "VN");
        Locale.setDefault(vietnameseLocale);
        for (var phong : viewModel.getRooms()) {
            VBox container = new VBox();
            container.setCursor(Cursor.HAND);
            container.setPrefSize(235, 85);
            container.getStyleClass().add("room-container-booking-in-advance");

            HBox header = new HBox();
            header.setStyle("-fx-background-color: #2356eb; -fx-background-radius: 5 5 0 0;");
            header.setMinHeight(25);
            Text headerText = new Text("Phòng " + phong.getRoomNumber());
            headerText.setFill(Color.WHITE);
            headerText.setFont(Font.font(null, FontWeight.MEDIUM, 17));
            header.setAlignment(Pos.CENTER);
            header.getChildren().add(headerText);

            VBox info = new VBox(2);
            Text loaiPhong = new Text("Loại phòng: " + phong.getRoomTypeName());
            loaiPhong.setFont(Font.font(null, FontWeight.MEDIUM, 15));

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            Text gia = new Text("Giá: " + formatter.format(phong.getPrice()) + " đồng/đêm");
            gia.setFont(Font.font(15));

            info.getChildren().addAll(loaiPhong, gia);
            info.setStyle("-fx-padding: 5 10 5 10;");

            container.getChildren().addAll(header, info);
            container.setUserData(phong);
            container.setOnMouseClicked(mouseEvent -> seletectItem(container));
            tilePane.getChildren().add(container);
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> selectRoomForCheckOutVBox.requestFocus());
    }

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) selectRoomForCheckOutVBox.getScene().getWindow();
        stage.close();
    }

    public void nextCheckout(MouseEvent mouseEvent) {
        System.out.println(viewModel.getSelectedRooms().stream().map(Reservation::getBasePrice).collect(Collectors.toList()));
    }
}
