package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DAO.EmployeeDAO;
import com.example.hotelmanagement.DAO.InvoiceDAO;
import com.example.hotelmanagement.DAO.ReservationDAO;
import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DTO.SelectRoom_RoomDisplay;
import com.example.hotelmanagement.Models.*;
import com.example.hotelmanagement.ViewModels.InvoiceDetailViewModel;
import com.example.hotelmanagement.ViewModels.SelectRoomForCheckOutViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

import java.io.IOException;
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
    @FXML
    public void nextCheckout() {
        if (viewModel.getSelectedRooms().isEmpty()) {
            System.out.println("Vui lòng chọn ít nhất một phòng để thanh toán.");
            // Có thể thêm Alert ở đây để thông báo cho người dùng
            return;
        }

        try {
            List<Reservation> reservations = viewModel.getSelectedRooms();
            ReservationDAO reservationDAO = new ReservationDAO();
            for (Reservation res : reservations) {
                res.setCheckOutDate(java.time.Instant.now());
                reservationDAO.update(res);
            }
            InvoiceDetailViewModel invoiceDetailVM = new InvoiceDetailViewModel(reservations);
            invoiceDetailVM.getInvoice().get().setInvoiceType(2);
            invoiceDetailVM.getInvoice().get().setPaymentStatus("Chưa thanh toán");


            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/InvoiceDetailView.fxml"));
            Parent root = fxmlLoader.load();

            InvoiceDetailController controller = fxmlLoader.getController();
            controller.setViewModelForCreation(invoiceDetailVM);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            handleClose(null);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) selectRoomForCheckOutVBox.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

}
