package com.example.hotelmanagement.Views;

import com.example.hotelmanagement.ViewModels.BookingViewModel;
import com.example.hotelmanagement.ViewModels.ConfirmBookingCodeViewModel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ConfirmBookingCodeController {
    @FXML private Label message;
    @FXML private MFXTextField confirmCodeTextField;
    @FXML private VBox confirmBookingCodeVBox;

    ConfirmBookingCodeViewModel viewModel;

    public void setViewModel(ConfirmBookingCodeViewModel viewModel) {
        this.viewModel = viewModel;
        bindData();
    }

    private void bindData() {
        message.setVisible(false);
    }

    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) confirmBookingCodeVBox.getScene().getWindow();
        stage.close();
    }

    public void handleConfirm(MouseEvent mouseEvent) {
        boolean isExist = viewModel.hasActivePrebooking(confirmCodeTextField.getText());
        if (isExist) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/hotelmanagement/Views/BookingView.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/CSS/reservation-style.css").toExternalForm());
                BookingViewModel vm = new BookingViewModel(viewModel.getRoom(), false);
                vm.setParent(viewModel.getParent());
                BookingController controller = fxmlLoader.getController();
                controller.setViewModel(vm);
                Stage stage = new Stage();
                stage.initStyle(StageStyle.UNDECORATED);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();

                Stage stage1 = (Stage) confirmBookingCodeVBox.getScene().getWindow();
                stage1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            message.setVisible(true);
        }
    }
}
