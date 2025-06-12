package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.DAO.RoomDAO;
import com.example.hotelmanagement.DTO.Reservation_RoomDisplay;
import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.ViewModels.BookingInAdvanceInvoiceViewModel;
import com.example.hotelmanagement.ViewModels.BookingInAdvanceViewModel;
import com.example.hotelmanagement.ViewModels.BookingViewModel;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import lombok.Setter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class BookingInAdvanceController implements Initializable {
    @FXML private MFXTextField addressTextField;
    @FXML private MFXTextField phoneNumberTextField;
    @FXML private MFXTextField customerNameTextField;
    @FXML private MFXDatePicker checkOutPicker;
    @FXML private MFXDatePicker checkInPicker;
    @FXML private TilePane tilePane;
    @FXML
    private VBox BookingInAdvanceVBox;

    @Setter
    BookingInAdvanceViewModel viewModel;

    private void chonItem(VBox item) {
        if (viewModel.getSelectedItem() != null) {
            viewModel.getSelectedItem().getStyleClass().remove("room-container-selected");
        }
        viewModel.setSelectedItem(item);
        viewModel.getSelectedItem().getStyleClass().add("room-container-selected");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale vietnameseLocale = new Locale("vi", "VN");
        Locale.setDefault(vietnameseLocale);

        Platform.runLater(() -> BookingInAdvanceVBox.requestFocus());

        setupMFXDatePicker(checkOutPicker);
        setupMFXDatePicker(checkInPicker);
    }

    public void initializeRoomDisplays() {
        tilePane.getChildren().clear();

        for (Reservation_RoomDisplay phong : viewModel.getRoomReservationDisplays()) {
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
            Text loaiPhong = new Text("Loại phòng: " + phong.getRoomType().getTypeName());
            loaiPhong.setFont(Font.font(null, FontWeight.MEDIUM, 15));

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            Text gia = new Text("Giá: " + formatter.format(phong.getRoomType().getBasePrice()) + " đồng/đêm");
            gia.setFont(Font.font(15));

            info.getChildren().addAll(loaiPhong, gia);
            info.setStyle("-fx-padding: 5 10 5 10;");

            container.getChildren().addAll(header, info);
            container.setOnMouseClicked(event -> chonItem(container));
            container.setUserData(phong);
            tilePane.getChildren().add(container);
        }
    }

    public void setViewModel(BookingInAdvanceViewModel viewModel) {
        this.viewModel = viewModel;
        Platform.runLater(this::initializeRoomDisplays);
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

            datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                viewModel.getRoomReservationDisplays().clear();
                initializeRoomDisplays();
                viewModel.setSelectedItem(null);
            });
        }
    }

    public void handleFindRooms(MouseEvent mouseEvent) {
        if (viewModel != null) {
            viewModel.setCheckInDate(checkInPicker.getValue());
            viewModel.setCheckOutDate(checkOutPicker.getValue());

            if (viewModel.getCheckInDate() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng chọn ngày nhận phòng");
                alert.showAndWait();
                return;
            }

            if (viewModel.getCheckOutDate() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng chọn ngày trả phòng");
                alert.showAndWait();
                return;
            }

            if (viewModel.getCheckInDate().isBefore(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Ngày nhận phòng không được trước ngày hôm nay");
                alert.showAndWait();
                return;
            }

            if (viewModel.getCheckOutDate().isBefore(viewModel.getCheckInDate())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Ngày trả phòng không được trước ngày nhận phòng");
                alert.showAndWait();
                return;
            }

            viewModel.findRoom();
            initializeRoomDisplays();
        }
    }

    public void nextView(MouseEvent mouseEvent) {
        if (!checkInPicker.getValue().equals(LocalDate.now())) {
            if (customerNameTextField.getText().isEmpty() || addressTextField.getText().isEmpty() || phoneNumberTextField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập đầy đủ tất cả các thông tin bắt buộc");
                alert.showAndWait();
                return;
            }

            if (!phoneNumberTextField.getText().matches("\\d+")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Số điện thoại phải là chuỗi số");
                alert.showAndWait();
                return;
            }

            try {
                FXMLLoader bookingNotefxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingInAdvanceInvoiceView.fxml"));
                Parent root = bookingNotefxmlLoader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
                if (viewModel.getSelectedItem().getUserData() instanceof Reservation_RoomDisplay roomReservationDisplay) {
                    Room room = new RoomDAO().findById(roomReservationDisplay.getId());
                    BookingInAdvanceInvoiceViewModel vm = new BookingInAdvanceInvoiceViewModel(room, viewModel.getCheckInDate(), viewModel.getCheckOutDate(), customerNameTextField.getText(), addressTextField.getText(), phoneNumberTextField.getText());
                    vm.setParent(viewModel);
                    BookingInAdvanceInvoiceController controller = bookingNotefxmlLoader.getController();
                    controller.setViewModel(vm);
                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setScene(scene);
                    stage.showAndWait();
                    viewModel.findRoom();
                    initializeRoomDisplays();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingView.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
                if (viewModel.getSelectedItem().getUserData() instanceof Reservation_RoomDisplay roomReservationDisplay) {
                    Room room = new RoomDAO().findById(roomReservationDisplay.getId());
                    BookingViewModel vm = new BookingViewModel(room, false);
                    vm.getCheckOutDate().set(checkOutPicker.getValue().atStartOfDay(ZoneOffset.UTC).toLocalDate());
                    vm.setParent(viewModel.getParent());
                    BookingController controller = fxmlLoader.getController();
                    controller.setViewModel(vm);
                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setScene(scene);
                    stage.showAndWait();
                    viewModel.getRoomReservationDisplays().removeIf(r -> r.getId() == room.getId());
                    viewModel.setSelectedItem(null);
                    viewModel.findRoom();
                    initializeRoomDisplays();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}